Feature: Payment

Scenario: Successful Payment
    Given a customer with id "cid1"
    And a merchant with id "mid1"
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is successful
    And the merchant has 10 kr less in his bank account
    And the customer has 10 kr more in his bank account

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

Scenario: Customer registered
    Given a customer with id "cid1"
    When the customer is registered
    Then the customer registration is successful

Scenario: Customer not registered
    Given an unregistered customer with id "cid2"
    When the customer is registered
    Then the customer registration is unsuccessful

Scenario: Merchant registered
    Given a merchant with id "mid1"
    When the merchant is registered
    Then the merchant registration is successful

Scenario: Merchant not registered
    Given an unregistered merchant with id "mid2"
    When the merchant is registered
    Then the merchant registration is unsuccessful