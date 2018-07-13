#!/usr/bin/env bash

$dataNode = "DFSDataServer"

findDataNode(){
    ${dataNodePid}=`ps -ef | grep "$1" | grep -v "$0" | grep -v "grep" | awk '{print $2}'`
    return ${dataNodePid}
}

stopDataNode(){
    kill -9 $1
}

startDataNode(){
    nohup sudo java -jar xxx.jar > log/data_node_server.log &
}

