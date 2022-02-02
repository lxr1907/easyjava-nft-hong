#!/bin/bash
base=/root/easyJava-nft/easy-java-eth-nft
cd $base
git pull
mvn clean package -Dmaven.test.skip=true
