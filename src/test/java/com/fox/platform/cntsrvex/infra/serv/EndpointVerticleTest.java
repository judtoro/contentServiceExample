package com.fox.platform.cntsrvex.infra.serv;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class EndpointVerticleTest {


  private static Logger log = LoggerFactory.getLogger(EndpointVerticleTest.class);

  private static Vertx vertx;
  private int port;

  /**
   * Method that sets the necesary to perform the unit tests.
   *
   * @param ctx
   */
  @Before
  public void setUp(TestContext ctx) {
    log.info("Iniciando prueba");

    JsonObject config = loadConfigFile();
    DeploymentOptions options = new DeploymentOptions()
        .setConfig(config);


    port = config
        .getJsonObject("contentServiceExample")
        .getJsonObject("httpServerOptions")
        .getInteger("port");

    vertx = Vertx.vertx();
    vertx.deployVerticle(ProxyChannelsVerticleJuan.class.getName(), options);
    vertx.deployVerticle(ProxyChannelsVerticle.class.getName(), options);
    vertx.deployVerticle(EndpointVerticle.class.getName(), options);
  }

  /**
   * Method that restores the enviroment after all the tests are completed.
   *
   * @param ctx
   */
  @After
  public void down(TestContext ctx) {
    log.info("Terminando el test");
    vertx.close(ctx.asyncAssertSuccess());
  }


  /**
   * <pre>
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById
   * GIVEN a valid countryId
   * WHEN execute the call to channles_ale
   * endpoint
   * THEN Return a json with the information of channels fields
   *
   * 1. test that the response
   *
   * is NOT NULL
   * </pre>
   *
   * @param cxt
   */
  @Test
  public void testRESTMethodsAle(TestContext ctx) {
    Async async = ctx.async();

    vertx.createHttpClient().getNow(port, "localhost", "/channels_ale?countryId='CO'", response -> {
      ctx.assertEquals(response.statusCode(), HttpStatus.SC_OK);
      response.bodyHandler(body -> {
        ctx.assertNotNull(body.toJsonArray());
        ctx.assertFalse(body.toJsonArray().isEmpty());
        async.complete();
      });
    });
  }

  /**
   * <pre>
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById
   * GIVEN a valid countryId
   * WHEN execute the call to channles_juan endpoint
   * THEN Return a json with the information of channels fields
   *
   * 1. test that the response
   *
   * is NOT NULL
   * </pre>
   *
   * @param cxt
   */
  @Test
  public void testRESTMethodsJuan(TestContext ctx) {
    Async async = ctx.async();

    vertx.createHttpClient().getNow(port, "localhost", "/channels_juan?countryId='CO'",
        response -> {
          ctx.assertEquals(response.statusCode(), HttpStatus.SC_OK);
          response.bodyHandler(body -> {
            ctx.assertNotNull(body.toJsonArray());
            ctx.assertFalse(body.toJsonArray().isEmpty());
            async.complete();
          });
        });
  }


  // Helper Methods to load Configuration file, and load Resource
  private JsonObject loadConfigFile() {
    return new JsonObject(loadResource(Resources.CONFIG));
  }

  private String loadResource(Resources resource) {
    InputStream in = this.getClass().getResourceAsStream(resource.getPath());
    try {
      return IOUtils.toString(in, "UTF-8");
    } catch (Exception ex) {
      return "";
    }
  }

  private enum Resources {
    CONFIG("/default-config.json");

    private String path;

    Resources(String path) {
      this.path = path;
    }

    String getPath() {
      return path;
    }
  }

}
