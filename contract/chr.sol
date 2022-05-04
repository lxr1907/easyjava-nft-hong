// contracts/CHRToken.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract CHRToken is ERC20, Ownable {

    //价格 1 klay = onePrice个 chr
    uint256 public onePrice = 1;
    constructor(uint256 initialSupply) ERC20("chr", "CHR") {
        _mint(msg.sender, initialSupply * (10 ** uint256(decimals())));
    }

    receive() external payable
    {
        uint256 amount = msg.value;
        _mint(msg.sender, amount * onePrice);
    }
    //提现chr兑换为klay
    function withDraw(uint256 amount, address payable receiver)
    payable
    public onlyOwner {
        require(balanceOf(receiver) >= amount);
        _burn(receiver, amount);
        receiver.transfer(amount / onePrice);
    }
    //消耗掉chr，比如购买game coin时调用
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