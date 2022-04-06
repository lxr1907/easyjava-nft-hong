// contracts/CHRToken.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/utils/Counters.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract CHRToken is ERC20,Ownable {

    //价格 1 klay = 1 chr
    uint256 public onePrice=1;
    constructor(uint256 initialSupply) ERC20("chr", "CHR") {
        _mint(msg.sender,initialSupply*(10**uint256(decimals())));
    }

    receive () external payable
    {
        uint256 amount = msg.value;
        _mint(msg.sender, amount*onePrice);
    }
    //修改价格
    function setPrice(uint256 price)public onlyOwner{
        onePrice=price;
    }
}