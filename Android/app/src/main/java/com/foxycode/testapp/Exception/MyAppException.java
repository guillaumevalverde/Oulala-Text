package com.foxycode.testapp.Exception;

public class MyAppException extends Exception{
	
	public final static int ERROR_SERVER_INT = 121;
	public final static String ERROR_SERVER = "Pbm with the server";
	int errorCode = -1;
	
	public MyAppException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MyAppException(String message) {
		super(message);
	}
	
	public static String reportError(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}
