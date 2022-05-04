package easyJava.controller;

import org.springframework.web.bind.annotation.RestController;


@RestController
public class KlayContractController {


    public static final String ABI = "[\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"initialSupply\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"constructor\"\n" +
            "},\n" +
            "{\n" +
            "\"anonymous\": false,\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"owner\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"spender\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"indexed\": false,\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"value\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"Approval\",\n" +
            "\"type\": \"event\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"spender\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"amount\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"approve\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"bool\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"bool\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"amount\",\n" +
            "\"type\": \"uint256\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"receiver\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"burn\",\n" +
            "\"outputs\": [],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"spender\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"subtractedValue\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"decreaseAllowance\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"bool\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"bool\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"spender\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"addedValue\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"increaseAllowance\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"bool\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"bool\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"anonymous\": false,\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"previousOwner\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"newOwner\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"OwnershipTransferred\",\n" +
            "\"type\": \"event\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"renounceOwnership\",\n" +
            "\"outputs\": [],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"price\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"setPrice\",\n" +
            "\"outputs\": [],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"to\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"amount\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"transfer\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"bool\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"bool\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"anonymous\": false,\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"from\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"indexed\": true,\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"to\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"indexed\": false,\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"value\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"Transfer\",\n" +
            "\"type\": \"event\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"from\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"to\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"amount\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"transferFrom\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"bool\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"bool\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"newOwner\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"transferOwnership\",\n" +
            "\"outputs\": [],\n" +
            "\"stateMutability\": \"nonpayable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"amount\",\n" +
            "\"type\": \"uint256\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"address payable\",\n" +
            "\"name\": \"receiver\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"withDraw\",\n" +
            "\"outputs\": [],\n" +
            "\"stateMutability\": \"payable\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"stateMutability\": \"payable\",\n" +
            "\"type\": \"receive\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"owner\",\n" +
            "\"type\": \"address\"\n" +
            "},\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"spender\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"allowance\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"account\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"name\": \"balanceOf\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"decimals\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint8\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"uint8\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"getBalance\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"balance\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"name\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"string\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"string\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"onePrice\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"owner\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"address\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"address\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"symbol\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"string\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"string\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "},\n" +
            "{\n" +
            "\"inputs\": [],\n" +
            "\"name\": \"totalSupply\",\n" +
            "\"outputs\": [\n" +
            "{\n" +
            "\"internalType\": \"uint256\",\n" +
            "\"name\": \"\",\n" +
            "\"type\": \"uint256\"\n" +
            "}\n" +
            "],\n" +
            "\"stateMutability\": \"view\",\n" +
            "\"type\": \"function\"\n" +
            "}\n" +
            "]";
    public static StringBuilder contractBinaryData = new StringBuilder();

    static {
        contractBinaryData.append("608060405260016006553480156200001657600080fd5b506040516200298d3803806200298d83398181016040528101906200003c91906200048c565b6040518060400160405280600381526020017f63687200000000000000000000000000000000000000000000000000000000008152506040518060400160405280600381526020017f43485200000000000000000000000000000000000000000000000000000000008152508160039080519060200190620000c09291906200039c565b508060049080519060200190620000d99291906200039c565b505050620000fc620000f06200014260201b60201c565b6200014a60201b60201c565b6200013b33620001116200021060201b60201c565b60ff16600a62000122919062000641565b836200012f919062000692565b6200021960201b60201c565b5062000866565b600033905090565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905081600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b60006012905090565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156200028c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401620002839062000754565b60405180910390fd5b620002a0600083836200039260201b60201c565b8060026000828254620002b4919062000776565b92505081905550806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546200030b919062000776565b925050819055508173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef83604051620003729190620007e4565b60405180910390a36200038e600083836200039760201b60201c565b5050565b505050565b505050565b828054620003aa9062000830565b90600052602060002090601f016020900481019282620003ce57600085556200041a565b82601f10620003e957805160ff19168380011785556200041a565b828001600101855582156200041a579182015b8281111562000419578251825591602001919060010190620003fc565b5b5090506200042991906200042d565b5090565b5b80821115620004485760008160009055506001016200042e565b5090565b600080fd5b6000819050919050565b620004668162000451565b81146200047257600080fd5b50565b60008151905062000486816200045b565b92915050565b600060208284031215620004a557620004a46200044c565b5b6000620004b58482850162000475565b91505092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60008160011c9050919050565b6000808291508390505b60018511156200054c57808604811115620005245762000523620004be565b5b6001851615620005345780820291505b80810290506200054485620004ed565b945062000504565b94509492505050565b6000826200056757600190506200063a565b816200057757600090506200063a565b81600181146200059057600281146200059b57620005d1565b60019150506200063a565b60ff841115620005b057620005af620004be565b5b8360020a915084821115620005ca57620005c9620004be565b5b506200063a565b5060208310610133831016604e8410600b84101617156200060b5782820a905083811115620006055762000604620004be565b5b6200063a565b6200061a8484846001620004fa565b92509050818404811115620006345762000633620004be565b5b81810290505b9392505050565b60006200064e8262000451565b91506200065b8362000451565b92506200068a7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff848462000555565b905092915050565b60006200069f8262000451565b9150620006ac8362000451565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0483118215151615620006e857620006e7620004be565b5b828202905092915050565b600082825260208201905092915050565b7f45524332303a206d696e7420746f20746865207a65726f206164647265737300600082015250565b60006200073c601f83620006f3565b9150620007498262000704565b602082019050919050565b600060208201905081810360008301526200076f816200072d565b9050919050565b6000620007838262000451565b9150620007908362000451565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff03821115620007c857620007c7620004be565b5b828201905092915050565b620007de8162000451565b82525050565b6000602082019050620007fb6000830184620007d3565b92915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806200084957607f821691505b6020821081141562000860576200085f62000801565b5b50919050565b61211780620008766000396000f3fe6080604052600436106101185760003560e01c8063845fb211116100a0578063a9059cbb11610064578063a9059cbb146103d0578063ce60cdde1461040d578063dd62ed3e14610438578063f2fde38b14610475578063fcd3533c1461049e5761013c565b8063845fb211146102f85780638da5cb5b1461031457806391b7f5ed1461033f57806395d89b4114610368578063a457c2d7146103935761013c565b806323b872dd116100e757806323b872dd146101ff578063313ce5671461023c578063395093511461026757806370a08231146102a4578063715018a6146102e15761013c565b806306fdde0314610141578063095ea7b31461016c57806312065fe0146101a957806318160ddd146101d45761013c565b3661013c576000349050610139336006548361013491906114e0565b6104c7565b50005b600080fd5b34801561014d57600080fd5b50610156610627565b60405161016391906115d3565b60405180910390f35b34801561017857600080fd5b50610193600480360381019061018e9190611684565b6106b9565b6040516101a091906116df565b60405180910390f35b3480156101b557600080fd5b506101be6106dc565b6040516101cb9190611709565b60405180910390f35b3480156101e057600080fd5b506101e96106e4565b6040516101f69190611709565b60405180910390f35b34801561020b57600080fd5b5061022660048036038101906102219190611724565b6106ee565b60405161023391906116df565b60405180910390f35b34801561024857600080fd5b5061025161071d565b60405161025e9190611793565b60405180910390f35b34801561027357600080fd5b5061028e60048036038101906102899190611684565b610726565b60405161029b91906116df565b60405180910390f35b3480156102b057600080fd5b506102cb60048036038101906102c691906117ae565b61075d565b6040516102d89190611709565b60405180910390f35b3480156102ed57600080fd5b506102f66107a5565b005b610312600480360381019061030d9190611819565b61082d565b005b34801561032057600080fd5b50610329610920565b6040516103369190611868565b60405180910390f35b34801561034b57600080fd5b5061036660048036038101906103619190611883565b61094a565b005b34801561037457600080fd5b5061037d6109d0565b60405161038a91906115d3565b60405180910390f35b34801561039f57600080fd5b506103ba60048036038101906103b59190611684565b610a62565b6040516103c791906116df565b60405180910390f35b3480156103dc57600080fd5b506103f760048036038101906103f29190611684565b610ad9565b60405161040491906116df565b60405180910390f35b34801561041957600080fd5b50610422610afc565b60405161042f9190611709565b60405180910390f35b34801561044457600080fd5b5061045f600480360381019061045a91906118b0565b610b02565b60405161046c9190611709565b60405180910390f35b34801561048157600080fd5b5061049c600480360381019061049791906117ae565b610b89565b005b3480156104aa57600080fd5b506104c560048036038101906104c091906118f0565b610c81565b005b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610537576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161052e9061197c565b60405180910390fd5b61054360008383610d20565b8060026000828254610555919061199c565b92505081905550806000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282546105aa919061199c565b925050819055508173ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8360405161060f9190611709565b60405180910390a361062360008383610d25565b5050565b60606003805461063690611a21565b80601f016020809104026020016040519081016040528092919081815260200182805461066290611a21565b80156106af5780601f10610684576101008083540402835291602001916106af565b820191906000526020600020905b81548152906001019060200180831161069257829003601f168201915b5050505050905090565b6000806106c4610d2a565b90506106d1818585610d32565b600191505092915050565b600047905090565b6000600254905090565b6000806106f9610d2a565b9050610706858285610efd565b610711858585610f89565b60019150509392505050565b60006012905090565b600080610731610d2a565b90506107528185856107438589610b02565b61074d919061199c565b610d32565b600191505092915050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6107ad610d2a565b73ffffffffffffffffffffffffffffffffffffffff166107cb610920565b73ffffffffffffffffffffffffffffffffffffffff1614610821576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161081890611a9f565b60405180910390fd5b61082b600061120a565b565b610835610d2a565b73ffffffffffffffffffffffffffffffffffffffff16610853610920565b73ffffffffffffffffffffffffffffffffffffffff16146108a9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108a090611a9f565b60405180910390fd5b816108b38261075d565b10156108be57600080fd5b6108c881836112d0565b8073ffffffffffffffffffffffffffffffffffffffff166108fc600654846108f09190611aee565b9081150290604051600060405180830381858888f1935050505015801561091b573d6000803e3d6000fd5b505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905090565b610952610d2a565b73ffffffffffffffffffffffffffffffffffffffff16610970610920565b73ffffffffffffffffffffffffffffffffffffffff16146109c6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109bd90611a9f565b60405180910390fd5b8060068190555050565b6060600480546109df90611a21565b80601f0160208091040260200160405190810160405280929190818152602001828054610a0b90611a21565b8015610a585780601f10610a2d57610100808354040283529160200191610a58565b820191906000526020600020905b815481529060010190602001808311610a3b57829003601f168201915b5050505050905090565b600080610a6d610d2a565b90506000610a7b8286610b02565b905083811015610ac0576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ab790611b91565b60405180910390fd5b610acd8286868403610d32565b60019250505092915050565b600080610ae4610d2a565b9050610af1818585610f89565b600191505092915050565b60065481565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b610b91610d2a565b73ffffffffffffffffffffffffffffffffffffffff16610baf610920565b73ffffffffffffffffffffffffffffffffffffffff1614610c05576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610bfc90611a9f565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff161415610c75576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c6c90611c23565b60405180910390fd5b610c7e8161120a565b50565b610c89610d2a565b73ffffffffffffffffffffffffffffffffffffffff16610ca7610920565b73ffffffffffffffffffffffffffffffffffffffff1614610cfd576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610cf490611a9f565b60405180910390fd5b81610d078261075d565b1015610d1257600080fd5b610d1c81836112d0565b5050565b505050565b505050565b600033905090565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610da2576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d9990611cb5565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415610e12576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e0990611d47565b60405180910390fd5b80600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92583604051610ef09190611709565b60405180910390a3505050565b6000610f098484610b02565b90507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8114610f835781811015610f75576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610f6c90611db3565b60405180910390fd5b610f828484848403610d32565b5b50505050565b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610ff9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ff090611e45565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415611069576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161106090611ed7565b60405180910390fd5b611074838383610d20565b60008060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050818110156110fa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016110f190611f69565b60405180910390fd5b8181036000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550816000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825461118d919061199c565b925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040516111f19190611709565b60405180910390a3611204848484610d25565b50505050565b6000600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905081600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055508173ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a35050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff161415611340576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161133790611ffb565b60405180910390fd5b61134c82600083610d20565b60008060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050818110156113d2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016113c99061208d565b60405180910390fd5b8181036000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550816002600082825461142991906120ad565b92505081905550600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405161148e9190611709565b60405180910390a36114a283600084610d25565b505050565b6000819050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60006114eb826114a7565b91506114f6836114a7565b9250817fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff048311821515161561152f5761152e6114b1565b5b828202905092915050565b600081519050919050565b600082825260208201905092915050565b60005b83811015611574578082015181840152602081019050611559565b83811115611583576000848401525b50505050565b6000601f19601f8301169050919050565b60006115a58261153a565b6115af8185611545565b93506115bf818560208601611556565b6115c881611589565b840191505092915050565b600060208201905081810360008301526115ed818461159a565b905092915050565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000611625826115fa565b9050919050565b6116358161161a565b811461164057600080fd5b50565b6000813590506116528161162c565b92915050565b611661816114a7565b811461166c57600080fd5b50565b60008135905061167e81611658565b92915050565b6000806040838503121561169b5761169a6115f5565b5b60006116a985828601611643565b92505060206116ba8582860161166f565b9150509250929050565b60008115159050919050565b6116d9816116c4565b82525050565b60006020820190506116f460008301846116d0565b92915050565b611703816114a7565b82525050565b600060208201905061171e60008301846116fa565b92915050565b60008060006060848603121561173d5761173c6115f5565b5b600061174b86828701611643565b935050602061175c86828701611643565b925050604061176d8682870161166f565b9150509250925092565b600060ff82169050919050565b61178d81611777565b82525050565b60006020820190506117a86000830184611784565b92915050565b6000602082840312156117c4576117c36115f5565b5b60006117d284828501611643565b91505092915050565b60006117e6826115fa565b9050919050565b6117f6816117db565b811461180157600080fd5b50565b600081359050611813816117ed565b92915050565b600080604083850312156118305761182f6115f5565b5b600061183e8582860161166f565b925050602061184f85828601611804565b9150509250929050565b6118628161161a565b82525050565b600060208201905061187d6000830184611859565b92915050565b600060208284031215611899576118986115f5565b5b60006118a78482850161166f565b91505092915050565b600080604083850312156118c7576118c66115f5565b5b60006118d585828601611643565b92505060206118e685828601611643565b9150509250929050565b60008060408385031215611907576119066115f5565b5b60006119158582860161166f565b925050602061192685828601611643565b9150509250929050565b7f45524332303a206d696e7420746f20746865207a65726f206164647265737300600082015250565b6000611966601f83611545565b915061197182611930565b602082019050919050565b6000602082019050818103600083015261199581611959565b9050919050565b60006119a7826114a7565b91506119b2836114a7565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156119e7576119e66114b1565b5b828201905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680611a3957607f821691505b60208210811415611a4d57611a4c6119f2565b5b50919050565b7f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572600082015250565b6000611a89602083611545565b9150611a9482611a53565b602082019050919050565b60006020820190508181036000830152611ab881611a7c565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b6000611af9826114a7565b9150611b04836114a7565b925082611b1457611b13611abf565b5b828204905092915050565b7f45524332303a2064656372656173656420616c6c6f77616e63652062656c6f7760008201527f207a65726f000000000000000000000000000000000000000000000000000000602082015250565b6000611b7b602583611545565b9150611b8682611b1f565b604082019050919050565b60006020820190508181036000830152611baa81611b6e565b9050919050565b7f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160008201527f6464726573730000000000000000000000000000000000000000000000000000602082015250565b6000611c0d602683611545565b9150611c1882611bb1565b604082019050919050565b60006020820190508181036000830152611c3c81611c00565b9050919050565b7f45524332303a20617070726f76652066726f6d20746865207a65726f2061646460008201527f7265737300000000000000000000000000000000000000000000000000000000602082015250565b6000611c9f602483611545565b9150611caa82611c43565b604082019050919050565b60006020820190508181036000830152611cce81611c92565b9050919050565b7f45524332303a20617070726f766520746f20746865207a65726f20616464726560008201527f7373000000000000000000000000000000000000000000000000000000000000602082015250565b6000611d31602283611545565b9150611d3c82611cd5565b604082019050919050565b60006020820190508181036000830152611d6081611d24565b9050919050565b7f45524332303a20696e73756666696369656e7420616c6c6f77616e6365000000600082015250565b6000611d9d601d83611545565b9150611da882611d67565b602082019050919050565b60006020820190508181036000830152611dcc81611d90565b9050919050565b7f45524332303a207472616e736665722066726f6d20746865207a65726f20616460008201527f6472657373000000000000000000000000000000000000000000000000000000602082015250565b6000611e2f602583611545565b9150611e3a82611dd3565b604082019050919050565b60006020820190508181036000830152611e5e81611e22565b9050919050565b7f45524332303a207472616e7366657220746f20746865207a65726f206164647260008201527f6573730000000000000000000000000000000000000000000000000000000000602082015250565b6000611ec1602383611545565b9150611ecc82611e65565b604082019050919050565b60006020820190508181036000830152611ef081611eb4565b9050919050565b7f45524332303a207472616e7366657220616d6f756e742065786365656473206260008201527f616c616e63650000000000000000000000000000000000000000000000000000602082015250565b6000611f53602683611545565b9150611f5e82611ef7565b604082019050919050565b60006020820190508181036000830152611f8281611f46565b9050919050565b7f45524332303a206275726e2066726f6d20746865207a65726f2061646472657360008201527f7300000000000000000000000000000000000000000000000000000000000000602082015250565b6000611fe5602183611545565b9150611ff082611f89565b604082019050919050565b6000602082019050818103600083015261201481611fd8565b9050919050565b7f45524332303a206275726e20616d6f756e7420657863656564732062616c616e60008201527f6365000000000000000000000000000000000000000000000000000000000000602082015250565b6000612077602283611545565b91506120828261201b565b604082019050919050565b600060208201905081810360008301526120a68161206a565b9050919050565b60006120b8826114a7565b91506120c3836114a7565b9250828210156120d6576120d56114b1565b5b82820390509291505056fea2646970667358221220aeab5fbc7be7fc1d4ea4cdaa74e03114d102078d5a62aec24c8573da5eef483f64736f6c634300080b0033");
    }

}
