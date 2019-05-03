package com.demo.mtba.service;

import com.demo.mtba.domain.Account;
import com.demo.mtba.domain.Response;
import com.demo.mtba.domain.Transaction;
import com.demo.mtba.domain.TransactionStatus;
import com.demo.mtba.domain.exceptions.TransferException;
import com.sun.net.httpserver.HttpExchange;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private static final int HTTP_SUCCESS = 200;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int DEFAULT_ID = 0;
    private static final String NOT_VALID_INPUT_PARAMETERS = "Input parameters are not valid.";

    private TransferService transferService;

    public RequestHandler(TransferService transferService) {
        this.transferService = transferService;
    }

    public Response getResponse(final HttpExchange exchange) {
        final Map<String, String> queryParameters = getQueryParams(exchange.getRequestURI().getRawQuery());
        int responseCode;
        String responseMessage;
        if (isValidParameters(queryParameters)) {
            Transaction transaction = new Transaction.Builder()
                    .id(DEFAULT_ID)
                    .user(queryParameters.get("user"))
                    .accountFrom(new Account(queryParameters.get("accountFrom")))
                    .accountTo(new Account(queryParameters.get("accountTo")))
                    .amount(new BigDecimal(queryParameters.get("amount")))
                    .status(TransactionStatus.NEW)
                    .build();

            try {
                responseMessage = transferService.doTransfer(transaction);
                responseCode = HTTP_SUCCESS;
            } catch (TransferException e) {
                System.err.println(e.getMessage());
                responseMessage = e.getMessage();
                responseCode = HTTP_BAD_REQUEST;
            }

        } else {
            responseMessage = NOT_VALID_INPUT_PARAMETERS;
            responseCode = HTTP_BAD_REQUEST;
        }

        return new Response(responseCode, responseMessage);
    }

    private boolean isValidParameters(Map<String, String> queryParameters) {
        return queryParameters.containsKey("user") &&
                queryParameters.containsKey("accountFrom") &&
                queryParameters.containsKey("accountTo") &&
                queryParameters.containsKey("amount") &&
                isConvertibleToBigDecimal(queryParameters.get("amount"));
    }

    private boolean isConvertibleToBigDecimal(String amount) {
        try {
            new BigDecimal(amount);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Map<String, String> getQueryParams(String query) {
        final Map<String, String> resultMap = new HashMap<>();
        if (query != null && !"".equals(query)) {
            String[] keyValuePairs = query.split("&");
            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    resultMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return resultMap;
    }

}
