package com.eurodyn.qlack2.common.util.exception;

/**
 * A generic exception representing a "does not exist" condition.
 */
public class QDoesNotExistException extends QException {

  public QDoesNotExistException() {
    super();
  }

  public QDoesNotExistException(String message) {
    super(message);
  }
}
