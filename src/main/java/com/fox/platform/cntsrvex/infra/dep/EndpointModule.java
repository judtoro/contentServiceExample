package com.fox.platform.cntsrvex.infra.dep;

import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannel;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannelImpl;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;

/**
 * Google guice configuraciont module Binder.
 *
 * @author juan.toro
 *
 */
public class EndpointModule extends AbstractModule {


  private final Vertx vertx;

  public EndpointModule(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  protected void configure() {
    bind(Vertx.class).toInstance(vertx);
    bind(HandlersChannel.class).to(HandlersChannelImpl.class);
  }
}
