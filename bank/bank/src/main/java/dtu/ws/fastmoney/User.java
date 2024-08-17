package dtu.ws.fastmoney;

import javax.persistence.Embeddable;

/**
 * The user who owns an account in the database.
 * 
 * The user is defined by its CPR number, first name, and last name.
 */
@Embeddable
public class User {
	private String firstName;
	private String lastName;
	private String cprNumber;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getCprNumber() {
		return cprNumber;
	}
	public void setCprNumber(String cprNumber) {
		this.cprNumber = cprNumber;
	}
	
	@Override
	public String toString() {
		return String.format("%s, %s, %s", cprNumber, firstName, lastName);
	}
}
