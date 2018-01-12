package com.fox.platform.cntsrvex.infra.conf.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import io.vertx.core.http.HttpServerOptions;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentServiceExampleConfigImpl implements ContentServiceExampleConfig {

  public static final String CONFIG_FIELD = "contentServiceExample";

  private static final int DEFAULT_PORT = 8081;
  private static final String DEFAULT_ADDRESS = "get_channels";
  private static final int DEFAULT_OMNIX_PORT = 443;


  private HttpServerOptions httpServerOptions;
  private String address;
  private int omnixPort;


  public ContentServiceExampleConfigImpl() {
    this.httpServerOptions = new HttpServerOptions();
    this.httpServerOptions.setPort(DEFAULT_PORT);

    this.address = DEFAULT_ADDRESS;
    this.omnixPort = DEFAULT_OMNIX_PORT;
  }

  @Override
  public HttpServerOptions getHttpServerOptions() {
    return httpServerOptions;
  }

  public void setHttpServerOptions(HttpServerOptions httpServerOptions) {
    this.httpServerOptions = httpServerOptions;
  }

  @Override
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public int getOmnixPort() {
    return omnixPort;
  }

  public void setOmnixPort(int omnixPort) {
    this.omnixPort = omnixPort;
  }

}
