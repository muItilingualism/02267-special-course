# Fake Danish persons can be created using www.fakenamegenerator.com
Feature: Create Account 
Scenario: Create Account Success 
	Given a user with first name "Christina M.", last name "Koch", and CPR number "271096-1002" 
	When creating an account 
	Then a valid account id is assigned. 
	
Scenario: Creating the same account again 
	Given that I have created an account for user "Amanda M.", "Lassen", "100475-1066" 
	When I create the account a second time 
	Then I get the error message "Account already exists" 
	
Scenario:
Creating two accounts with same first and last name but different CPR numbers 
	Given that I have created an account for user "Amanda M.", "Lassen", "100475-1066" 
	When I create an account for "Amanda M.", "Lassen", "050945-3691" 
	Then Then I have two accounts with differennt account id 
	
Scenario: Create Account First Name empty 
	Given a user with first name "", last name "Koch", and CPR number "271096-1002" 
	When creating an account 
	Then I get the error message "Missing first name" 
	
Scenario: Create Account Last Name empty 
	Given a user with first name "Christina M.", last name "", and CPR number "271096-1002" 
	When creating an account 
	Then I get the error message "Missing last name" 
	
Scenario: Create Account CPR Number empty 
	Given a user with first name "Christina M.", last name "Koch", and CPR number "" 
	When creating an account 
	Then I get the error message "Missing CPR number" 
 
Scenario: Create an account with negative balance 
	Given a user with first name "Christina M.", last name "Koch", and CPR number "271096-1002" 
	When creating an account with balance -1
	Then I get the error message "Initial balance must be 0 or positive" 
		
 