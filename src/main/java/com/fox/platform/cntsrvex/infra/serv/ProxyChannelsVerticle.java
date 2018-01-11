package com.fox.platform.cntsrvex.infra.serv;

import com.fox.platform.cntsrvex.infra.util.QueryUtil;
import com.jayway.jsonpath.JsonPath;
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
 *
 */
public class ProxyChannelsVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProxyChannelsVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    vertx.eventBus().consumer("get_channels_ale", this::onMessage);
    startFuture.complete();

  }

  private void onMessage(Message<String> message) {
    try {
      logger.info("Getting channels for - CountryId: " + message.body());

      // Query
      String queryString = QueryUtil.getChannels(message.body());
      JsonObject jsonQuery = new JsonObject(queryString);

      // Result
      Future<JsonObject> resultObj = processResponse(message);

      doPetition(jsonQuery, resultObj);

    } catch (Exception e) {
      logger.error("Error getting channels fields.", e);
    }
  }

  /**
   * Method used to call Omnix.
   *
   * @param jsonObj: Json send whith the request.
   * @param resultObj: Future to handle the response.
   */
  private void doPetition(JsonObject jsonObj, final Future<JsonObject> resultObj) {
    WebClient webclient = WebClient.create(vertx);

    webclient
        .post(443, "search-omnix-services-sh2266ar6ket7lqcnhj3dpzccu.us-east-1.es.amazonaws.com",
            "/omnix_es/contentObjects/_search")
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

      JsonArray resultJson = new JsonArray();

      JsonObject obj = result.result();
      obj.getJsonObject("hits").getJsonArray("hits")
          .forEach(objHit -> ((JsonObject) objHit).getJsonObject("inner_hits")
              .getJsonObject("groups").getJsonObject("hits").getJsonArray("hits")
              .forEach(objHit2 -> resultJson
                  .add(((JsonObject) objHit2).getJsonObject("_source").getJsonObject("fields"))));

      logger.info("Final size " + resultJson.size());
      message.reply(resultJson);
    });
    return resultObj;
  }

  /**
   * Method to handle the response from Omnix. It uses the JsonPath library.
   *
   * @param message: Message object used to reply the response from Omnix.
   * @return
   */
  public Future<JsonObject> jsonPathResponse(Message<String> message) {

    final Future<JsonObject> resultObj = Future.future();
    resultObj.setHandler(result -> {

      Object expectResp = JsonPath.read(result.result().encode(),
          "$.hits.hits[*].inner_hits.groups.hits.hits[*]._source.fields");

      JsonArray resultJson = new JsonArray(expectResp.toString());
      logger.info("Expected " + resultJson);

      message.reply(resultJson);
    });
    return resultObj;
  }
}
