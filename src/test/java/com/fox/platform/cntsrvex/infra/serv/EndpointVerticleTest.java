package com.fox.platform.cntsrvex.infra.serv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class EndpointVerticleTest {


  private static Logger log = LogManager.getLogger();

  private static Vertx vertx;
  private int port = Integer.getInteger("http.port", 8080);

   @Before
  public void setUp(TestContext ctx) {
    log.info("Iniciando prueba");
    vertx = Vertx.vertx();
    vertx.deployVerticle(ProxyChannelsVerticleJuan.class.getName());
    vertx.deployVerticle(ProxyChannelsVerticle.class.getName());
    vertx.deployVerticle(EndpointVerticle.class.getName());
  }

   @After
  public void down(TestContext ctx) {
    log.info("Terminando el test");
    vertx.close(ctx.asyncAssertSuccess());
  }

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

   @Test
   public void testRESTMethodsJuan(TestContext ctx) {
     Async async = ctx.async();

     vertx.createHttpClient().getNow(port, "localhost", "/channels_juan?countryId='CO'", response -> {
       ctx.assertEquals(response.statusCode(), HttpStatus.SC_OK);
       response.bodyHandler(body -> {
         ctx.assertEquals(body.toJsonArray().size(), 4);
         async.complete();
       });
     });
   }

}
