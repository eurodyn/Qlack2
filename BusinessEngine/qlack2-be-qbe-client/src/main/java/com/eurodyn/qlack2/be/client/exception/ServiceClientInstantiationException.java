package com.eurodyn.qlack2.be.client.exception;

@SuppressWarnings("serial")
public class ServiceClientInstantiationException extends Exception {
	public ServiceClientInstantiationException() {
		super();
	}
	public ServiceClientInstantiationException(String msg) {
		super(msg);
	}
}
