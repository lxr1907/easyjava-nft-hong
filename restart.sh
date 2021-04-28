#!/bin/bash
ID=`ps -ef | grep "easyJava"| grep -v "$0" | grep -v "grep" | awk '{print $2}'`
echo $ID
echo "---------------"
for id in $ID
do
	kill -9 $id
	echo "killed $id"
done
echo "---------------"
nohup java -jar -Dspring.profiles.active=dev  target/easyJava.jar  2>&1 &
