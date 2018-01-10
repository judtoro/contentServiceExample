package com.fox.platform.cntsrvex.infra.serv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fox.platform.cntsrvex.infra.hndlr.HandlersChannelImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;


public class EndpointVerticle extends AbstractVerticle {

  private static final Logger logger = LogManager.getLogger();

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    logger.debug("Start Http Server at port: {}", Integer.getInteger("http.port", 8080));

    Router router = getRouter();

    vertx.createHttpServer().requestHandler(router::accept).listen(Integer.getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(result.cause());
      }
    });
  }

  private Router getRouter() {
    Router router = Router.router(vertx);
    HandlersChannelImpl handler = new HandlersChannelImpl(vertx);

    // Routes
    router.get("/channels_ale").handler(handler::getChannelsAle);
    router.get("/channels_juan").handler(handler::getChannelsJuan);

    return router;
  }

}
