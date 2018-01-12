package com.fox.platform.cntsrvex.infra.conf.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import io.vertx.core.http.HttpServerOptions;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentServiceExampleConfigImpl implements ContentServiceExampleConfig {

  public static final String CONFIG_FIELD = "contentServiceExample";

  private static final int DEFAULT_PORT = 8081;
  private static final String DEFAULT_ADDRESS_ALE = "get_channels_ale";
  private static final String DEFAULT_ADDRESS_JUAN = "get_channels_juan";
  private static final int DEFAULT_OMNIX_PORT = 443;


  private HttpServerOptions httpServerOptions;
  private String addressAle;
  private String addressJuan;
  private int omnixPort;


  public ContentServiceExampleConfigImpl() {
    this.httpServerOptions = new HttpServerOptions();
    this.httpServerOptions.setPort(DEFAULT_PORT);

    this.addressAle = DEFAULT_ADDRESS_ALE;
    this.addressJuan = DEFAULT_ADDRESS_JUAN;
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
  public String getAddressAle() {
    return addressAle;
  }

  public void setAddressAle(String addressAle) {
    this.addressAle = addressAle;
  }

  @Override
  public String getAddressJuan() {
    return addressJuan;
  }

  public void setAddressJuan(String addressJuan) {
    this.addressJuan = addressJuan;
  }

  @Override
  public int getOmnixPort() {
    return omnixPort;
  }

  public void setOmnixPort(int omnixPort) {
    this.omnixPort = omnixPort;
  }

}
