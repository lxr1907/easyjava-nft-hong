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
    //创建所有账户余额数组
    OrderEntity [] public   ordersArray;
    function getOrders() public view returns (uint arr){
        arr= ordersArray.length;
    }
    constructor(uint256 initialSupply) ERC20("GameCoin", "gamecoin") {
        _mint(msg.sender, initialSupply * (10 ** uint256(decimals())));
    }

    receive() external payable
    {
        uint256 amount = msg.value;
        _mint(msg.sender, amount * onePrice);
    }
    //个人挂单,我的出价和数量
     function addOrder(uint256 amount, uint256 myprice )  external
    {
        require(balanceOf(msg.sender) >= amount);
        _burn(msg.sender, amount);
        if(ordersArray.length==0){
            ordersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
            return;
        }
        bool startMove = false;
        for (uint i = 0; i < ordersArray.length; i++) {
            if(startMove){
                OrderEntity storage  afterOrder = ordersArray[i];
                ordersArray[i] = cheapOrder;
                cheapOrder = afterOrder;
            }else{
                cheapOrder = ordersArray[i];
                if( myprice > cheapOrder.price){
                    ordersArray[i] =  OrderEntity({
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
            ordersArray.push(OrderEntity({
                                        amount:amount,
                                        price:myprice,
                                        time:block.timestamp,
                                        sender:msg.sender
                                    }));
        }else{
            ordersArray.push(cheapOrder);
        }
    }

      //取消挂单
     function cancelOrder(uint256 time)  external
    {
        bool startMove = false;
        uint256 amount=0;
        for (uint i = 0; i < ordersArray.length; i++) {
            if(startMove){
                    if(i+1< ordersArray.length){
                    ordersArray[i] = ordersArray[i+1];
                    }
            }else{
                if( time == ordersArray[i].time
                  && ordersArray[i].sender == msg.sender){
                      amount=ordersArray[i].amount;
                    if(i+1< ordersArray.length){
                        ordersArray[i] = ordersArray[i+1];
                    }
                    startMove = true;
                }
            }
        }
        delete ordersArray[ordersArray.length-1];
        _mint(msg.sender,amount);
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