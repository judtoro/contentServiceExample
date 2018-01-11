package com.fox.platform.cntsrvex.infra.hndlr;

import java.util.Optional;
import com.fox.platform.cntsrvex.infra.exc.RequestException;
import com.google.inject.Inject;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import com.newrelic.agent.deps.org.apache.http.entity.ContentType;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * Endpoint handlers implementation.
 *
 * @author juan.toro
 * @author alejandra.ramirez
 *
 */
public class HandlersChannelImpl implements HandlersChannel {

  private static Logger logger = LoggerFactory.getLogger(HandlersChannelImpl.class);

  private Vertx vertx;

  @Inject
  public HandlersChannelImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  public void getChannelsPost(RoutingContext routingContext) {
    try {
      logger.info(routingContext.getBodyAsString());

      routingContext.request().bodyHandler(handler -> {
        JsonObject request = handler.toJsonObject();
        routingContext.response().setStatusCode(HttpStatus.SC_CREATED)
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
            .end(request.encodePrettily());
      });

    } catch (EncodeException e) {
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());
    }
  }


  /**
   * Get channel list
   *
   * @param routingContext
   */
  @Override
  public void getChannelsAle(RoutingContext routingContext) {
    try {

     String countryId =  Optional
          .ofNullable(routingContext.request().getParam("countryId"))
          .orElseThrow(() -> new RequestException(HttpStatus.SC_BAD_REQUEST, "Bad countryId code"));


      logger.info("CountryId " + countryId);

      vertx.eventBus().send("get_channels_ale", countryId, resp -> {

        if (resp.succeeded()) {
          logger.info("When print !!! " + resp.result().body());
          routingContext.response().setStatusCode(HttpStatus.SC_OK);
          routingContext.response().end(resp.result().body().toString());
        } else {
          logger.error("Error trying to reach Omnix!", resp.cause());
        }
      });

    } catch (RequestException e) {
      routingContext.response().setStatusCode(e.getCode())
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());
    } catch (Exception e) {
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());

    }
  }

  /**
   * Get channel list
   *
   * @param routingContext
   */
  @Override
  public void getChannelsJuan(RoutingContext routingContext) {
    try {

      String countryId =  Optional
          .ofNullable(routingContext.request().getParam("countryId"))
          .orElseThrow(() -> new RequestException(HttpStatus.SC_BAD_REQUEST, "Bad countryId code"));
      logger.info("CountryId " + countryId);

      vertx.eventBus().send("get_channels_juan", countryId, resp -> {

        if (resp.succeeded()) {
          logger.info("When print !!! " + resp.result().body());
          routingContext.response().setStatusCode(HttpStatus.SC_OK);
          routingContext.response().end(resp.result().body().toString());
        } else {
          logger.error("Error trying to reach Omnix!", resp.cause());
        }
      });

    } catch (RequestException e) {
      routingContext.response().setStatusCode(e.getCode())
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());
    } catch (Exception e) {
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getMimeType())
          .end(e.getMessage());

    }
  }

}
