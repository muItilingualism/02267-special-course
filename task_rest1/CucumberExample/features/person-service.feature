Feature: person service

Background:
    Given the person service is reset

Scenario: person service returns the person name and address
    When I call the person service
    Then I get the name "John Doe"
    And I get the address "123 Main Street"

Scenario: update person information
    When I update the person with name "Jane Doe" and address "456 Elm Street"
    And I call the person service
    Then the person service returns the name "Jane Doe"
    And the person service returns the address "456 Elm Street"