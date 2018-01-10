package com.fox.platform.cntsrvex.infra.exc;

public class RequestException extends RuntimeException {

  private static final long serialVersionUID = 1653024775690784495L;

  private final int code;

  public RequestException(int code, String message) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

}
