package com.fox.platform.cntsrvex.infra.serv;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class EndpointVerticleTest {


  private static Logger log = LoggerFactory.getLogger(EndpointVerticleTest.class);

  private static Vertx vertx;
  private int port = Integer.getInteger("http.port", 8080);

  /**
   * Method that sets the necesary to perform the unit tests.
   *
   * @param ctx
   */
  @Before
  public void setUp(TestContext ctx) {
    log.info("Iniciando prueba");
    vertx = Vertx.vertx();
    vertx.deployVerticle(ProxyChannelsVerticleJuan.class.getName());
    vertx.deployVerticle(ProxyChannelsVerticle.class.getName());
    vertx.deployVerticle(EndpointVerticle.class.getName());
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
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById GIVEN a valid countryId WHEN execute the call to channles_ale
   * endpoint THEN Return a json with the information of channels fields 1. test that the response
   * is NOT NULL
   *
   * @param cxt
   */
  @Test
  public void testRESTMethodsAle(TestContext ctx) {
    Async async = ctx.async();

    vertx.createHttpClient().getNow(port, "localhost", "/channels_ale?countryId='CO'", response -> {
      ctx.assertEquals(response.statusCode(), HttpStatus.SC_OK);
      response.bodyHandler(body -> {
        ctx.assertEquals(body.toJsonArray().size(), 4);
        async.complete();
      });
    });
  }

  /**
   * Integration test with the Scenario
   *
   * SCENARIO: UpdateLevelById GIVEN a valid countryId WHEN execute the call to channles_juan
   * endpoint THEN Return a json with the information of channels fields 1. test that the response
   * is NOT NULL
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
            ctx.assertEquals(body.toJsonArray().size(), 4);
            async.complete();
          });
        });
  }

}
