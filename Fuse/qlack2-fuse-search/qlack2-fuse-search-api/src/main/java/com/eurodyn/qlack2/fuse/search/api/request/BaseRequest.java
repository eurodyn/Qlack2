package com.eurodyn.qlack2.fuse.search.api.request;

public abstract class BaseRequest {
  private boolean async = false;

  public boolean isAsync() {
    return async;
  }

  public void setAsync(boolean async) {
    this.async = async;
  }
}
