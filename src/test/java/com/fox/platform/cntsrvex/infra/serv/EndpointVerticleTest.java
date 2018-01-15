package com.fox.platform.cntsrvex.infra.serv;

import java.io.InputStream;
import java.util.function.Consumer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
    DeploymentOptions options = new DeploymentOptions().setConfig(config);


    port = config.getJsonObject("contentServiceExample").getJsonObject("httpServerOptions")
        .getInteger("port");

    vertx = Vertx.vertx();
    vertx.deployVerticle(ProxyChannelsVerticle.class.getName(), options);
    vertx.deployVerticle(EndpointVerticle.class.getName(), options, ctx.asyncAssertSuccess());
  }

  /**
   * Method that restores the enviroment after all the tests are completed.
   *
   * @param ctx context
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
   * WHEN execute the call to channles endpoint
   * THEN Return a json with the information of channels fields
   *
   * 1. test that the response
   *
   * is NOT NULL
   * </pre>
   *
   * @param cxt context
   */
  @Test
  public void testRESTMethodsGoodCountryId(TestContext ctx) {
    String channelStr = "/channels?countryId='CO'";
    Consumer<Buffer> expected = body -> {
      ctx.assertNotNull(body.toJsonArray());
      ctx.assertFalse(body.toJsonArray().isEmpty());
    };
    testRESTMethods(ctx, channelStr, expected);
  }

  /**
   * <pre>
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById
   * GIVEN a invalid countryId
   * WHEN execute the call to channles endpoint
   * THEN Return a json with the information of channels fields
   *
   * 1. test that the response
   *
   * is NOT NULL
   * </pre>
   *
   * @param cxt context
   */
  @Test
  public void testRESTMethodsBadCountryId(TestContext ctx) {
    String channelStr = "/channels?countryId='O'";
    Consumer<Buffer> expected = body -> {
      ctx.assertNotNull(body.toJsonArray());
      ctx.assertTrue(body.toJsonArray().isEmpty());
    };
    testRESTMethods(ctx, channelStr, expected);
  }

  /**
   * <pre>
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById
   * GIVEN without countryId and with other parameter
   * WHEN execute the call to channles endpoint
   * THEN Return a json with the information of channels fields
   *
   * 1. test that the response
   *
   * is NOT NULL
   * </pre>
   *
   * @param cxt context
   */
  @Test
  public void testRESTMethodsWithoutCountry(TestContext ctx) {
    String channelStr = "/channels?country='CO";
    testRESTMethods(ctx, channelStr, HttpStatus.SC_BAD_REQUEST);
  }

  private void testRESTMethods(TestContext ctx, String channelStr, Consumer<Buffer> expected) {
    Async async = ctx.async();

    vertx.createHttpClient().getNow(port, "localhost", channelStr, response -> {
      ctx.assertEquals(response.statusCode(), HttpStatus.SC_OK);
      response.bodyHandler(body -> {
        try {
          expected.accept(body);
        } catch (Exception e) {
          log.error("Body {" + body + "}", e);
        }
        async.complete();
      });
    });
  }

  /**
   * Service invocation
   *
   * @param ctx context
   * @param channelStr Connection string
   * @param status Response
   */
  private void testRESTMethods(TestContext ctx, String channelStr, int status) {
    Async async = ctx.async();

    vertx.createHttpClient().getNow(port, "localhost", channelStr, response -> {
      ctx.assertEquals(response.statusCode(), status);
      async.complete();
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
