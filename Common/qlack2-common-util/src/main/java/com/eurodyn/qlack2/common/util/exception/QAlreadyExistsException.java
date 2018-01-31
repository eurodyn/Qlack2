package com.eurodyn.qlack2.common.util.exception;

/**
 * A generic exception representing an "already exists" condition.
 */
public class QAlreadyExistsException extends QException {

  public QAlreadyExistsException() {
    super();
  }

  public QAlreadyExistsException(String message) {
    super(message);
  }
}
