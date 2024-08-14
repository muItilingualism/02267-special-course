Feature: person service
Scenario: person service returns the person name and address
    When I call the person service
    Then I get the name "John Doe"
    And I get the address "123 Main Street"