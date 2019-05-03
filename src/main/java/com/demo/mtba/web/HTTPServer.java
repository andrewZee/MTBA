package com.demo.mtba.web;

import com.demo.mtba.dao.DaoService;

import java.io.IOException;

public interface HTTPServer {
    void startWithDao(DaoService daoService) throws IOException;
}
