package com.demo.mtba.web;

import com.demo.mtba.dao.DaoService;
import com.demo.mtba.domain.Response;
import com.demo.mtba.service.RequestHandler;
import com.demo.mtba.service.TransferService;
import com.demo.mtba.service.TransferServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HTTPServerImpl implements HTTPServer {

    private static TransferService transferService;

    @Override
    public void startWithDao(DaoService daoService) throws IOException {
        this.transferService = new TransferServiceImpl(daoService);
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/transfer", HTTPServerImpl::handleRequest);
        server.start();
        System.out.println("HTTP Server has been started.");
        System.out.println("Transfer example: http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=100");
    }

    private static synchronized void handleRequest(final HttpExchange exchange) throws IOException {
        printRequestInputInfo(exchange);

        RequestHandler requestHandler = new RequestHandler(transferService);
        Response response = requestHandler.getResponse(exchange);

        exchange.sendResponseHeaders(response.getResponseCode(), response.getResponseMessage().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getResponseMessage().getBytes());
        os.flush();
        exchange.close();

        printRequestOutputInfo(exchange, response);
    }

    private static void printRequestInputInfo(final HttpExchange exchange) {
        System.out.println(exchange.toString()+ ": HTTP " + exchange.getRequestMethod()
                + " request for transfer processing [URI=" + exchange.getRequestURI() + "]");
    }

    private static void printRequestOutputInfo(final HttpExchange exchange, final Response response) {
        System.out.println(exchange.toString()+ ": Response code = " + response.getResponseCode()
                + ", Response Message = " + response.getResponseMessage());
    }

}
