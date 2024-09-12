# Info

This is a clone of task_rest2-monolith-event but as microservices instead of a monolith.

# Setup

Run the `setup.sh` script to build the bank image.

# Run

Run the `run.sh` script to start the bank and SimpleDTUPay

Run `docker-compose down` to shut it down.

# Test

Run the `test.sh` script to start the bank, SimpleDTUPay and run the end-to-end client-tests. 

Run `docker-compose down` to shut it down
Run the `test.sh` script again to retest.
