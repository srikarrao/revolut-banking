package com.revolut.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.banking.config.MainConfig;
import com.revolut.banking.controller.Controller;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		logger.info("Starting Revolut Banking Application..");
		MainConfig.getInstance().config();
		Controller.resources();
	}
}