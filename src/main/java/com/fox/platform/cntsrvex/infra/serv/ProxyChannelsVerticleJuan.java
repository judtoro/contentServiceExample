package com.fox.platform.cntsrvex.infra.serv;

import java.util.Map;
import com.fox.platform.cntsrvex.dom.ent.JsonFields;
import com.fox.platform.cntsrvex.infra.conf.ContentServiceExampleConfig;
import com.fox.platform.cntsrvex.infra.dep.ChannelsModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
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
 * @author juan.toro
 *
 */
public class ProxyChannelsVerticleJuan extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProxyChannelsVerticleJuan.class);

  @Inject
  private ContentServiceExampleConfig contentServiceExampleConfig;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Guice.createInjector(new ChannelsModule(vertx, config())).injectMembers(this);

    Future<Void> future = Future.future();
    super.start(future);

    future.setHandler(handler -> {
      logger.info("Getting channels.......");
      vertx.eventBus().consumer(contentServiceExampleConfig.getAddressJuan(), this::onMessage);
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
              message.reply(getFields(response.result().bodyAsJsonObject()));
            } else {
              logger.error("Call Omnix Fail", response.cause());
            }
          });

    } catch (Exception e) {
      logger.error("Error getting channels fields.", e);
    }
  }

  /**
   * Process the response from Omnix
   * @param json
   * @return
   */
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
