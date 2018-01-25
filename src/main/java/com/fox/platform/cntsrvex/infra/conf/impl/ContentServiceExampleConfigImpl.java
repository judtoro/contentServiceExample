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
  private static final String DEFAULT_URL = "search-omnix-services-sh2266ar6ket7lqcnhj3dpzccu.us-east-1.es.amazonaws.com";
  private static final String DEFAULT_URL_PATH = "/omnix_es/contentObjects/_search";
  private static final String DEFAULT_PAYLOAD = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.description\":\"olympicschannel\"}},{\"nested\":{\"path\":\"groups\",\"query\":{\"bool\":{\"must\":[{\"nested\":{\"path\":\"groups.feeds\",\"query\":{\"match\":{\"groups.feeds.countryId\":\"${countryId}\"}}}}]}},\"inner_hits\":{}}}]}},\"sort\":[{\"groups.fields.id.raw\":{\"nested_path\":\"groups\",\"order\":\"asc\"}}]}";
  private static final String DEFAULT_PATH = "/channels";


  private HttpServerOptions httpServerOptions;
  private String address;
  private int omnixPort;
  private String omnixUrl;
  private String omnixPath;
  private String omnixRequestPayload;
  private String path;


  public ContentServiceExampleConfigImpl() {
    this.httpServerOptions = new HttpServerOptions();
    this.httpServerOptions.setPort(DEFAULT_PORT);

    this.address = DEFAULT_ADDRESS;
    this.omnixPort = DEFAULT_OMNIX_PORT;
    this.omnixUrl = DEFAULT_URL;
    this.omnixPath = DEFAULT_URL_PATH;
    this.omnixRequestPayload = DEFAULT_PAYLOAD;
    this.path = DEFAULT_PATH;
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

  @Override
  public String getOmnixUrl() {
    return omnixUrl;
  }

  public void setOmnixUrl(String omnixUrl) {
    this.omnixUrl = omnixUrl;
  }

  @Override
  public String getOmnixPath() {
    return omnixPath;
  }

  public void setOmnixPath(String omnixPath) {
    this.omnixPath = omnixPath;
  }

  @Override
  public String getOmnixRequestPayload() {
    return omnixRequestPayload;
  }

  public void setOmnixRequestPayload(String omnixRequestPayload) {
    this.omnixRequestPayload = omnixRequestPayload;
  }

  @Override
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

}
