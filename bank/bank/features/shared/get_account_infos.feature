#Feature: Get account infos
#Scenario: Get account infos
#	Given account "Nikolaj K.", "Bertelsen", "220866-2859" with start balance 1000
#	And account "Magnus F.", "Andersen", "050298-2419" with start balance 0
#	When I get all account infos
#	Then account info include "Nikolaj K.", "Bertelsen", "220866-2859" and a valid account id
#	And account info include "Magnus F.", "Andersen", "050298-2419" and a valid account id
#	