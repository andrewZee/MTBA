package com.demo.mtba;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;

public class HTTPServerTestIT {

    private static HttpClient client;

    @BeforeClass
    public static void startApplication() throws IOException {
        Application.main(null);
        client = HttpClient.newHttpClient();
    }

    @Test
    public void shouldPerformTransfer() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=100"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Transfer completed successfully.", response.body());
    }

    @Test
    public void shouldNotPerformTransferDueToNoMoneyOnAccount() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=10000"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Transfer is not allowed.", response.body());
    }

    @Test
    public void shouldNotPerformTransferDueToAccountDoesNotExist() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?user=John&accountFrom=12AB&accountTo=34CD&amount=100"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Transfer is not allowed.", response.body());
    }

    @Test
    public void shouldNotPerformTransferDueToIncorrectUserName() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?user=WrongName&accountFrom=AB12&accountTo=CD34&amount=100"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Transfer is not allowed.", response.body());
    }

    @Test
    public void shouldNotPerformTransferDueToAmountIsNotANumber() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=IsNotANumber"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Input parameters are not valid.", response.body());
    }

    @Test
    public void shouldNotPerformTransferWhenIncorrectRequest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/transfer?IncorrectRequest"))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Input parameters are not valid.", response.body());
    }

}
