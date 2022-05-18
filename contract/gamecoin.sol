// contracts/GameCoin.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";


contract GameCoin is ERC20, Ownable {

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
    function getSaleOrders() public view returns (uint arr){
        arr= saleOrdersArray.length;
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
     function addSaleOrder(uint256 amount, uint256 myprice )  external
    {
        require(balanceOf(msg.sender) >= amount);
        //先扣gamecoin，取消订单加回去
        _burn(msg.sender, amount);
        if(saleOrdersArray.length==0){
            saleOrdersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
            return;
        }
        bool startMove = false;
        for (uint i = 0; i < saleOrdersArray.length; i++) {
            if(startMove){
                OrderEntity storage  afterOrder = saleOrdersArray[i];
                saleOrdersArray[i] = cheapOrder;
                cheapOrder = afterOrder;
            }else{
                cheapOrder = saleOrdersArray[i];
                if( myprice > cheapOrder.price){
                    saleOrdersArray[i] =  OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    });
                    startMove = true;
                }
            }
        }

        if(!startMove){
            saleOrdersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
        }else{
            saleOrdersArray.push(cheapOrder);
        }
    }

      //取消挂单，出售gamecoin
     function cancelSaleOrder(uint256 time)  external
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
     function addBuyOrder(uint256 myprice )  external payable
    {
        uint256 amount = msg.value;
        if(buyOrdersArray.length==0){
            buyOrdersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
            return;
        }
        bool startMove = false;
        for (uint i = 0; i < buyOrdersArray.length; i++) {
            if(startMove){
                OrderEntity storage  afterOrder = buyOrdersArray[i];
                buyOrdersArray[i] = buyHighOrder;
                buyHighOrder = afterOrder;
            }else{
                buyHighOrder = buyOrdersArray[i];
                if( myprice < buyHighOrder.price){
                    buyOrdersArray[i] =  OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    });
                    startMove = true;
                }
            }
        }

        if(!startMove){
            buyOrdersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
        }else{
            buyOrdersArray.push(buyHighOrder);
        }
    }

      //取消挂单，购买gamecoin
     function cancelBuyOrder(uint256 time)  external
    {
        bool startMove = false;
        uint256 amount=0;
        for (uint i = 0; i < buyOrdersArray.length; i++) {
            if(startMove){
                    if(i+1< buyOrdersArray.length){
                    buyOrdersArray[i] = buyOrdersArray[i+1];
                    }
            }else{
                if( time == buyOrdersArray[i].time
                  && buyOrdersArray[i].sender == msg.sender){
                      amount=buyOrdersArray[i].amount;
                    if(i+1< saleOrdersArray.length){
                        buyOrdersArray[i] = buyOrdersArray[i+1];
                    }
                    startMove = true;
                }
            }
        }
        delete buyOrdersArray[buyOrdersArray.length-1];
        msg.sender.transfer(amount);
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