package dtu.example;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

public class HelloService {
    public String hello() {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/hello");
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        response.close();

        return value;
    }
}
