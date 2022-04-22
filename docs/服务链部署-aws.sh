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
sudo echo '["kni://80188a5695c6a0ac6acb86cbf95d4a9b546a72af5d26b55f67cc8c302143e1bc0bf7eee3f64bc2cbf65f361effcc44d0a9dee108ba76b44d7267fc77de29a3e4@127.0.0.1:50505?discport=0"]' > ~/data/main-bridges.json
#正式klay
# data目录 /data/klay #端口，12323,8651,8652,50505

#下载kscn，homi并解压缩

./homi setup local --cn-num 1 --test-num 1 --servicechain --chainID 1002 --p2p-port 22323 -o homi-output

#初始化kscn
kscn --datadir ~/data init ~/homi-output/scripts/genesis.json

cp ~/homi-output/scripts/static-nodes.json ~/data/
sudo cp ~/homi-output/keys/nodekey1 ~/data/klay/nodekey
#配置文件端口8551,8552改为7551，7552
sudo ./kscnd start
sudo ./kscn attach --datadir ~/data

#导入初始钱包
sudo ./kscn account import --datadir ~/data ~/homi-output/keys_test/testkey1
#密码Q开头
#返回地址d14884ea853e6a1d63d109791f7ca87a8129963b
#配置...
#SC_SUB_BRIDGE=1
#SC_PARENT_CHAIN_ID=1001
#SC_ANCHORING_PERIOD=10
> subbridge.peers.length
1
> subbridge.parentOperator
"0x04c5e71a48883cb6e6153322d72544375230ea74"
> subbridge.childOperator
"0xdec169171c407eca5f581cac25a28737cad1b684"
> klay.getBalance(subbridge.childOperator)
0
> klay.getBalance(subbridge.parentOperator)
0
> klay.getBalance("d14884ea853e6a1d63d109791f7ca87a8129963b")
10000000000
