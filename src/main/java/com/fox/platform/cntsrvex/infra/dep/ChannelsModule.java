package com.fox.platform.cntsrvex.infra.dep;

import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.conf.impl.ContentServiceExampleConfigImpl;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannel;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannelImpl;
import com.fox.platform.lib.cfg.ConfigLibFactory;
import com.google.inject.AbstractModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Google guice configuraciont module Binder.
 *
 * @author juan.toro
 *
 */
public class ChannelsModule extends AbstractModule {


  private final Vertx vertx;
  private ContentServiceExampleConfig contentServiceExampleConfig;

  public ChannelsModule(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    contentServiceExampleConfig = (ContentServiceExampleConfig) ConfigLibFactory
        .FACTORY
        .createServiceConfig(
                config,
                ContentServiceExampleConfigImpl.CONFIG_FIELD,
                ContentServiceExampleConfigImpl.class
                );
  }

  @Override
  protected void configure() {
    bind(Vertx.class).toInstance(vertx);
    bind(HandlersChannel.class).to(HandlersChannelImpl.class);
    bind(ContentServiceExampleConfig.class).toInstance(contentServiceExampleConfig);
  }
}
