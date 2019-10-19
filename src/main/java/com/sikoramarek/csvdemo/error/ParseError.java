package com.sikoramarek.csvdemo.error;

public class ParseError extends RuntimeException {

	public ParseError() {
		super("Parse error");
	}

	public ParseError(String error) {
		super(error);
	}

}
