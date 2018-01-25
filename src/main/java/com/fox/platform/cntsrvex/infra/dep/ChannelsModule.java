package com.fox.platform.cntsrvex.infra.dep;

import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.conf.impl.ContentServiceExampleConfigImpl;
import com.fox.platform.lib.cfg.ConfigLibFactory;
import com.google.inject.AbstractModule;
import io.vertx.core.json.JsonObject;

/**
 * Google guice configuraciont module Binder.
 *
 * @author juan.toro
 *
 */
public class ChannelsModule extends AbstractModule {


  private ContentServiceExampleConfig contentServiceExampleConfig;

  public ChannelsModule(JsonObject config) {
    contentServiceExampleConfig =
        (ContentServiceExampleConfig) ConfigLibFactory.FACTORY.createServiceConfig(config,
            ContentServiceExampleConfigImpl.CONFIG_FIELD, ContentServiceExampleConfigImpl.class);
  }

  @Override
  protected void configure() {
    bind(ContentServiceExampleConfig.class).toInstance(contentServiceExampleConfig);
  }
}
