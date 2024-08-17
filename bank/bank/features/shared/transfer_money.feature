Feature: Transfer Money
Scenario: Successful transfer
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
	When I transfer 100 from the first account to the second account with description "supermarket"
	Then the balance of the first account is 900
	And the balance of the second account is 100
	
Scenario: Debitor account does not exist
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And a non existent account id "not exists1"
	When I transfer 100 from the first account to the second account with description "sports"
	Then I get the error message "Creditor account does not exist"
		
Scenario: Creditor account does not exist
	Given a non existent account id "not exists1"
	And account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	When I transfer 100 from the first account to the second account with description "car repair"
	Then I get the error message "Debtor account does not exist"

Scenario: Debtor balance will get negative
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 0
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 1000
	When I transfer 100 from the first account to the second account with description "car repair"
	Then I get the error message "Debtor balance will be negative"
	
Scenario: Transfer amount needs to be positive
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
	When I transfer -100 from the first account to the second account with description "car repair"
	Then I get the error message "Amount must be positive"
	
Scenario: Description is required
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
	When I transfer 100 from the first account to the second account with description ""
	Then I get the error message "Description missing"
	