#!/bin/sh
set -e
SHOST=${1:-localhost}
SPORT=${2:-80}
SPACKAGE=${3:-dtu.ws.fastmoney}
mvn clean test -Dservice.host=$SHOST -Dservice.port=$SPORT 
