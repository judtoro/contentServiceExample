package com.fox.platform.cntsrvex.infra.serv;

import org.apache.commons.text.StrSubstitutor;
import com.fox.platform.cntsrvex.dom.ent.JsonFields;
import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.dep.ChannelsModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;


/**
 *
 * Proxy verticle to perform queries over Omnix
 *
 * @author alejandra.ramirez
 * @author juan.toro
 *
 *
 */
public class ProxyChannelsVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProxyChannelsVerticle.class);

  @Inject
  private ContentServiceExampleConfig contentServiceExampleConfig;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Guice.createInjector(new ChannelsModule(config())).injectMembers(this);

    vertx.eventBus().consumer(contentServiceExampleConfig.getAddress(), this::onMessage);

    startFuture.complete();

  }

  /**
   * Prosses the received message trough the event bus.
   * @param message
   */
  private void onMessage(Message<String> message) {
    try {
      logger.info("Getting channels for - CountryId: " + message.body());

      // Query
      JsonObject jsonQuery = new JsonObject(
          getPayload(contentServiceExampleConfig.getOmnixRequestPayload(), message.body()));

      // Result
      Future<JsonObject> resultObj = processResponse(message);

      getChannelsFromCMS(jsonQuery, resultObj);

    } catch (Exception e) {
      message.fail(500, "Error getting channels fields");
      logger.error("Error getting channels fields.", e);
    }
  }

  /**
   * Method used to call Omnix.
   *
   * @param jsonObj: Json send whith the request.
   * @param resultObj: Future to handle the response.
   */
  private void getChannelsFromCMS(JsonObject jsonObj, final Future<JsonObject> resultObj) {
    WebClient webclient = WebClient.create(vertx);

    webclient.post(contentServiceExampleConfig.getOmnixPort(),
        contentServiceExampleConfig.getOmnixUrl(), contentServiceExampleConfig.getOmnixPath())
        .ssl(true).sendJson(jsonObj, response -> {
          if (response.succeeded()) {
            logger.info("Call Omnix Succeed!. Response: " + response.result().bodyAsString());
            resultObj.complete(response.result().bodyAsJsonObject());
          } else {
            logger.error("Call Omnix Fail" + response.cause());
            resultObj.fail(response.cause());
          }
        });
  }

  /**
   * Method to handle the response from Omnix. It uses the json object
   *
   * @param message: Message object used to reply the response from Omnix.
   * @return
   */
  private Future<JsonObject> processResponse(Message<String> message) {

    final Future<JsonObject> resultObj = Future.future();
    resultObj.setHandler(result -> {

      try {
        JsonArray resultJson = new JsonArray();

        JsonObject obj = result.result();
        obj.getJsonObject(JsonFields.HITS_OBJECT.getFieldName())
            .getJsonArray(JsonFields.HITS_ARRAY.getFieldName()).forEach(
                objHit -> ((JsonObject) objHit).getJsonObject(JsonFields.INNER_HITS.getFieldName())
                    .getJsonObject(JsonFields.GROUPS.getFieldName())
                    .getJsonObject(JsonFields.HITS_OBJECT.getFieldName())
                    .getJsonArray(JsonFields.HITS_ARRAY.getFieldName())
                    .forEach(objHit2 -> resultJson
                        .add(((JsonObject) objHit2).getJsonObject(JsonFields.SOURCE.getFieldName())
                            .getJsonObject(JsonFields.FIELDS.getFieldName()))));

        logger.info("Final size " + resultJson.size());
        message.reply(resultJson);
      } catch (Exception e) {
        logger.error(e, e);
        message.fail(HttpStatus.SC_NOT_IMPLEMENTED, "Transformation error");
      }
    });
    return resultObj;
  }


  /**
   * Replaces the countryId token with the provided countryId.
   *
   * @param payload
   * @param countryId
   * @return
   */
  public String getPayload(String payload, String countryId) {

    JsonObject request = new JsonObject();
    request.put(JsonFields.COUNTRY.getFieldName(), countryId);

    return new StrSubstitutor(request.getMap()).replace(payload);
  }

}
