package com.fox.platform.cntsrvex.infra.serv;

import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.dep.ChannelsModule;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannel;
import com.fox.platform.lib.vrt.AbstractConfigurationVerticle;
import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * <pre>
 * Verticle exposing a REST api to get channels info from Omnix
 *
 * 1. Get channels (Alejandra implementation): /channels_ale?countryId={country}
 * 2. Get channels (Juan implementation): /channels_juan?countryId={country}
 *
 * &#64;author juan.toro
 * &#64;author alejandra.ramirez
 * </pre>
 *
 */
public class EndpointVerticle extends AbstractConfigurationVerticle {

  private static final Logger logger = LoggerFactory.getLogger(EndpointVerticle.class);

  @Inject
  private HandlersChannel handler;

  @Inject
  private ContentServiceExampleConfig contentServiceExampleConfig;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Guice.createInjector(new ChannelsModule(vertx, config())).injectMembers(this);

    logger.info("Start Http Server at port (default-config.json): " + contentServiceExampleConfig.getHttpServerOptions().getPort());

    Router router = getRouter();

    vertx.createHttpServer(contentServiceExampleConfig.getHttpServerOptions())
      .requestHandler(router::accept)
      .listen(
          result -> {
          if (result.succeeded()) {
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private Router getRouter() {
    Router router = Router.router(vertx);


    // Routes
    router.get("/channels_ale").handler(handler::getChannelsAle);
    router.get("/channels_juan").handler(handler::getChannelsJuan);

    return router;
  }

}
