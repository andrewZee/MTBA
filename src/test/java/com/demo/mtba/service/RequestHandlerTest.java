package com.demo.mtba.service;

import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Response;
import com.demo.mtba.domain.Transaction;
import com.demo.mtba.domain.TransactionStatus;
import com.demo.mtba.domain.exceptions.TransferException;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {

    private static final String SUCCESS_MSG = "Transfer completed successfully.";
    private static final String FAIL_MSG = "Input parameters are not valid.";

    @Mock
    private TransferService transferService;
    @Mock
    private HttpExchange exchange;
    @InjectMocks
    private RequestHandler requestHandler;

    private Transaction transaction;

    @Before
    public void setup() throws URISyntaxException {
        transaction = new Transaction.Builder()
                .id(0L)
                .user("John")
                .accountFrom(new Account("AB12"))
                .accountTo(new Account("CD34"))
                .amount(new BigDecimal(100))
                .status(TransactionStatus.NEW)
                .build();
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=100"));
    }

    @Test
    public void shouldReturnSuccessfulResponse() throws TransferException {
        when(transferService.doTransfer(transaction)).thenReturn(SUCCESS_MSG);
        Response expected = new Response(200, SUCCESS_MSG);

        Response actual = requestHandler.getResponse(exchange);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnUnsuccessfulResponseWhenIncorrectInputParameters() throws URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8080/transfer?someBadData"));
        Response expected = new Response(400, FAIL_MSG);

        Response actual = requestHandler.getResponse(exchange);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnUnsuccessfulResponseWhenAmountIsNotNumber() throws URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("http://localhost:8080/transfer?user=John&accountFrom=AB12&accountTo=CD34&amount=isNotNumber"));
        Response expected = new Response(400, FAIL_MSG);

        Response actual = requestHandler.getResponse(exchange);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnUnsuccessfulResponseWhenTransferThrowsException() throws URISyntaxException, TransferException {
        when(transferService.doTransfer(transaction)).thenThrow(new TransferException("Failed message"));
        Response expected = new Response(400, "Failed message");

        Response actual = requestHandler.getResponse(exchange);

        assertEquals(expected, actual);
    }

}
