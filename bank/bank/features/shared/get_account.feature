Feature: Get account by CPR number
Scenario: Get account success
	Given a user with first name "Christina M.", last name "Koch", and CPR number "271096-1002" 
	When creating an account 
	And getting the account by CPR number
	Then the account has the correct account id
	And the first name is "Christina M.", last name is "Koch", and CPR number is "271096-1002"

Scenario: Get non-existent account
	When getting an account with CPR number "123-non-existent"
	Then I get the error message "Account does not exist"
