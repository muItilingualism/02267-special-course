Feature: registration

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