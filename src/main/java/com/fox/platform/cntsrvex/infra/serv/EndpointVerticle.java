package com.fox.platform.cntsrvex.infra.serv;

import java.util.Optional;
import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.dep.ChannelsModule;
import com.fox.platform.cntsrvex.infra.exc.RequestException;
import com.fox.platform.lib.vrt.AbstractConfigurationVerticle;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import com.newrelic.agent.deps.org.apache.http.entity.ContentType;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Verticle exposing a REST api to get channels info from Omnix
 *
 * <ul>
 * <li>Get channels: /channels?countryId={country}</li>
 * </ul>
 *
 * @author juan.toro
 * @author alejandra.ramirez
 *
 */
public class EndpointVerticle extends AbstractConfigurationVerticle {

  private static final Logger logger = LoggerFactory.getLogger(EndpointVerticle.class);

  @Inject
  private ContentServiceExampleConfig contentServiceExampleConfig;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Guice.createInjector(new ChannelsModule(config())).injectMembers(this);

    logger.info("Start Http Server at port (default-config.json): "
        + contentServiceExampleConfig.getHttpServerOptions().getPort());

    Router router = getRouter();

    vertx.createHttpServer(contentServiceExampleConfig.getHttpServerOptions())
        .requestHandler(router::accept).listen(result -> {
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
    router.get(contentServiceExampleConfig.getPath()).handler(this::getChannels);

    return router;
  }

  private void getChannels(RoutingContext routingContext) {
    try {

      String countryId = Optional.ofNullable(routingContext.request().getParam("countryId"))
          .orElseThrow(() -> new RequestException(HttpStatus.SC_BAD_REQUEST, "Bad countryId code"));


      logger.info("CountryId " + countryId);

      vertx.eventBus().send(contentServiceExampleConfig.getAddress(), countryId, resp -> {

        if (resp.succeeded()) {
          logger.info("Result " + resp.result().body());
          routingContext.response().setStatusCode(HttpStatus.SC_OK);
          routingContext.response().end(resp.result().body().toString());
        } else {
          throw new RequestException(HttpStatus.SC_SERVICE_UNAVAILABLE,
              "Error trying to reach Omnix");
        }
      });

    } catch (RequestException e) {
      logger.error("Error trying to reach Omnix!", e);
      routingContext.response().setStatusCode(e.getCode())
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());
    } catch (Exception e) {
      logger.error("Error trying to reach Omnix!", e);
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());

    }
  }
}
