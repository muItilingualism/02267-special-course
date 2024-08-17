#!/bin/sh
# $1 = host
# $1 = port
set -e
./build_deploy.sh $1 $2
sleep 30
./tests.sh $1 $2
# ./cleanup.sh
