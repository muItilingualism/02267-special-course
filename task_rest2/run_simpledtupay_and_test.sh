#!/bin/sh
set -e

run_simpledtupay() {
    
    (cd simple-dtu-pay ; mvn clean package )
    ( cd ../bank/bank_service ; ./build.sh localhost 8081 )

    if docker-compose up --build -d; then
        echo "SimpleDTUPay service started successfully"
    else
        echo "Failed to start SimpleDTUPay service"
        exit 1
    fi
}

run_client_tests() {
    cd client-test
    mvn clean verify
    if [ $? -eq 0 ]; then
        echo "End-to-end tests completed successfully"
    else
        echo "End-to-end tests failed"
        exit 1
    fi
    cd ..
}

main() {
    echo "Starting SimpleDTUPay service..."
    run_simpledtupay
    
    echo "Waiting for 10 s for SimpleDTUPay..."
    sleep 10
    
    echo "Running end-to-end tests..."
    run_client_tests
    
    echo "Stopping SimpleDTUPay service..."
    docker-compose down
    
    echo "All tests passed."
}

main
