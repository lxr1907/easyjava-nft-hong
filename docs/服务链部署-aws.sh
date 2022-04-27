#服务器
#3.0.50.153
#baobab
#data目录 /data/baobab #端口，32323,8551,8552,50505

wget https://packages.klaytn.net/klaytn/v1.8.2/ken-baobab-v1.8.2-0-linux-amd64.tar.gz
tar -xvf ken-baobab-v1.8.2-0-linux-amd64.tar.gz

curl -X GET https://packages.klaytn.net/baobab/genesis.json -o ~/genesis.json

sudo ./ken --datadir /data/baobab init genesis.json 

sudo ./ken attach --datadir  /data/baobab

> mainbridge.nodeInfo.kni
#"kni://80188a5695c6a0ac6acb86cbf95d4a9b546a72af5d26b55f67cc8c302143e1bc0bf7eee3f64bc2cbf65f361effcc44d0a9dee108ba76b44d7267fc77de29a3e4@[::]:50505?discport=0"
#["kni://80188a5695c6a0ac6acb86cbf95d4a9b546a72af5d26b55f67cc8c302143e1bc0bf7eee3f64bc2cbf65f361effcc44d0a9dee108ba76b44d7267fc77de29a3e4@127.0.0.1:50505?discport=0"]
sudo vi  ~/data/main-bridges.json
#正式klay
# data目录 /data/klay #端口，12323,8651,8652,50505

#下载kscn，homi并解压缩

sudo ./homi setup local --cn-num 1 --test-num 1 --servicechain --chainID 1002 --p2p-port 22323 -o homi-output
#
# 0x2540be400
# de0b6b3a7640000
#初始化kscn
sudo ./kscn --datadir ~/data init ~/homi-output/scripts/genesis.json

sudo cp ~/homi-output/scripts/static-nodes.json ~/data/
sudo cp ~/homi-output/keys/nodekey1 ~/data/klay/nodekey
#配置文件端口8551,8552改为7551，7552
sudo ./kscnd start
sudo chmod 755 ~/data
sudo tail -f ~/data/logs/kscnd.out

sudo ./kscn attach --datadir ~/data
#导入初始钱包
sudo ./kscn account import --datadir ~/data ~/homi-output/keys_test/testkey1
#123
#返回地址d14884ea853e6a1d63d109791f7ca87a8129963b
#配置...
#SC_SUB_BRIDGE=1
#SC_PARENT_CHAIN_ID=1001
#SC_ANCHORING_PERIOD=10
> klay.blockNumber
89
> subbridge.peers.length
1
> subbridge.parentOperator
"0x14a0717d5bb8b42e1a33a8f944d37464be9e6624"  0x4dca18a1d7e4c2a04d8b5d0f4552963624b02abd
> subbridge.childOperator
"0x56c8cb5daf329fc8613112b51e359b2dbae4fd97"
> klay.getBalance(subbridge.childOperator)
0
> klay.getBalance(subbridge.parentOperator)
0
> klay.getBalance("5392fcdc9266cfb605f706bad502d40f242f9160")
10000000000

> subbridge.anchoring(true)
true
#该步骤总是结果为0
> subbridge.latestAnchoredBlockNumber
100
> personal.unlockAccount("5392fcdc9266cfb605f706bad502d40f242f9160") # PIdjRGLuvFFAdE[7
> klay.sendTransaction({from:"5392fcdc9266cfb605f706bad502d40f242f9160", to:subbridge.childOperator, value: 99997999999999997})
> klay.getBalance("5392fcdc9266cfb605f706bad502d40f242f9160")

> subbridge.parentOperatorBalance
klay.sendTransaction({from:"5392fcdc9266cfb605f706bad502d40f242f9160", to:subbridge.parentOperator, value: 99997999999999997})

klay.sendTransaction({from:"5392fcdc9266cfb605f706bad502d40f242f9160", to:subbridge.childOperator, value: 1000000000000})
klay.sendTransaction({from:"5392fcdc9266cfb605f706bad502d40f242f9160", to:subbridge.parentOperator, value: 1000000000000})

sudo curl -H "Content-Type: application/json" --data '{"jsonrpc":"2.0","method":"rpc_modules","params":[],"id":1}' http://54.169.70.175:8551

sudo  curl -H "Content-Type: application/json" --data '{"jsonrpc":"2.0","method":"rpc_modules","params":[],"id":1}' https://api.baobab.klaytn.net:8651

# 参考论坛
# https://forum.klaytn.com/t/baobab/635
# https://forum.klaytn.com/t/service-chain-anchoring-fail/4901/3
# https://forum.klaytn.com/t/topic/3127


#正式klay
# data目录 /data/klay_tar/klay #端口，32323,8551,8552,50505
sudo ./ken attach --datadir  /data/klay_tar/klay
> mainbridge.nodeInfo.kni
["kni://bd534ae8e27dc9f6fbc39c7fc5be432ed72d5f4d5707a1ecb478a6426e7f39da7ba3c5ff7f17098969273a6a7991dea8d49ee50aeb5d8d4ff1fcfda4ab85a715@127.0.0.1:50505?discport=0"]


> klay.blockNumber
1939
> subbridge.latestAnchoredBlockNumber
1910
> subbridge.childOperatorBalance
> subbridge.parentOperatorBalance
506575000000000000
> klay.blockNumber
1966
> klay.blockNumber
2012
>
klay.getBalance("0x9c4d8c2a7ba6c3316cb6abad2373b7c976912b56")
部署bridge
ubuntu@ip-172-31-22-236:~/klay-servicechain/erc20$ sudo node erc20-deploy.js
------------------------- erc20-deploy START -------------------------
info.bridge: 0x4e0C5Bd72820af2815936037343fd4511C49cB04
info.token: 0x4B2a72a43fd6E07FF3679523023898d91356eeaB
info.bridge: 0xD3D2f9A31731e6c6a27122f3869547358506f098
info.token: 0x01EEF0F6661CB84d81701C7D0cD7Cf4E83b2F643
/home/ubuntu/klay-servicechain/node_modules/caver-js/packages/caver-core-helpers/src/errors.js:87
        return new Error(`Returned error: ${message}`)
               ^

Error: Returned error: insufficient funds of the sender for value
    at Object.ErrorResponse (/home/ubuntu/klay-servicechain/node_modules/caver-js/packages/caver-core-helpers/src/errors.js:87:16)
    at /home/ubuntu/klay-servicechain/node_modules/caver-js/packages/caver-core-requestmanager/src/index.js:155:44
    at XMLHttpRequest.request.onreadystatechange (/home/ubuntu/klay-servicechain/node_modules/caver-js/packages/caver-core-requestmanager/caver-providers-http/src/index.js:122:13)
    at XMLHttpRequestEventTarget.dispatchEvent (/home/ubuntu/klay-servicechain/node_modules/xhr2-cookies/dist/xml-http-request-event-target.js:34:22)
    at XMLHttpRequest._setReadyState (/home/ubuntu/klay-servicechain/node_modules/xhr2-cookies/dist/xml-http-request.js:208:14)
    at XMLHttpRequest._onHttpResponseEnd (/home/ubuntu/klay-servicechain/node_modules/xhr2-cookies/dist/xml-http-request.js:318:14)
    at IncomingMessage.<anonymous> (/home/ubuntu/klay-servicechain/node_modules/xhr2-cookies/dist/xml-http-request.js:289:61)
    at IncomingMessage.emit (node:events:538:35)
    at endReadableNT (node:internal/streams/readable:1345:12)
    at processTicksAndRejections (node:internal/process/task_queues:83:21)
