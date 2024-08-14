package dtu.example;

import static org.junit.Assert.assertEquals;

import dtu.example.model.Person;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PersonServiceSteps {
    Person person;
    PersonService service = new PersonService();

    @When("I call the person service")
    public void iCallThePersonService() {
        person = service.getPerson();
    }

    @Then("I get the name {string}")
    public void iGetTheName(String string) {
        assertEquals(string, person.getName());
    }

    @Then("I get the address {string}")
    public void iGetTheAddress(String string) {
        assertEquals(string, person.getAddress());
    }
}
