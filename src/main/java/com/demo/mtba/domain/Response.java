package com.demo.mtba.domain;

public class Response {

    private int responseCode;
    private String responseMessage;

    public Response(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Response)) {
            return false;
        }

        Response r = (Response) o;

        return responseCode == r.responseCode
                && responseMessage.equals(r.responseMessage);
    }

}
