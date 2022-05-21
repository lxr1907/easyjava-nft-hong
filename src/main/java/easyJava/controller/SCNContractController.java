package easyJava.controller;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class SCNContractController {


    public static final String ABI = "[\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"initialSupply\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"constructor\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"anonymous\": false,\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"owner\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"spender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": false,\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"value\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"Approval\",\n" +
            "\t\t\"type\": \"event\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"anonymous\": false,\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"previousOwner\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"newOwner\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"OwnershipTransferred\",\n" +
            "\t\t\"type\": \"event\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"anonymous\": false,\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"from\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": true,\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"to\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"indexed\": false,\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"value\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"Transfer\",\n" +
            "\t\t\"type\": \"event\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"myprice\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"addBuyOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"myprice\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"addSaleOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"owner\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"spender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"allowance\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"spender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"approve\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"bool\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"bool\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"arrTop\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"sender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"account\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"balanceOf\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"receiver\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"burn\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"buyOrderNoPrice\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"buyOrdersArray\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"sender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"cancelBuyOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"cancelSaleOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"decimals\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint8\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint8\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"spender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"subtractedValue\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"decreaseAllowance\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"bool\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"bool\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"getBalance\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"balance\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"getBuyOrders\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"components\": [\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\t\t\"name\": \"sender\",\n" +
            "\t\t\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t],\n" +
            "\t\t\t\t\"internalType\": \"struct GameCoin.OrderEntity[]\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"tuple[]\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"getSaleOrders\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"components\": [\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t{\n" +
            "\t\t\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\t\t\"name\": \"sender\",\n" +
            "\t\t\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t],\n" +
            "\t\t\t\t\"internalType\": \"struct GameCoin.OrderEntity[]\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"tuple[]\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"spender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"addedValue\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"increaseAllowance\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"bool\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"bool\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"matchBuyOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"matchSaleOrder\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"name\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"string\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"string\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"onePrice\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"owner\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"renounceOwnership\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"gamecoinPayed\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"saleOrderNoPrice\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"saleOrdersArray\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"time\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"sender\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"price\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"setPrice\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"symbol\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"string\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"string\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [],\n" +
            "\t\t\"name\": \"totalSupply\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"view\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"to\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"transfer\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"bool\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"bool\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"from\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"to\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"transferFrom\",\n" +
            "\t\t\"outputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"bool\",\n" +
            "\t\t\t\t\"name\": \"\",\n" +
            "\t\t\t\t\"type\": \"bool\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address\",\n" +
            "\t\t\t\t\"name\": \"newOwner\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"transferOwnership\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"nonpayable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"inputs\": [\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"uint256\",\n" +
            "\t\t\t\t\"name\": \"amount\",\n" +
            "\t\t\t\t\"type\": \"uint256\"\n" +
            "\t\t\t},\n" +
            "\t\t\t{\n" +
            "\t\t\t\t\"internalType\": \"address payable\",\n" +
            "\t\t\t\t\"name\": \"receiver\",\n" +
            "\t\t\t\t\"type\": \"address\"\n" +
            "\t\t\t}\n" +
            "\t\t],\n" +
            "\t\t\"name\": \"withDraw\",\n" +
            "\t\t\"outputs\": [],\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"function\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t\t\"stateMutability\": \"payable\",\n" +
            "\t\t\"type\": \"receive\"\n" +
            "\t}\n" +
            "]";
    public static StringBuilder contractBinaryData = new StringBuilder();

    static {
        contractBinaryData.append("608060405260646006553480156200001657600080fd5b5060405162004fda38038062004fda83398181016040528101906200003c91906200048c565b6040518060400160405280600881526020017f47616d65436f696e0000000000000000000000000000000000000000000000008152506040518060400160405280600881526020017f67616d65636f696e0000000000000000000000000000000000000000000000008152508160039080519060200190620000c09291906200039c565b508060049080519060200190620000d99291906200039c565b505050620000fc620000f06200014260201b60201c565b6200014a60201b60201c565b6200013b33620001116200021060201b60201c565b60ff16600a62000122919062000641565b836200012f919062000692565b6200021960201b60201c565b5062000866565b600033905090565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905081600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156200028c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401620002839062000754565b60405180910390fd5b620002a0600083836200039260201b60201c565b8060026000828254620002b4919062000776565b92505081905550806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546200030b919062000776565b925050819055508173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051620003729190620007e4565b60405180910390a36200038e600083836200039760201b60201c565b5050565b505050565b505050565b828054620003aa9062000830565b90600052602060002090601f016020900481019282620003ce57600085556200041a565b82601f10620003e957805160ff19168380011785556200041a565b828001600101855582156200041a579182015b8281111562000419578251825591602001919060010190620003fc565b5b5090506200042991906200042d565b5090565b5b80821115620004485760008160009055506001016200042e565b5090565b600080fd5b6000819050919050565b620004668162000451565b81146200047257600080fd5b50565b60008151905062000486816200045b565b92915050565b600060208284031215620004a557620004a46200044c565b5b6000620004b58482850162000475565b91505092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60008160011c9050919050565b6000808291508390505b60018511156200054c57808604811115620005245762000523620004be565b5b6001851615620005345780820291505b80810290506200054485620004ed565b945062000504565b94509492505050565b6000826200056757600190506200063a565b816200057757600090506200063a565b81600181146200059057600281146200059b57620005d1565b60019150506200063a565b60ff841115620005b057620005af620004be565b5b8360020a915084821115620005ca57620005c9620004be565b5b506200063a565b5060208310610133831016604e8410600b84101617156200060b5782820a905083811115620006055762000604620004be565b5b6200063a565b6200061a8484846001620004fa565b92509050818404811115620006345762000633620004be565b5b81810290505b9392505050565b60006200064e8262000451565b91506200065b8362000451565b92506200068a7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff848462000555565b905092915050565b60006200069f8262000451565b9150620006ac8362000451565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0483118215151615620006e857620006e7620004be565b5b828202905092915050565b600082825260208201905092915050565b7f45524332303a206d696e7420746f20746865207a65726f206164647265737300600082015250565b60006200073c601f83620006f3565b9150620007498262000704565b602082019050919050565b600060208201905081810360008301526200076f816200072d565b9050919050565b6000620007838262000451565b9150620007908362000451565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff03821115620007c857620007c7620004be565b5b828201905092915050565b620007de8162000451565b82525050565b6000602082019050620007fb6000830184620007d3565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806200084957607f821691505b6020821081141562000860576200085f62000801565b5b50919050565b61476480620008766000396000f3fe6080604052600436106101e75760003560e01c80638da5cb5b11610102578063baa10f9411610095578063dd62ed3e11610064578063dd62ed3e146106c3578063e0c5a27714610700578063f2fde38b1461072b578063fcd3533c146107545761020b565b8063baa10f9414610613578063be5d147f14610653578063c13de9131461067c578063ce60cdde146106985761020b565b8063a457c2d7116100d1578063a457c2d714610530578063a9059cbb1461056d578063b17c6ab8146105aa578063b27597b5146105d35761020b565b80638da5cb5b1461048657806391b7f5ed146104b157806395d89b41146104da5780639b917c79146105055761020b565b8063362ad9401161017a578063715018a611610149578063715018a614610420578063767167e6146104375780638428b32d14610460578063845fb2111461046a5761020b565b8063362ad9401461035c578063395093511461036657806358d7c8b2146103a357806370a08231146103e35761020b565b806318160ddd116101b657806318160ddd146102bf5780632382a359146102ea57806323b872dd146102f4578063313ce567146103315761020b565b806306fdde031461021057806308bd2f9a1461023b578063095ea7b31461025757806312065fe0146102945761020b565b3661020b576000349050610208336006548361020391906138bd565b61077d565b50005b600080fd5b34801561021c57600080fd5b506102256108dd565b60405161023291906139b0565b60405180910390f35b61025560048036038101906102509190613a03565b61096f565b005b34801561026357600080fd5b5061027e60048036038101906102799190613aa1565b610eb9565b60405161028b9190613afc565b60405180910390f35b3480156102a057600080fd5b506102a9610edc565b6040516102b69190613b26565b60405180910390f35b3480156102cb57600080fd5b506102d4610ee4565b6040516102e19190613b26565b60405180910390f35b6102f2610eee565b005b34801561030057600080fd5b5061031b60048036038101906103169190613b41565b6110e6565b6040516103289190613afc565b60405180910390f35b34801561033d57600080fd5b50610346611115565b6040516103539190613bb0565b60405180910390f35b61036461111e565b005b34801561037257600080fd5b5061038d60048036038101906103889190613aa1565b6113df565b60405161039a9190613afc565b60405180910390f35b3480156103af57600080fd5b506103ca60048036038101906103c59190613bcb565b611489565b6040516103da9493929190613c07565b60405180910390f35b3480156103ef57600080fd5b5061040a60048036038101906104059190613c4c565b6114e9565b6040516104179190613b26565b60405180910390f35b34801561042c57600080fd5b50610435611531565b005b34801561044357600080fd5b5061045e60048036038101906104599190613bcb565b6115b9565b005b610468611845565b005b610484600480360381019061047f9190613cb7565b611ac1565b005b34801561049257600080fd5b5061049b611bb4565b6040516104a89190613cf7565b60405180910390f35b3480156104bd57600080fd5b506104d860048036038101906104d39190613bcb565b611bde565b005b3480156104e657600080fd5b506104ef611c64565b6040516104fc91906139b0565b60405180910390f35b34801561051157600080fd5b5061051a611cf6565b6040516105279190613e34565b60405180910390f35b34801561053c57600080fd5b5061055760048036038101906105529190613aa1565b611dc9565b6040516105649190613afc565b60405180910390f35b34801561057957600080fd5b50610594600480360381019061058f9190613aa1565b611eb3565b6040516105a19190613afc565b60405180910390f35b3480156105b657600080fd5b506105d160048036038101906105cc9190613bcb565b611ed6565b005b3480156105df57600080fd5b506105fa60048036038101906105f59190613bcb565b612247565b60405161060a9493929190613c07565b60405180910390f35b34801561061f57600080fd5b5061063a60048036038101906106359190613bcb565b6122a7565b60405161064a9493929190613c07565b60405180910390f35b34801561065f57600080fd5b5061067a60048036038101906106759190613bcb565b612307565b005b61069660048036038101906106919190613bcb565b6126fc565b005b3480156106a457600080fd5b506106ad612c3e565b6040516106ba9190613b26565b60405180910390f35b3480156106cf57600080fd5b506106ea60048036038101906106e59190613e56565b612c44565b6040516106f79190613b26565b60405180910390f35b34801561070c57600080fd5b50610715612ccb565b6040516107229190613e34565b60405180910390f35b34801561073757600080fd5b50610752600480360381019061074d9190613c4c565b612d9e565b005b34801561076057600080fd5b5061077b60048036038101906107769190613e96565b612e96565b005b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156107ed576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016107e490613f22565b60405180910390fd5b6107f960008383612f35565b806002600082825461080b9190613f42565b92505081905550806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546108609190613f42565b925050819055508173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040516108c59190613b26565b60405180910390a36108d960008383612f3a565b5050565b6060600380546108ec90613fc7565b80601f016020809104026020016040519081016040528092919081815260200182805461091890613fc7565b80156109655780601f1061093a57610100808354040283529160200191610965565b820191906000526020600020905b81548152906001019060200180831161094857829003601f168201915b5050505050905090565b60008211801561097f5750600081115b61098857600080fd5b81610992336114e9565b101561099d57600080fd5b6109a73383612f3f565b600060405180608001604052808481526020018381526020014281526020013373ffffffffffffffffffffffffffffffffffffffff1681525090506000600f805490501415610a8857600f81908060018154018082558091505060019003906000526020600020906004020160009091909190915060008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050610eb4565b6000805b600f80549050811015610d5f578115610c02576000600f8281548110610ab557610ab4613ff9565b5b906000526020600020906004020190506007600f8381548110610adb57610ada613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055509050508060076000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555090505050610d4c565b600f8181548110610c1657610c15613ff9565b5b906000526020600020906004020160076000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550905050600760010154841115610d4b5782600f8281548110610cd057610ccf613ff9565b5b906000526020600020906004020160008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550905050600191505b5b8080610d5790614028565b915050610a8c565b5080610dfd57600f82908060018154018082558091505060019003906000526020600020906004020160009091909190915060008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050610eb2565b600f600790806001815401808255809150506001900390600052602060002090600402016000909190919091506000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050505b505b505050565b600080610ec4613116565b9050610ed181858561311e565b600191505092915050565b600047905090565b6000600254905090565b60006010600081548110610f0557610f04613ff9565b5b906000526020600020906004020160000154905060006010600081548110610f3057610f2f613ff9565b5b906000526020600020906004020160010154905060005b600f8054905081108015610f5c575060008110155b1561109c57600f8181548110610f7557610f74613ff9565b5b906000526020600020906004020160010154821115610f935761109c565b6000600f8281548110610fa957610fa8613ff9565b5b90600052602060002090600402016000015490508381111561101b576000610fda85836132e990919063ffffffff16565b905080600f8481548110610ff157610ff0613ff9565b5b90600052602060002090600402016000018190555060009450611014338661077d565b505061109c565b61102e81856132e990919063ffffffff16565b93506000600f838154811061104657611045613ff9565b5b9060005260206000209060040201600001549050611065600f846132ff565b6000831461107d5760018361107a9190614071565b92505b611087338261077d565b5050808061109490614028565b915050610f47565b50600082146110d5578160106000815481106110bb576110ba613ff9565b5b9060005260206000209060040201600001819055506110e2565b6110e1601060006132ff565b5b5050565b6000806110f1613116565b90506110fe858285613485565b611109858585613511565b60019150509392505050565b60006012905090565b6000600f60008154811061113557611134613ff9565b5b90600052602060002090600402016000015490506000600f6000815481106111605761115f613ff9565b5b906000526020600020906004020160010154905060005b6010805490508110801561118c575060008110155b1561139557601081815481106111a5576111a4613ff9565b5b9060005260206000209060040201600101548210156111c357611395565b6000601082815481106111d9576111d8613ff9565b5b9060005260206000209060040201600001549050838111156112c557600061120a85836132e990919063ffffffff16565b905060006112466010858154811061122557611224613ff9565b5b9060005260206000209060040201600101548761379290919063ffffffff16565b9050816010858154811061125d5761125c613ff9565b5b906000526020600020906004020160000181905550600095503373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501580156112bc573d6000803e3d6000fd5b50505050611395565b6112d881856132e990919063ffffffff16565b93506000611314601084815481106112f3576112f2613ff9565b5b9060005260206000209060040201600101548361379290919063ffffffff16565b90506113216010846132ff565b60008314611339576001836113369190614071565b92505b3373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f1935050505015801561137f573d6000803e3d6000fd5b505050808061138d90614028565b915050611177565b50600082146113ce5781600f6000815481106113b4576113b3613ff9565b5b9060005260206000209060040201600001819055506113db565b6113da600f60006132ff565b5b5050565b6000806113ea613116565b905061147e818585600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546114799190613f42565b61311e565b600191505092915050565b600f818154811061149957600080fd5b90600052602060002090600402016000915090508060000154908060010154908060020154908060030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905084565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b611539613116565b73ffffffffffffffffffffffffffffffffffffffff16611557611bb4565b73ffffffffffffffffffffffffffffffffffffffff16146115ad576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016115a4906140f1565b60405180910390fd5b6115b760006137a8565b565b600081116115c657600080fd5b806115d0336114e9565b10156115db57600080fd5b60005b60108054905081101561182e5760006010828154811061160157611600613ff9565b5b9060005260206000209060040201600001549050828111156116e957600061163284836132e990919063ffffffff16565b9050600061166e6010858154811061164d5761164c613ff9565b5b9060005260206000209060040201600101548461379290919063ffffffff16565b9050816010858154811061168557611684613ff9565b5b9060005260206000209060040201600001819055503373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501580156116e0573d6000803e3d6000fd5b5050505061182e565b6116fc81846132e990919063ffffffff16565b9250600061175d6010848154811061171757611716613ff9565b5b9060005260206000209060040201600101546010858154811061173d5761173c613ff9565b5b90600052602060002090600402016000015461379290919063ffffffff16565b90506010838154811061177357611772613ff9565b5b9060005260206000209060040201600080820160009055600182016000905560028201600090556003820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905550506001836117d09190614071565b92503373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015611818573d6000803e3d6000fd5b505050808061182690614028565b9150506115de565b506000811461184257611841338261077d565b5b50565b60003490506000341161185757600080fd5b60005b600f80549050811015611a6d5760006118c6600f83815481106118805761187f613ff9565b5b906000526020600020906004020160010154600f84815481106118a6576118a5613ff9565b5b90600052602060002090600402016000015461379290919063ffffffff16565b90508281111561199c5760006118e584836132e990919063ffffffff16565b90506000611921600f8581548110611900576118ff613ff9565b5b9060005260206000209060040201600101548361386e90919063ffffffff16565b9050600061195d82600f878154811061193d5761193c613ff9565b5b9060005260206000209060040201600001546132e990919063ffffffff16565b905081600f868154811061197457611973613ff9565b5b906000526020600020906004020160000181905550611993338261077d565b50505050611a6d565b6119af81846132e990919063ffffffff16565b92506000600f83815481106119c7576119c6613ff9565b5b9060005260206000209060040201600001549050600f83815481106119ef576119ee613ff9565b5b9060005260206000209060040201600080820160009055600182016000905560028201600090556003820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555050600183611a4c9190614071565b9250611a58338261077d565b50508080611a6590614028565b91505061185a565b5060008114611abe573373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015611abc573d6000803e3d6000fd5b505b50565b611ac9613116565b73ffffffffffffffffffffffffffffffffffffffff16611ae7611bb4565b73ffffffffffffffffffffffffffffffffffffffff1614611b3d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611b34906140f1565b60405180910390fd5b81611b47826114e9565b1015611b5257600080fd5b611b5c8183612f3f565b8073ffffffffffffffffffffffffffffffffffffffff166108fc60065484611b849190614140565b9081150290604051600060405180830381858888f19350505050158015611baf573d6000803e3d6000fd5b505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b611be6613116565b73ffffffffffffffffffffffffffffffffffffffff16611c04611bb4565b73ffffffffffffffffffffffffffffffffffffffff1614611c5a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611c51906140f1565b60405180910390fd5b8060068190555050565b606060048054611c7390613fc7565b80601f0160208091040260200160405190810160405280929190818152602001828054611c9f90613fc7565b8015611cec5780601f10611cc157610100808354040283529160200191611cec565b820191906000526020600020905b815481529060010190602001808311611ccf57829003601f168201915b5050505050905090565b6060600f805480602002602001604051908101604052809291908181526020016000905b82821015611dc057838290600052602060002090600402016040518060800160405290816000820154815260200160018201548152602001600282015481526020016003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152505081526020019060010190611d1a565b50505050905090565b600080611dd4613116565b90506000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905083811015611e9a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611e91906141e3565b60405180910390fd5b611ea7828686840361311e565b60019250505092915050565b600080611ebe613116565b9050611ecb818585613511565b600191505092915050565b60008060005b600f805490508110156121c2578215611fe657600f80549050600182611f029190613f42565b1015611fe157600f600182611f179190613f42565b81548110611f2857611f27613ff9565b5b9060005260206000209060040201600f8281548110611f4a57611f49613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055509050505b6121af565b600f8181548110611ffa57611ff9613ff9565b5b9060005260206000209060040201600201548414801561208a57503373ffffffffffffffffffffffffffffffffffffffff16600f82815481106120405761203f613ff9565b5b906000526020600020906004020160030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b156121ae57600f81815481106120a3576120a2613ff9565b5b9060005260206000209060040201600001549150600f805490506001826120ca9190613f42565b10156121a957600f6001826120df9190613f42565b815481106120f0576120ef613ff9565b5b9060005260206000209060040201600f828154811061211257612111613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055509050505b600192505b5b80806121ba90614028565b915050611edc565b50600f6001600f805490506121d79190614071565b815481106121e8576121e7613ff9565b5b9060005260206000209060040201600080820160009055600182016000905560028201600090556003820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555050612242338261077d565b505050565b6010818154811061225757600080fd5b90600052602060002090600402016000915090508060000154908060010154908060020154908060030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905084565b601181815481106122b757600080fd5b90600052602060002090600402016000915090508060000154908060010154908060020154908060030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905084565b60008060006001905060005b60108054905081101561262157831561241d576010805490506001826123399190613f42565b101561241857601060018261234e9190613f42565b8154811061235f5761235e613ff9565b5b90600052602060002090600402016010828154811061238157612380613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055509050505b61260e565b6010818154811061243157612430613ff9565b5b906000526020600020906004020160020154851480156124c157503373ffffffffffffffffffffffffffffffffffffffff166010828154811061247757612476613ff9565b5b906000526020600020906004020160030160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b1561260d57601081815481106124da576124d9613ff9565b5b90600052602060002090600402016000015492506010818154811061250257612501613ff9565b5b9060005260206000209060040201600101549150600f805490506001826125299190613f42565b101561260857601060018261253e9190613f42565b8154811061254f5761254e613ff9565b5b90600052602060002090600402016010828154811061257157612570613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055509050505b600193505b5b808061261990614028565b915050612313565b50601060016010805490506126369190614071565b8154811061264757612646613ff9565b5b9060005260206000209060040201600080820160009055600182016000905560028201600090556003820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055505060003390508073ffffffffffffffffffffffffffffffffffffffff166108fc6126c9848661379290919063ffffffff16565b9081150290604051600060405180830381858888f193505050501580156126f4573d6000803e3d6000fd5b505050505050565b60003490506000341180156127115750600082115b61271a57600080fd5b6000604051806080016040528061273a858561386e90919063ffffffff16565b81526020018481526020014281526020013373ffffffffffffffffffffffffffffffffffffffff1681525090506000601080549050141561280d57601081908060018154018082558091505060019003906000526020600020906004020160009091909190915060008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050612c39565b6000805b601080549050811015612ae45781156129875760006010828154811061283a57612839613ff9565b5b90600052602060002090600402019050600b601083815481106128605761285f613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555090505080600b6000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555090505050612ad1565b6010818154811061299b5761299a613ff9565b5b9060005260206000209060040201600b6000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550905050600b60010154851015612ad0578260108281548110612a5557612a54613ff9565b5b906000526020600020906004020160008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550905050600191505b5b8080612adc90614028565b915050612811565b5080612b8257601082908060018154018082558091505060019003906000526020600020906004020160009091909190915060008201518160000155602082015181600101556040820151816002015560608201518160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505050612c37565b6010600b90806001815401808255809150506001900390600052602060002090600402016000909190919091506000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050505b505b505050565b60065481565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b60606010805480602002602001604051908101604052809291908181526020016000905b82821015612d9557838290600052602060002090600402016040518060800160405290816000820154815260200160018201548152602001600282015481526020016003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152505081526020019060010190612cef565b50505050905090565b612da6613116565b73ffffffffffffffffffffffffffffffffffffffff16612dc4611bb4565b73ffffffffffffffffffffffffffffffffffffffff1614612e1a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401612e11906140f1565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415612e8a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401612e8190614275565b60405180910390fd5b612e93816137a8565b50565b612e9e613116565b73ffffffffffffffffffffffffffffffffffffffff16612ebc611bb4565b73ffffffffffffffffffffffffffffffffffffffff1614612f12576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401612f09906140f1565b60405180910390fd5b81612f1c826114e9565b1015612f2757600080fd5b612f318183612f3f565b5050565b505050565b505050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415612faf576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401612fa690614307565b60405180910390fd5b612fbb82600083612f35565b60008060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015613041576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161303890614399565b60405180910390fd5b8181036000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555081600260008282546130989190614071565b92505081905550600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040516130fd9190613b26565b60405180910390a361311183600084612f3a565b505050565b600033905090565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff16141561318e576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016131859061442b565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156131fe576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016131f5906144bd565b60405180910390fd5b80600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925836040516132dc9190613b26565b60405180910390a3505050565b600081836132f79190614071565b905092915050565b8180549050811061330f57613481565b60008190505b600183805490506133269190614071565b811015613416578260018261333b9190613f42565b8154811061334c5761334b613ff9565b5b906000526020600020906004020183828154811061336d5761336c613ff9565b5b90600052602060002090600402016000820154816000015560018201548160010155600282015481600201556003820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160030160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550905050808061340e90614028565b915050613315565b5081805480613428576134276144dd565b5b6001900381819060005260206000209060040201600080820160009055600182016000905560028201600090556003820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055505090555b5050565b60006134918484612c44565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff811461350b57818110156134fd576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016134f490614558565b60405180910390fd5b61350a848484840361311e565b5b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415613581576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401613578906145ea565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156135f1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016135e89061467c565b60405180910390fd5b6135fc838383612f35565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015613682576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016136799061470e565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550816000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546137159190613f42565b925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040516137799190613b26565b60405180910390a361378c848484612f3a565b50505050565b600081836137a09190614140565b905092915050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905081600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b6000818361387c91906138bd565b905092915050565b6000819050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60006138c882613884565b91506138d383613884565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff048311821515161561390c5761390b61388e565b5b828202905092915050565b600081519050919050565b600082825260208201905092915050565b60005b83811015613951578082015181840152602081019050613936565b83811115613960576000848401525b50505050565b6000601f19601f8301169050919050565b600061398282613917565b61398c8185613922565b935061399c818560208601613933565b6139a581613966565b840191505092915050565b600060208201905081810360008301526139ca8184613977565b905092915050565b600080fd5b6139e081613884565b81146139eb57600080fd5b50565b6000813590506139fd816139d7565b92915050565b60008060408385031215613a1a57613a196139d2565b5b6000613a28858286016139ee565b9250506020613a39858286016139ee565b9150509250929050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000613a6e82613a43565b9050919050565b613a7e81613a63565b8114613a8957600080fd5b50565b600081359050613a9b81613a75565b92915050565b60008060408385031215613ab857613ab76139d2565b5b6000613ac685828601613a8c565b9250506020613ad7858286016139ee565b9150509250929050565b60008115159050919050565b613af681613ae1565b82525050565b6000602082019050613b116000830184613aed565b92915050565b613b2081613884565b82525050565b6000602082019050613b3b6000830184613b17565b92915050565b600080600060608486031215613b5a57613b596139d2565b5b6000613b6886828701613a8c565b9350506020613b7986828701613a8c565b9250506040613b8a868287016139ee565b9150509250925092565b600060ff82169050919050565b613baa81613b94565b82525050565b6000602082019050613bc56000830184613ba1565b92915050565b600060208284031215613be157613be06139d2565b5b6000613bef848285016139ee565b91505092915050565b613c0181613a63565b82525050565b6000608082019050613c1c6000830187613b17565b613c296020830186613b17565b613c366040830185613b17565b613c436060830184613bf8565b95945050505050565b600060208284031215613c6257613c616139d2565b5b6000613c7084828501613a8c565b91505092915050565b6000613c8482613a43565b9050919050565b613c9481613c79565b8114613c9f57600080fd5b50565b600081359050613cb181613c8b565b92915050565b60008060408385031215613cce57613ccd6139d2565b5b6000613cdc858286016139ee565b9250506020613ced85828601613ca2565b9150509250929050565b6000602082019050613d0c6000830184613bf8565b92915050565b600081519050919050565b600082825260208201905092915050565b6000819050602082019050919050565b613d4781613884565b82525050565b613d5681613a63565b82525050565b608082016000820151613d726000850182613d3e565b506020820151613d856020850182613d3e565b506040820151613d986040850182613d3e565b506060820151613dab6060850182613d4d565b50505050565b6000613dbd8383613d5c565b60808301905092915050565b6000602082019050919050565b6000613de182613d12565b613deb8185613d1d565b9350613df683613d2e565b8060005b83811015613e27578151613e0e8882613db1565b9750613e1983613dc9565b925050600181019050613dfa565b5085935050505092915050565b60006020820190508181036000830152613e4e8184613dd6565b905092915050565b60008060408385031215613e6d57613e6c6139d2565b5b6000613e7b85828601613a8c565b9250506020613e8c85828601613a8c565b9150509250929050565b60008060408385031215613ead57613eac6139d2565b5b6000613ebb858286016139ee565b9250506020613ecc85828601613a8c565b9150509250929050565b7f45524332303a206d696e7420746f20746865207a65726f206164647265737300600082015250565b6000613f0c601f83613922565b9150613f1782613ed6565b602082019050919050565b60006020820190508181036000830152613f3b81613eff565b9050919050565b6000613f4d82613884565b9150613f5883613884565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff03821115613f8d57613f8c61388e565b5b828201905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680613fdf57607f821691505b60208210811415613ff357613ff2613f98565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b600061403382613884565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8214156140665761406561388e565b5b600182019050919050565b600061407c82613884565b915061408783613884565b92508282101561409a5761409961388e565b5b828203905092915050565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572600082015250565b60006140db602083613922565b91506140e6826140a5565b602082019050919050565b6000602082019050818103600083015261410a816140ce565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b600061414b82613884565b915061415683613884565b92508261416657614165614111565b5b828204905092915050565b7f45524332303a2064656372656173656420616c6c6f77616e63652062656c6f7760008201527f207a65726f000000000000000000000000000000000000000000000000000000602082015250565b60006141cd602583613922565b91506141d882614171565b604082019050919050565b600060208201905081810360008301526141fc816141c0565b9050919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f6464726573730000000000000000000000000000000000000000000000000000602082015250565b600061425f602683613922565b915061426a82614203565b604082019050919050565b6000602082019050818103600083015261428e81614252565b9050919050565b7f45524332303a206275726e2066726f6d20746865207a65726f2061646472657360008201527f7300000000000000000000000000000000000000000000000000000000000000602082015250565b60006142f1602183613922565b91506142fc82614295565b604082019050919050565b60006020820190508181036000830152614320816142e4565b9050919050565b7f45524332303a206275726e20616d6f756e7420657863656564732062616c616e60008201527f6365000000000000000000000000000000000000000000000000000000000000602082015250565b6000614383602283613922565b915061438e82614327565b604082019050919050565b600060208201905081810360008301526143b281614376565b9050919050565b7f45524332303a20617070726f76652066726f6d20746865207a65726f2061646460008201527f7265737300000000000000000000000000000000000000000000000000000000602082015250565b6000614415602483613922565b9150614420826143b9565b604082019050919050565b6000602082019050818103600083015261444481614408565b9050919050565b7f45524332303a20617070726f766520746f20746865207a65726f20616464726560008201527f7373000000000000000000000000000000000000000000000000000000000000602082015250565b60006144a7602283613922565b91506144b28261444b565b604082019050919050565b600060208201905081810360008301526144d68161449a565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603160045260246000fd5b7f45524332303a20696e73756666696369656e7420616c6c6f77616e6365000000600082015250565b6000614542601d83613922565b915061454d8261450c565b602082019050919050565b6000602082019050818103600083015261457181614535565b9050919050565b7f45524332303a207472616e736665722066726f6d20746865207a65726f20616460008201527f6472657373000000000000000000000000000000000000000000000000000000602082015250565b60006145d4602583613922565b91506145df82614578565b604082019050919050565b60006020820190508181036000830152614603816145c7565b9050919050565b7f45524332303a207472616e7366657220746f20746865207a65726f206164647260008201527f6573730000000000000000000000000000000000000000000000000000000000602082015250565b6000614666602383613922565b91506146718261460a565b604082019050919050565b6000602082019050818103600083015261469581614659565b9050919050565b7f45524332303a207472616e7366657220616d6f756e742065786365656473206260008201527f616c616e63650000000000000000000000000000000000000000000000000000602082015250565b60006146f8602683613922565b91506147038261469c565b604082019050919050565b60006020820190508181036000830152614727816146eb565b905091905056fea264697066735822122021496723a4a9bc1ed310555de2906d3504d635ceb70d6f8af31b48c74e67cbd264736f6c634300080b0033");
    }

}
