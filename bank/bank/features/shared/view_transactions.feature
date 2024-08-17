Feature: View transactions
Scenario: Successful transfer
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
	When I transfer 100 from the first account to the second account with description "supermarket"
	Then account 1 has transaction 1 of amount 100 with creditor account 2 and balance 900 and description "supermarket" and current Date/Time
	And account 2 has transaction 1 of amount 100 with debtor account 1 and balance 100 and description "supermarket" and current Date/Time

Scenario: More than one transaction
	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
	When I transfer 100 from the first account to the second account with description "supermarket"
	And I transfer 50 from the first account to the second account with description "car repairs"
	Then account 1 has transaction 1 of amount 100 with creditor account 2 and balance 900 and description "supermarket" and current Date/Time
	And account 1 has transaction 2 of amount 50 with creditor account 2 and balance 850 and description "car repairs" and current Date/Time
	And account 2 has transaction 1 of amount 100 with debtor account 1 and balance 100 and description "supermarket" and current Date/Time
	And account 2 has transaction 2 of amount 50 with debtor account 1 and balance 150 and description "car repairs" and current Date/Time
	