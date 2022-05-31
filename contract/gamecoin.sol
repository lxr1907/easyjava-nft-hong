// contracts/GameCoin.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";


contract GameCoin is ERC20, Ownable {

    using SafeMath for uint256;
    //官方价格 1 chr-token = onePrice个 GameCoin
    uint256 public onePrice = 100;
    //手续费除数，千分之二除以500
    uint256 public taxRate = 500;
    //订单实体
    struct OrderEntity{
        //数量
        uint256 amount;
        //价格
        uint256 price;
        //时间
        uint256 time;
        address sender;
        //1用chrToken换gamecoin，反之2
        uint256 sale;
        address to;
    }
    //出售gamecoin数组
    OrderEntity [] public   saleOrdersArray;
    //购买gamecoin数组
    OrderEntity [] public   buyOrdersArray;
    OrderEntity [] public   historyOrders;
    constructor(uint256 initialSupply) ERC20("GameCoin", "gamecoin") {
        _mint(msg.sender, initialSupply * (10 ** uint256(decimals())));
    }

    function getSaleOrders() public view returns (OrderEntity [] memory ){
        return saleOrdersArray;
    }

    function getBuyOrders() public view returns (OrderEntity [] memory ){
        return buyOrdersArray;
    }

    function getHistoryOrders() public view returns (OrderEntity [] memory ){
        return historyOrders;
    }

    //个人挂单,出售gamecoin
    function addSaleOrder(uint256 amount, uint256 myprice )  public payable
    {
        require(amount>0 && myprice>0);
        require(balanceOf(msg.sender) >= amount);
        //先扣gamecoin，取消订单加回去
        _burn(msg.sender, amount);
        OrderEntity memory newOrder = OrderEntity({
        amount:amount,
        price:myprice,
        time:block.timestamp,
        sender:msg.sender,
        sale:2,
        to:msg.sender
        });
        if(saleOrdersArray.length==0){
            saleOrdersArray.push(newOrder);
        }else{
            uint index=saleOrdersArray.length;
            for (uint i = 0; i < saleOrdersArray.length; i++) {
                if( myprice > saleOrdersArray[i].price){
                    index=i;
                    break;
                }
            }
            //复制最后一条并加到结尾
            saleOrdersArray.push(saleOrdersArray[saleOrdersArray.length-1]);
            //把i后面的后移一位
            for (uint i = saleOrdersArray.length-1; i > index; i--) {
                saleOrdersArray[i]=saleOrdersArray[i-1];
            }
            saleOrdersArray[index]=newOrder;
        }
        matchSaleOrder();
    }
    //取消挂单，出售gamecoin
    function cancelSaleOrder(uint256 time)  public
    {
        bool startMove = false;
        uint256 amount=0;
        for (uint i = 0; i < saleOrdersArray.length; i++) {
            if(startMove){
                if(i+1< saleOrdersArray.length){
                    saleOrdersArray[i] = saleOrdersArray[i+1];
                }
            }else{
                if( time == saleOrdersArray[i].time
                    && saleOrdersArray[i].sender == msg.sender){
                    amount=saleOrdersArray[i].amount;
                    if(i+1< saleOrdersArray.length){
                        saleOrdersArray[i] = saleOrdersArray[i+1];
                    }
                    startMove = true;
                }
            }
        }
        saleOrdersArray.pop();
        //取消挂单，加回去gamecoin
        _mint(msg.sender,amount);
    }



    //个人挂单,购买gamecoin
    function addBuyOrder(uint256 myprice )  public payable
    {
        uint256 amount = msg.value;
        require(msg.value>0 && myprice>0);
        OrderEntity memory newOrder=OrderEntity({
        amount:amount.mul(myprice),
        price:myprice,
        time:block.timestamp,
        sender:msg.sender,
        sale:1,
        to:msg.sender
        });
        if(buyOrdersArray.length==0){
            buyOrdersArray.push(newOrder);
        }else{
            uint index = buyOrdersArray.length;
            for (uint i = 0; i < buyOrdersArray.length; i++) {
                if( myprice < buyOrdersArray[i].price){
                    index=i;
                    break;
                }
            }
            //复制最后一条并加到结尾
            buyOrdersArray.push(buyOrdersArray[buyOrdersArray.length-1]);
            //把i后面的后移一位
            for (uint i = buyOrdersArray.length-1; i > index; i--) {
                buyOrdersArray[i]=buyOrdersArray[i-1];
            }
            buyOrdersArray[index]=newOrder;
        }
        matchBuyOrder();
    }

    //取消挂单，购买gamecoin
    function cancelBuyOrder(uint256 time)  public
    {
        bool startMove = false;
        uint256 amount=0;
        uint256 price=1;
        for (uint i = 0; i < buyOrdersArray.length; i++) {
            if(startMove){
                if(i+1< buyOrdersArray.length){
                    buyOrdersArray[i] = buyOrdersArray[i+1];
                }
            }else{
                if( time == buyOrdersArray[i].time
                    && buyOrdersArray[i].sender == msg.sender){
                    amount=buyOrdersArray[i].amount;
                    price=buyOrdersArray[i].price;
                    if(i+1< saleOrdersArray.length){
                        buyOrdersArray[i] = buyOrdersArray[i+1];
                    }
                    startMove = true;
                }
            }
        }
        buyOrdersArray.pop();
        address payable receiver= payable(msg.sender);
        receiver.transfer(amount.div(price));
    }


    //撮合出售gamecoin挂单
    function matchSaleOrder()  public  payable
    {
        uint256 gamecoinPayed=saleOrdersArray[0].amount;
        uint256 price=saleOrdersArray[0].price;
        uint256 chrGet = 0;
        //由于i=0位置的订单如果匹配上了价格和数量则会删除
        //出价小于订单价则无法成交
        while (buyOrdersArray.length>0
            && price>=buyOrdersArray[0].price) {
            //当前订单对应的gamecoin总数
            uint256 gamecoinAmount=buyOrdersArray[0].amount;
            if(gamecoinAmount>gamecoinPayed){
                //当前订单总数充足，则部分成交
                uint256 gamecoinAmountLeft=gamecoinAmount.sub(gamecoinPayed);
                //千分之2手续费
                uint256 taxFee = gamecoinAmount.div(taxRate);
                //发gamecoin给匹配到的买家
                _mint(buyOrdersArray[0].sender,gamecoinAmount.sub(taxFee));
                //累加chrGet获得
                chrGet = chrGet.add(gamecoinPayed.div(buyOrdersArray[0].price));
                buyOrdersArray[0].amount = gamecoinAmountLeft;
                OrderEntity memory newOrderHistory = OrderEntity({
                amount:gamecoinPayed,
                price:buyOrdersArray[0].price,
                time:block.timestamp,
                sender:buyOrdersArray[0].sender,
                sale:buyOrdersArray[0].sale,
                to:saleOrdersArray[0].sender
                });
                historyOrders.push(newOrderHistory);
                gamecoinPayed=0;
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinPayed = gamecoinPayed.sub(gamecoinAmount);
                //千分之2手续费
                uint256 taxFee = gamecoinAmount.div(taxRate);
                //发gamecoin给匹配到的买家
                _mint(buyOrdersArray[0].sender,gamecoinAmount.sub(taxFee));
                //累加chrGet获得
                chrGet = chrGet.add(gamecoinAmount.div(buyOrdersArray[0].price));
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinAmount,
                price:buyOrdersArray[0].price,
                time:block.timestamp,
                sender:buyOrdersArray[0].sender,
                sale:buyOrdersArray[0].sale,
                to:saleOrdersArray[0].sender
                });
                historyOrders.push(newOrderHistory);
                //删除消耗掉的这个订单
                deleteOne(buyOrdersArray,0);
            }
        }
        //千分之2手续费
        uint256 taxFee = chrGet.div(taxRate);
        //加chr
        payable(msg.sender).transfer(chrGet.sub(taxFee));
        if(gamecoinPayed!=0){
            //剩余的金额不为0，则剩余一部分未成交
            saleOrdersArray[0].amount=gamecoinPayed;
        }else{
            //剩余的金额为0，则删除
            deleteOne(saleOrdersArray,0);
        }
    }
    //撮合购买gamecoin挂单
    function matchBuyOrder()   public  payable
    {
        uint256 gamecoinWant = buyOrdersArray[0].amount;
        uint256 price = buyOrdersArray[0].price;
        uint256 gamecoinGet=0;
        //由于i=0位置的订单如果匹配上了价格和数量则会删除，
        //出价大于订单价则无法成交
        while (saleOrdersArray.length>0
            && price<=saleOrdersArray[0].price) {
            //当前订单对应的chr总数
            uint256 gamecoinAmount = saleOrdersArray[0].amount;
            if(gamecoinAmount > gamecoinWant){
                //当前订单总数充足，则部分成交
                uint256 gamecoinLeft = gamecoinAmount.sub(gamecoinWant);
                saleOrdersArray[0].amount = gamecoinLeft;
                //加chr给卖家
                uint256 chrAmount=gamecoinAmount.div(saleOrdersArray[0].price);
                uint256 taxFee=chrAmout.div(taxRate);
                payable(saleOrdersArray[0].sender).transfer(chrAmount.sub(taxFee));
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinWant,
                price:saleOrdersArray[0].price,
                time:block.timestamp,
                sender:saleOrdersArray[0].sender,
                sale:saleOrdersArray[0].sale,
                to:buyOrdersArray[0].sender
                });
                historyOrders.push(newOrderHistory);
                //累加获得的数量
                gamecoinGet = gamecoinGet.add(gamecoinWant);
                gamecoinWant=0;
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinWant = gamecoinWant.sub(gamecoinAmount);
                //加chr给卖家
                uint256 chrAmount=gamecoinAmount.div(saleOrdersArray[0].price);
                uint256 taxFee=chrAmout.div(taxRate);
                payable(saleOrdersArray[0].sender).transfer(chrAmount.sub(taxFee));
                //累加获得的数量
                gamecoinGet = gamecoinGet.add(gamecoinAmount);
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinAmount,
                price:saleOrdersArray[0].price,
                time:block.timestamp,
                sender:saleOrdersArray[0].sender,
                sale:saleOrdersArray[0].sale,
                to:buyOrdersArray[0].sender
                });
                historyOrders.push(newOrderHistory);
                //删除消耗掉的这个订单
                deleteOne(saleOrdersArray,0);
            }
        }
        //手续费千分之二
        uint256 taxFee=gamecoinGet.div(taxRate);
        //加gamecoin
        _mint(msg.sender,gamecoinGet.sub(taxFee));
        if(gamecoinWant!=0){
            //剩余的金额不为0，则部分未成交
            buyOrdersArray[0].amount=gamecoinWant;
        }else{
            //剩余的金额为0，则删除
            deleteOne(buyOrdersArray,0);
        }
    }

    function deleteOne(OrderEntity [] storage arr,uint index) private
    {
        if (index >= arr.length || arr.length == 0)
            return;
        for (uint i = index; i < arr.length-1; i++) {
            arr[i].amount = arr[i+1].amount;
            arr[i].price = arr[i+1].price;
            arr[i].time = arr[i+1].time;
            arr[i].sender = arr[i+1].sender;
        }
        arr.pop();
    }

    function deleteOneBuyOrder(uint index) public onlyOwner
    {
        deleteOne(buyOrdersArray,index);
    }

    function deleteOneSaleOrder(uint index) public onlyOwner
    {
        deleteOne(saleOrdersArray,index);
    }
    //消耗掉gamecoin
    function burn(uint256 amount, address receiver)
    public onlyOwner {
        require(balanceOf(receiver) >= amount);
        _burn(receiver, amount);
    }

    function getBalance() public view
    returns (uint256 balance){
        balance = address(this).balance;
    }
    //修改价格
    function setPrice(uint256 price) public onlyOwner {
        onePrice = price;
    }
    //修改价格
    function setTax(uint256 rate) public onlyOwner {
        taxRate = rate;
    }
}