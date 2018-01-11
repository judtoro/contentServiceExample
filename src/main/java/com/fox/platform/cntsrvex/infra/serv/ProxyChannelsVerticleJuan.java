package com.fox.platform.cntsrvex.infra.serv;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;

/**
 *
 * Proxy verticle to perform queries over Omnix
 *
 * @author juan.toro
 *
 */
public class ProxyChannelsVerticleJuan extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProxyChannelsVerticleJuan.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Future<Void> future = Future.future();
    super.start(future);

    future.setHandler(handler -> {
      logger.info("Getting channels.......");
      vertx.eventBus().consumer("get_channels_juan", this::onMessage);
      startFuture.complete();

    });


  }

  private void onMessage(Message<JsonObject> message) {
    try {
      logger.info("Handling message. CountryId: " + message.body());

      String jsonString =
          "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.description\":\"olympicschannel\"}},"
              + "{\"nested\":{\"path\":\"groups\",\"query\":{\"bool\":"
              + "{\"must\":[{\"nested\":{\"path\":\"groups.feeds\",\"query\":"
              + "{\"match\":{\"groups.feeds.countryId\":" + "\"" + message.body() + "\""
              + "}}}}]}},\"inner_hits\":{}}}]}},\"sort\":[{\"groups.fields.id.raw\":{\"nested_path"
              + "\":\"groups\",\"order\":\"asc\"}}]}";

      JsonObject json = new JsonObject(jsonString);
      logger.info("Json: " + json);

      WebClient webclient = WebClient.create(vertx);

      webclient
          .post(443, "search-omnix-services-sh2266ar6ket7lqcnhj3dpzccu.us-east-1.es.amazonaws.com",
              "/omnix_es/contentObjects/_search")
          .ssl(true).sendJson(json, response -> {
            if (response.succeeded()) {
              logger.info("Call Omnix Succeede!. Response: " + response.result().bodyAsString());
              message.reply(response.result().bodyAsJsonObject());
            } else {
              logger.error("Call Omnix Fail", response.cause());
            }
          });

    } catch (Exception e) {
      logger.error("Error getting channels fields.", e);
    }
  }


}
