#!/bin/sh
( cd ./simple-dtu-pay ; mvn clean package )
( cd ../bank/bank_service ; ./build.sh localhost 8081 )

if docker-compose up --build -d; then
    echo "Container(s) succesfully started"
else
    echo "docker-compose command failed"
fi