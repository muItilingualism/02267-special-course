package dtu.example;

import static org.junit.Assert.assertEquals;

import dtu.example.model.Person;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;

public class PersonServiceSteps {
    Person person;
    PersonService service = new PersonService();

    @Given("the person service is reset")
    public void thePersonServiceIsReset() {
        service.resetPerson();
    }

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

    @When("I update the person with name {string} and address {string}")
    public void iUpdateThePersonWithNameAndAddress(String name, String address) {
        Person person = new Person(name, address);

        Response response = service.updatePerson(person);
        assertEquals(200, response.getStatus());

        String json = response.readEntity(String.class);
        Jsonb jsonb = JsonbBuilder.create();

        person = jsonb.fromJson(json, Person.class);
    }

    @Then("the person service returns the name {string}")
    public void thePersonServiceReturnsTheName(String name) {
        assertEquals(name, person.getName());
    }

    @Then("the person service returns the address {string}")
    public void thePersonServiceReturnsTheAddress(String address) {
        assertEquals(address, person.getAddress());
    }
}
