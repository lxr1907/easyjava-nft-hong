package easyJava.controller;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class KlayContractController {


    public static String ABI="[\n" +
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
            "\t}\n" +
            "]" +
            "";

}
