Feature: Payment
Scenario: Successful Payment
    Given a customer with id "cid1"
    And a merchant with id "mid1"
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is successful

Scenario: List of payments
    Given a customer with id "cid1"
    And a merchant with id "mid1"
    And a successful payment of 10 kr from customer "cid1" to merchant "mid1"
    When the manager asks for a list of payments
    Then the list contains the payment where customer "cid1" paid 10 kr to merchant "mid1"

Scenario: Customer is not registered
    Given an unregistered customer with id "cid2"
    Given a merchant with id "mid1"
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is not successful
    And an error message is returned saying "customer id is unknown"

Scenario: Merchant is not registered
    Given an unregistered merchant with id "mid2"
    And a customer with id "cid1"
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is not successful
    And an error message is returned saying "merchant id is unknown"