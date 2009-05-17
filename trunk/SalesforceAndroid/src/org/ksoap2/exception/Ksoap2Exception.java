package org.ksoap2.exception;

public class Ksoap2Exception extends Exception {
	private static final long serialVersionUID = 1L;
	private String str_;
	public Ksoap2Exception(String str) {
		this.str_ = str;
	}

	public String getMessage() {
		return this.str_;
	}
}
