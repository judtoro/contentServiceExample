package com.fox.platform.cntsrvex.infra.serv;

import com.fox.platform.cntsrvex.infra.dep.EndpointModule;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannelImpl;
import com.google.inject.Guice;
import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
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
public class EndpointVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(EndpointVerticle.class);

  @Inject
  private HandlersChannelImpl handler;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    logger.debug("Start Http Server at port: " + Integer.getInteger("http.port", 8080));

    Router router = getRouter();

    vertx.createHttpServer().requestHandler(router::accept)
        .listen(Integer.getInteger("http.port", 8080), result -> {
          if (result.succeeded()) {
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private Router getRouter() {
    Router router = Router.router(vertx);

    Guice.createInjector(new EndpointModule(vertx)).injectMembers(this);

    // Routes
    router.get("/channels").handler(handler::getChannels);

    return router;
  }

}
