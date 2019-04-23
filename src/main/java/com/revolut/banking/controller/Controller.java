package com.revolut.banking.controller;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.revolut.banking.service.MoneyTransferService;

public class Controller {
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);
	private static final MoneyTransferService moneyTransferService = new MoneyTransferService();

	public static void resources() {
		before((request, response) -> {
			logger.info("Processing request with resourceUri: {}, http method: {}", request.pathInfo(),
					request.requestMethod());
			response.type("application/json");
		});

		get("/ping", (request, response) -> {
			return "pong";
		});
		
		post("/v1/accounts", (request, response) -> {
			return new Gson().toJsonTree(moneyTransferService.createAccount(request, response));
		});

		get("/v1/accounts/:accNumber", (request, response) -> {
			return new Gson().toJsonTree(moneyTransferService.getAccountBalance(request, response));
		});

		post("/v1/transfers", (request, response) -> {
			return new Gson().toJsonTree(moneyTransferService.transferMoney(request, response));
		});

		get("*", (request, response) -> {
			return new Gson().toJsonTree(new GenericResponse(HttpStatus.NOT_FOUND_404,
					new Gson().toJson("Resource in Money Transfer Not Found!")));
		});

		after((request, response) -> {
			logger.info("Response for resourceUri: {}, http method: {}", request.pathInfo(), request.requestMethod());
			response.type("application/json");
		});
	}
}