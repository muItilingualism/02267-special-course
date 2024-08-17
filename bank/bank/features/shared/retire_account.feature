Feature: Retire account
Scenario: Retire existing account
	Given an existent account "Tosh A.", "Petersen", "130840-3897"
	When I retire that account
	Then I can create a new account with the same information
	And I don't get an error messsage
	
Scenario: Retire a non existing account
	Given that account Id "12345" does not exist
	When I retire that account
	Then I get the error message "Account does not exist"
	
Scenario: Retire a non existing account
	Given that account Id is null
	When I retire that account
	Then I get the error message "Account does not exist"