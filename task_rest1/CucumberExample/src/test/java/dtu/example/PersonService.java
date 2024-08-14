package dtu.example;

import dtu.example.model.Person;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class PersonService {

    public Person getPerson() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/person");

        String response = target.request(MediaType.APPLICATION_JSON).get(String.class);

        Jsonb jsonb = JsonbBuilder.create();
        Person person = jsonb.fromJson(response, Person.class);

        client.close();

        return person;
    }

    public Response updatePerson(Person updatedPerson) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/person");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(updatedPerson, MediaType.APPLICATION_JSON));

        client.close();

        return response;
    }

    public Response resetPerson() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/person/reset");

        Response response = target.request().post(Entity.text(""));
        client.close();

        return response;
    }
}