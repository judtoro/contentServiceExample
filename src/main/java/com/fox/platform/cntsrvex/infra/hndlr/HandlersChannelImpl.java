package com.fox.platform.cntsrvex.infra.hndlr;

import java.util.Map;
import java.util.Optional;
import com.fox.platform.cntsrvex.dom.ent.JsonFields;
import com.fox.platform.cntsrvex.infra.exc.RequestException;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.JsonArray;
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
public class HandlersChannelImpl {

  private static Logger logger = LoggerFactory.getLogger(HandlersChannelImpl.class);

  private Vertx vertx;

  public HandlersChannelImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  public static void getChannelsPost(RoutingContext routingContext) {
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
          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(e.getMessage());
    }
  }


  /**
   * Get channel list
   *
   * @param routingContext
   */
  public void getChannelsAle(RoutingContext routingContext) {
    try {

      Optional<String> countryOpt =
          Optional.ofNullable(routingContext.request().getParam("countryId"));
      String countryId = countryOpt
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
          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(e.getMessage());
    } catch (Exception e) {
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(e.getMessage());

    }
  }

  public void getChannelsJuan(RoutingContext routingContext) {
    try {

      Optional<String> countryOpt =
          Optional.ofNullable(routingContext.request().getParam("countryId"));
      String countryId = countryOpt
          .orElseThrow(() -> new RequestException(HttpStatus.SC_BAD_REQUEST, "Bad countryId code"));
      logger.info("CountryId " + countryId);

      vertx.eventBus().send("get_channels_juan", countryId, resp -> {

        if (resp.succeeded()) {
          logger.info("When print !!! " + resp.result().body());
          routingContext.response().setStatusCode(HttpStatus.SC_OK);
          routingContext.response()
              .end(getFields(new JsonObject(resp.result().body().toString())).toString());
        } else {
          logger.error("Error trying to reach Omnix!", resp.cause());
        }
      });

    } catch (RequestException e) {
      routingContext.response().setStatusCode(e.getCode())
          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(e.getMessage());
    } catch (Exception e) {
      routingContext.response().setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
          .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain").end(e.getMessage());

    }
  }


  public JsonArray getFields(JsonObject json) {

    JsonArray fields = new JsonArray();

    try {
      JsonObject hitsObj = json.getJsonObject(JsonFields.HITS_OBJECT.getFieldName());
      JsonArray hitsArray = hitsObj.getJsonArray(JsonFields.HITS_ARRAY.getFieldName());


      if (hitsArray == null) {
        return fields;
      }

      hitsArray.stream().forEach(obj -> {
        JsonObject jsonHit = (JsonObject) obj;
        jsonHit.getJsonObject(JsonFields.INNER_HITS.getFieldName(), new JsonObject())
            .getJsonObject(JsonFields.GROUPS.getFieldName(), new JsonObject())
            .getJsonObject(JsonFields.HITS_OBJECT.getFieldName(), new JsonObject())
            .getJsonArray(JsonFields.HITS_ARRAY.getFieldName(), new JsonArray()).stream()
            .map(internalObject -> {
              JsonObject jsonInternalHit = (JsonObject) internalObject;

              Map<String, Object> fieldsMap =
                  jsonInternalHit.getJsonObject(JsonFields.SOURCE.getFieldName(), new JsonObject())
                      .getJsonObject(JsonFields.FIELDS.getFieldName(), new JsonObject()).getMap();

              return new JsonObject(fieldsMap);
            }).forEach(fields::add);
      });



    } catch (Exception ex) {
      logger.error("Error when parse data from Elastic: " + json.encode(), ex);
    }
    return fields;
  }
}
