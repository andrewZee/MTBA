package com.demo.mtba;

import com.demo.mtba.dao.DaoService;
import com.demo.mtba.dao.DaoServiceH2Impl;
import com.demo.mtba.web.HTTPServer;
import com.demo.mtba.web.HTTPServerImpl;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        final DaoService daoService = new DaoServiceH2Impl();
        daoService.initializeDB(true);

        final HTTPServer httpServer = new HTTPServerImpl();
        httpServer.startWithDao(daoService);
    }

}
