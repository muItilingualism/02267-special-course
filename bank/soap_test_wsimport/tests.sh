#!/bin/sh
set -e
SHOST=$1
SPORT=$2
sed -e "s/---host---/$SHOST/g" -e "s/---port---/$SPORT/g" < template/pom > pom.xml
mvn clean test -Dservice.host=$1 -Dservice.port=$2 
