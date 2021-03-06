// contracts/GameCoin.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";


contract GameCoin is ERC20, Ownable {

    using SafeMath for uint256;
    constructor(uint256 initialSupply) ERC20("GameCoin", "gamecoin") {
        _mint(msg.sender, initialSupply * (10 ** uint256(decimals())));
    }

    //订单实体
    struct GameItems{
        //唯一id
        uint256 id;
        //数量
        uint256 amount;
        //价格gamecoin
        uint256 price;
        //时间
        uint256 leftAmount;
    }
    mapping (uint256 => GameItems) public itemMap;
    mapping (address => mapping (uint256 => uint256)) public userItemMap;

    //新增一种类型的游戏道具
    function addItem(uint256 id,uint256 price,uint256 amount) public onlyOwner {
        GameItems memory newItem=GameItems({
        amount:amount,
        leftAmount:amount,
        price:price,
        id:id
        });
        itemMap[id]=newItem;
    }

    //道具转让给另一个人
    function transferItem(uint256 id,address from ,address to,uint256 price,uint256 count)  public onlyOwner
    {
        require(userItemMap[from][id]>=count);
        _transfer(to , from ,price.mul(count));
        userItemMap[from][id] = userItemMap[from][id]-count;
        userItemMap[to][id] = userItemMap[to][id]+count;
    }

    //个人购买道具批量
    function buyItems( uint256[]calldata ids , uint256 []calldata counts ) public
    {
        require(ids.length == counts.length);
        uint256 allAmount = 0;
        for (uint i = 0; i < ids.length; i++) {
            uint256 idI = ids[i];
            require(counts[i] <= itemMap[idI].leftAmount);
            allAmount  = counts[i].mul(itemMap[idI].price) + allAmount;
            itemMap[idI].leftAmount = itemMap[idI].leftAmount - counts[i];
            userItemMap[msg.sender][idI] = userItemMap[msg.sender][idI] + counts[i];
        }
        _burn(msg.sender,allAmount);
    }

    //管理员直接发nft
    function sendItem( uint256 idI ,uint256 count,address to ) public onlyOwner
    {
        require(count <= itemMap[idI].leftAmount);
        itemMap[idI].leftAmount = itemMap[idI].leftAmount - count;
        userItemMap[to][idI] = userItemMap[to][idI] + count;
    }

    function queryItem(address addr,uint256 id) public view returns ( uint256  )
    {
        return userItemMap[addr][id];
    }


    //官方价格 1 chr-token = onePrice个 GameCoin
    uint256 public onePrice = 100;
    //手续费除数，千分之二除以500
    uint256 public taxRate = 500;
    //传入的gamecoin价格保留4位小数，传入最小值10000，代表1
    uint256 public scale = 10000;
    //订单实体
    struct OrderEntity{
        //gamecoin数量
        uint256 amount;
        //chr数量
        uint256 chr;
        //价格
        uint256 price;
        //时间
        uint256 time;
        //先挂单的用户地址
        address sender;
        //1用chrToken换gamecoin，反之2
        uint256 sale;
        //后挂单的用户地址
        address to;
        //手续费
        uint256 taxFee;
    }
    //出售gamecoin数组
    OrderEntity [] public   saleOrdersArray;
    //购买gamecoin数组
    OrderEntity [] public   buyOrdersArray;
    OrderEntity [] public   saleOrdersArrayCache;
    OrderEntity [] public   buyOrdersArrayCache;
    OrderEntity [] public   historyOrders;

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
        require(amount>0 && myprice>=scale);
        require(balanceOf(msg.sender) >= amount);
        //先扣gamecoin，取消订单加回去
        _burn(msg.sender, amount);
        OrderEntity memory newOrder = OrderEntity({
        amount:amount,
        chr:0,
        price:myprice,
        time:block.timestamp,
        sender:msg.sender,
        sale:2,
        to:msg.sender,
        taxFee:0
        });
        saleOrdersArrayCache.push(newOrder);

    }
    //取消挂单，出售gamecoin
    function cancelSaleOrder(uint256 time)  public
    {
        uint256 index = 0;
        uint256 amount = 0;
        for (uint i = 0; i < saleOrdersArray.length; i++) {
            if( time == saleOrdersArray[i].time
                && saleOrdersArray[i].sender == msg.sender){
                amount = saleOrdersArray[i].amount;
                index = i;
                break;
            }
        }
        deleteOne(saleOrdersArray,index);
        //取消挂单，加回去gamecoin
        _mint(msg.sender,amount);
    }



    //个人挂单,购买gamecoin
    function addBuyOrder(uint256 myprice )  public payable
    {
        uint256 amount = msg.value;
        require(msg.value>0 && myprice>=scale);
        OrderEntity memory newOrder=OrderEntity({
        amount:0,
        chr:amount,
        price:myprice,
        time:block.timestamp,
        sender:msg.sender,
        sale:1,
        to:msg.sender,
        taxFee:0
        });
        buyOrdersArrayCache.push(newOrder);
    }

    //取消挂单，购买gamecoin
    function cancelBuyOrder(uint256 time)  public
    {
        uint256 index = 0;
        uint256 amount=0;
        for (uint i = 0; i < buyOrdersArray.length; i++) {
            if( time == buyOrdersArray[i].time
                && buyOrdersArray[i].sender == msg.sender){
                amount = buyOrdersArray[i].chr;
                index = i;
                break;
            }
        }
        deleteOne(buyOrdersArray,index);
        address payable receiver= payable(msg.sender);
        receiver.transfer(amount);
    }


    //撮合挂单
    function matchOrder()  public  payable
    {
        if(saleOrdersArrayCache.length!=0){
            insertOne(saleOrdersArray,saleOrdersArrayCache[0],2);
            deleteOne(saleOrdersArrayCache,0);
        }
        if(buyOrdersArrayCache.length!=0){
            insertOne(buyOrdersArray,buyOrdersArrayCache[0],1);
            deleteOne(buyOrdersArrayCache,0);
        }
        if(buyOrdersArray.length==0||saleOrdersArray.length==0){
            return;
        }
        if(buyOrdersArray[0].time<saleOrdersArray[0].time){
            matchBuyOrder();
            matchSaleOrder();
        }else{
            matchSaleOrder();
            matchBuyOrder();
        }
    }

    //撮合出售gamecoin挂单
    function matchSaleOrder()  public  payable
    {
        if(buyOrdersArray.length==0||saleOrdersArray.length==0){
            return;
        }
        uint256 gamecoinPayed = saleOrdersArray[0].amount;
        uint256 price = saleOrdersArray[0].price;
        uint256 chrGet = 0;
        //由于i=0位置的订单如果匹配上了价格和数量则会删除
        //出价小于订单价则无法成交
        while (buyOrdersArray.length>0
        && price >= buyOrdersArray[0].price
            && gamecoinPayed !=0 ) {
            //当前订单对应的gamecoin总数
            uint256 chrAmount = buyOrdersArray[0].chr;
            uint256 chrPrice = buyOrdersArray[0].price;
            uint256 chrWant = gamecoinPayed.mul(scale).div(chrPrice);
            if(chrAmount>chrWant){
                //当前订单总数充足，则部分成交
                uint256 chrLeft = chrAmount.sub(gamecoinPayed.mul(scale).div(buyOrdersArray[0].price));
                //千分之2手续费
                uint256 taxFeeOne = gamecoinPayed.div(taxRate);
                //发gamecoin给匹配到的买家
                _mint(buyOrdersArray[0].sender,gamecoinPayed.sub(taxFeeOne));
                //累加chrGet获得
                chrGet = chrGet.add(gamecoinPayed.mul(scale).div(buyOrdersArray[0].price));
                OrderEntity memory newOrderHistory = OrderEntity({
                amount:gamecoinPayed,
                chr:chrGet,
                price:buyOrdersArray[0].price,
                time:block.timestamp,
                sender:buyOrdersArray[0].sender,
                sale:buyOrdersArray[0].sale,
                to:saleOrdersArray[0].sender,
                taxFee:taxFeeOne
                });
                historyOrders.push(newOrderHistory);
                gamecoinPayed = 0;
                buyOrdersArray[0].chr = chrLeft;
                break;
            }else{
                uint gamecoinAmount = chrAmount.mul(chrPrice).div(scale);
                //千分之2手续费
                uint256 taxFeeOne = gamecoinAmount.div(taxRate);
                //发gamecoin给匹配到的买家
                _mint(buyOrdersArray[0].sender,gamecoinAmount.sub(taxFeeOne));
                //累加chrGet获得
                chrGet = chrGet.add(chrAmount);
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinAmount,
                chr:chrGet,
                price:buyOrdersArray[0].price,
                time:block.timestamp,
                sender:buyOrdersArray[0].sender,
                sale:buyOrdersArray[0].sale,
                to:saleOrdersArray[0].sender,
                taxFee:taxFeeOne
                });
                historyOrders.push(newOrderHistory);
                //删除消耗掉的这个订单
                deleteOne(buyOrdersArray,0);
                //当前订单总数不足，则消耗完该订单继续循环
                gamecoinPayed = gamecoinPayed.sub(gamecoinAmount);
            }
        }
        if(chrGet==0){
            return;
        }
        //千分之2手续费
        uint256 taxFee = chrGet.div(taxRate);
        //加chr
        payable(msg.sender).transfer(chrGet.sub(taxFee));
        if(gamecoinPayed != 0){
            //剩余的金额不为0，则剩余一部分未成交
            saleOrdersArray[0].amount = gamecoinPayed;
        }else{
            //剩余的金额为0，则删除
            deleteOne(saleOrdersArray,0);
        }
    }
    //撮合购买gamecoin挂单
    function matchBuyOrder()   public  payable
    {
        if(buyOrdersArray.length==0||saleOrdersArray.length==0){
            return;
        }
        uint256 chrPayed = buyOrdersArray[0].chr;
        uint256 price = buyOrdersArray[0].price;
        uint256 gamecoinGet = 0;
        //由于i=0位置的订单如果匹配上了价格和数量则会删除，
        //出价大于订单价则无法成交
        while (saleOrdersArray.length>0
        && price <= saleOrdersArray[0].price
            && chrPayed != 0) {
            //当前订单对应的chr总数
            uint256 gamecoinAmount = saleOrdersArray[0].amount;
            uint256 gamecoinPrice = saleOrdersArray[0].price;
            uint256 gamecoinWant = chrPayed.mul(gamecoinPrice).div(scale);
            if(gamecoinAmount > gamecoinWant){
                //当前订单总数充足，则部分成交
                uint256 gamecoinLeft = gamecoinAmount.sub(gamecoinWant);
                //加chr给卖家
                uint256 taxFeeOne = chrPayed.div(taxRate);
                payable(saleOrdersArray[0].sender).transfer(chrPayed.sub(taxFeeOne));
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinWant,
                chr:chrPayed,
                price:gamecoinPrice,
                time:block.timestamp,
                sender:saleOrdersArray[0].sender,
                sale:saleOrdersArray[0].sale,
                to:buyOrdersArray[0].sender,
                taxFee:taxFeeOne
                });
                historyOrders.push(newOrderHistory);
                //累加获得的数量
                gamecoinGet = gamecoinGet.add(gamecoinWant);
                chrPayed = 0;
                saleOrdersArray[0].amount = gamecoinLeft;
                break;
            }else{
                //加chr给卖家
                uint256 chrAmount = gamecoinAmount.mul(scale).div(gamecoinPrice);
                uint256 taxFeeOne = chrAmount.div(taxRate);
                payable(saleOrdersArray[0].sender).transfer(chrAmount.sub(taxFeeOne));
                //累加获得的数量
                gamecoinGet = gamecoinGet.add(gamecoinAmount);
                //记录历史
                OrderEntity memory newOrderHistory=OrderEntity({
                amount:gamecoinAmount,
                chr:chrAmount,
                price:gamecoinPrice,
                time:block.timestamp,
                sender:saleOrdersArray[0].sender,
                sale:saleOrdersArray[0].sale,
                to:buyOrdersArray[0].sender,
                taxFee:taxFeeOne
                });
                historyOrders.push(newOrderHistory);
                //删除消耗掉的这个订单
                deleteOne(saleOrdersArray,0);
                //当前订单总数不足，则消耗完该订单继续循环
                chrPayed = chrPayed.sub(chrAmount);
            }
        }
        if(gamecoinGet!=0){
            //手续费千分之二
            uint256 taxFee=gamecoinGet.div(taxRate);
            //加gamecoin
            _mint(msg.sender,gamecoinGet.sub(taxFee));
            if(chrPayed != 0){
                //剩余的金额不为0，则部分未成交
                buyOrdersArray[0].chr = chrPayed;
            }else{
                //剩余的金额为0，则删除
                deleteOne(buyOrdersArray,0);
            }
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
    function insertOne(OrderEntity [] storage arr,OrderEntity memory newOrder,uint buyOrSale) private
    {
        if(arr.length==0){
            arr.push(newOrder);
        }else{
            uint index = arr.length;
            for (uint i = 0; i < arr.length; i++) {
                //找到价格合适的位置
                if(buyOrSale==1){
                    if( newOrder.price < arr[i].price){
                        index=i;
                        break;
                    }
                }else{
                    if( newOrder.price > arr[i].price){
                        index=i;
                        break;
                    }
                }
            }
            //复制最后一条并加到结尾
            arr.push(arr[arr.length-1]);
            //把i后面的后移一位
            for (uint i = arr.length-1; i > index; i--) {
                arr[i]=arr[i-1];
            }
            //插入新订单到合适位置
            arr[index]=newOrder;
        }
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