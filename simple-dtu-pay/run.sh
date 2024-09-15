#!/bin/sh
( cd ./simple-dtu-pay ; mvn clean package )
( cd ./account ; mvn clean package )
( cd ./payment ; mvn clean package )


if docker-compose up --build -d; then
    echo "Container(s) succesfully started"
else
    echo "docker-compose command failed"
fi