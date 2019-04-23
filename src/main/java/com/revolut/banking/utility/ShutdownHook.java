package com.revolut.banking.utility;

import java.util.Objects;

public class ShutdownHook {

	public static void addShutDownHook(Thread shutDownHook){
		Objects.requireNonNull(shutDownHook, "shutDownHook");
		Runtime.getRuntime().addShutdownHook(shutDownHook);
	}
}
