package com.eurodyn.qlack2.be.workflow.api.exception;

import java.text.MessageFormat;

import com.eurodyn.qlack2.common.util.exception.QException;

public class QInvalidDataException extends QException {
	
	private static final long serialVersionUID = -6166817621128992398L;
	private String message;
	private String invalidDataSource;
	private String invalidDataValue;
	private String errorCode;

	public QInvalidDataException() {
		super();
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setInvalidDataSource(String source) {
		this.invalidDataSource = source;
	}
	
	public String getInvalidDataSource()
	{
		return this.invalidDataSource;
	}
	
	public void setInvalidDataValue(String value) {
		this.invalidDataValue = value;
	}
	
	public String getInvalidDataValue()
	{
		return this.invalidDataValue;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorCode()
	{
		return this.errorCode;
	}
}
