package org.acme;

import org.acme.model.Person;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/person")
public class PersonResource {

    private Person person = new Person("John Doe", "123 Main Street");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson() {
        return person;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putPerson(Person person) {
        this.person.setName(person.getName());
        this.person.setAddress(person.getAddress());

        return Response.ok(this.person).build();
    }

    @POST
    @Path("/reset")
    public Response resetPerson() {
        this.person = new Person("John Doe", "123 Main Street");

        return Response.ok().build();
    }
}
