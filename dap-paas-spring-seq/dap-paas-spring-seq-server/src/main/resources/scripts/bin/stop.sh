#!/bin/bash
cd `dirname $0`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=${DEPLOY_DIR}/config
SERVER_PORT=`sed '/server.port/!d;s/.*=//' $CONF_DIR/application.properties | tr -d '\r'`
echo "SERVER_PORT:${SERVER_PORT}"
COUNT=`netstat -an | grep :${SERVER_PORT} | wc -l`
if [[ ${COUNT} -gt 0 ]]; then
    PID=$(netstat -nlp | grep :${SERVER_PORT} | awk '{print $7}' | awk -F"/" '{ print $1 }')
	kill -15 ${PID}
	echo "端口${SERVER_PORT}，PID：${PID} 已停止"
else
    echo "该端口${SERVER_PORT}对应服务未启动"
fi