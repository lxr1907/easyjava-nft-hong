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
    //订单实体
    struct OrderEntity{
        //数量
        uint256 amount;
        //价格
        uint256 price;
        //时间
        uint256 time;
        address sender;
    }
    OrderEntity cheapOrder;
    OrderEntity buyHighOrder;
    //出售gamecoin数组
    OrderEntity [] public   saleOrdersArray;
    //购买gamecoin数组
    OrderEntity [] public   buyOrdersArray;
    OrderEntity [] public arrTop;
    function getSaleOrders() public view returns (OrderEntity [] memory ){
        return saleOrdersArray;
    }

    function getBuyOrders() public view returns (OrderEntity [] memory ){
        return buyOrdersArray;
    }
    constructor(uint256 initialSupply) ERC20("GameCoin", "gamecoin") {
        _mint(msg.sender, initialSupply * (10 ** uint256(decimals())));
    }

    receive() external payable
    {
        uint256 amount = msg.value;
        _mint(msg.sender, amount * onePrice);
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
        sender:msg.sender
        });
        if(saleOrdersArray.length==0){
            saleOrdersArray.push(newOrder);
        }else{
            bool startMove = false;
            for (uint i = 0; i < saleOrdersArray.length; i++) {
                if(startMove){
                    OrderEntity storage  afterOrder = saleOrdersArray[i];
                    saleOrdersArray[i] = cheapOrder;
                    cheapOrder = afterOrder;
                }else{
                    cheapOrder = saleOrdersArray[i];
                    if( myprice > cheapOrder.price){
                        saleOrdersArray[i] =  newOrder;
                        startMove = true;
                    }
                }
            }

            if(!startMove){
                saleOrdersArray.push(newOrder);
            }else{
                saleOrdersArray.push(cheapOrder);
            }
        }
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
        delete saleOrdersArray[saleOrdersArray.length-1];
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
        sender:msg.sender
        });
        if(buyOrdersArray.length==0){
            buyOrdersArray.push(newOrder);
        }else{
            bool startMove = false;
            for (uint i = 0; i < buyOrdersArray.length; i++) {
                if(startMove){
                    OrderEntity storage  afterOrder = buyOrdersArray[i];
                    buyOrdersArray[i] = buyHighOrder;
                    buyHighOrder = afterOrder;
                }else{
                    buyHighOrder = buyOrdersArray[i];
                    if( myprice < buyHighOrder.price){
                        buyOrdersArray[i] =  newOrder;
                        startMove = true;
                    }
                }
            }

            if(!startMove){
                buyOrdersArray.push(newOrder);
            }else{
                buyOrdersArray.push(buyHighOrder);
            }
        }
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
        delete buyOrdersArray[buyOrdersArray.length-1];
        address payable receiver= payable(msg.sender);
        receiver.transfer(amount.div(price));
    }


    //撮合出售gamecoin挂单
    function matchSaleOrder()  public  payable
    {
        uint256 gamecoinPayed=saleOrdersArray[0].amount;
        uint256 price=saleOrdersArray[0].price;

        for (uint i = 0; i < buyOrdersArray.length &&i>=0; i++) {
            if(price<buyOrdersArray[i].price){
                //出价小于订单价则无法成交
                break;
            }
            //当前订单对应的chr总数
            uint256 gamecoinAmount=buyOrdersArray[i].amount;
            if(gamecoinAmount>gamecoinPayed){
                //当前订单总数充足，则部分成交
                uint256 gamecoinAmountLeft=gamecoinAmount.sub(gamecoinPayed);
                uint256 chrGet=gamecoinPayed.div(buyOrdersArray[i].price);
                buyOrdersArray[i].amount=gamecoinAmountLeft;
                gamecoinPayed=0;
                //加chr
                payable(msg.sender).transfer(chrGet);
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinPayed = gamecoinPayed.sub(gamecoinAmount);
                uint256 chrGet=gamecoinAmount.div(buyOrdersArray[i].price);
                //删除消耗掉的这个订单
                deleteOne(buyOrdersArray,i);
                if(i!=0){
                    i=i-1;
                }
                //加chr
                payable(msg.sender).transfer(chrGet);
            }
        }
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
        for (uint i = 0; i < saleOrdersArray.length &&i>=0; i++) {
            if(price>saleOrdersArray[i].price){
                //出价大于订单价则无法成交
                break;
            }
            //当前订单对应的chr总数
            uint256 gamecoinAmount=saleOrdersArray[i].amount;
            if(gamecoinAmount>gamecoinWant){
                //当前订单总数充足，则部分成交
                uint256 gamecoinLeft=gamecoinAmount.sub(gamecoinWant);
                saleOrdersArray[i].amount=gamecoinLeft;
                gamecoinWant=0;
                //加gamecoin
                _mint(msg.sender,gamecoinWant);
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinWant = gamecoinWant.sub(gamecoinAmount);
                uint256 gamecoinGet=saleOrdersArray[i].amount;
                //删除消耗掉的这个订单
                deleteOne(saleOrdersArray,i);
                if(i!=0){
                    i=i-1;
                }
                //加gamecoin
                _mint(msg.sender,gamecoinGet);
            }
        }
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
        if (index >= arr.length) return;
        for (uint i = index; i < arr.length-1; i++) {
            arr[i] = arr[i+1];
        }
        arr.pop();
    }

    //个人市价,出售gamecoin
    function saleOrderNoPrice(uint256 gamecoinPayed)  public
    {
        require(gamecoinPayed>0);
        require(balanceOf(msg.sender) >= gamecoinPayed);
        for (uint i = 0; i < buyOrdersArray.length; i++) {
            //当前订单对应的chr总数
            uint256 gamecoinAmount=buyOrdersArray[i].amount;
            if(gamecoinAmount>gamecoinPayed){
                //当前订单总数充足，则部分成交
                uint256 gamecoinAmountLeft=gamecoinAmount.sub(gamecoinPayed);
                uint256 chrGet=gamecoinAmount.div(buyOrdersArray[i].price);
                buyOrdersArray[i].amount=gamecoinAmountLeft;
                //加chr
                payable(msg.sender).transfer(chrGet);
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinPayed = gamecoinPayed.sub(gamecoinAmount);
                uint256 chrGet=buyOrdersArray[i].amount.div(buyOrdersArray[i].price);
                //删除消耗掉的这个订单
                delete buyOrdersArray[i];
                i=i-1;
                //加chr
                payable(msg.sender).transfer(chrGet);
            }
        }
        if(gamecoinPayed!=0){
            //剩余的金额不为0，则退回
            _mint(msg.sender,gamecoinPayed);
        }
    }
    //个人市价,购买gamecoin
    function buyOrderNoPrice()  public payable
    {
        uint256 chrPayed = msg.value;
        require(msg.value>0);
        for (uint i = 0; i < saleOrdersArray.length; i++) {
            //当前订单对应的chr总数
            uint256 chrAmount=saleOrdersArray[i].amount.div(saleOrdersArray[i].price);
            if(chrAmount>chrPayed){
                //当前订单总数充足，则部分成交
                uint256 chrAmountLeft=chrAmount.sub(chrPayed);
                uint256 gamecoinLeft=chrAmountLeft.mul(saleOrdersArray[i].price);
                uint256 gamecoinGet=saleOrdersArray[i].amount.sub(gamecoinLeft);
                saleOrdersArray[i].amount=gamecoinLeft;
                //加gamecoin
                _mint(msg.sender,gamecoinGet);
                break;
            }else{
                //当前订单总数不足，则消耗完该订单继续循环
                chrPayed = chrPayed.sub(chrAmount);
                uint256 gamecoinGet=saleOrdersArray[i].amount;
                //删除消耗掉的这个订单
                delete saleOrdersArray[i];
                i=i-1;
                //加gamecoin
                _mint(msg.sender,gamecoinGet);
            }
        }
        if(chrPayed!=0){
            //剩余的金额不为0，则退回
            payable(msg.sender).transfer(chrPayed);
        }
    }

    //提现gamecoin兑换为 chr-token
    function withDraw(uint256 amount, address payable receiver)
    payable
    public onlyOwner {
        require(balanceOf(receiver) >= amount);
        _burn(receiver, amount);
        receiver.transfer(amount / onePrice);
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
}