#!/bin/sh
set -e
SHOST=${1:-localhost}
SPORT=${2:-80}
SPACKAGE=${3:-dtu.ws.fastmoney}
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" -e "s/---package---/$SPACKAGE/g" < template/pom > pom.xml
mvn clean install -Dservice.host=$1 -Dservice.port=$2 
