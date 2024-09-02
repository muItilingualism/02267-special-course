#!/bin/sh
set -e

run_simpledtupay() {
    
    ( cd simple-dtu-pay ; mvn clean package )

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
    
    echo "All tests passed."
    echo "To stop the containers, use 'docker-compose down'"
    echo "To rerun the tests, run this script again."
}

main
