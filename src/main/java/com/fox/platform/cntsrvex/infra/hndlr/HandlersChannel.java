package com.fox.platform.cntsrvex.infra.hndlr;

import io.vertx.ext.web.RoutingContext;

/**
 * Interface that defines the methods of the endpoint handlers implementation.
 *
 * @author juan.toro
 *
 */
public interface HandlersChannel {


  public void getChannels(RoutingContext routingContext);
}
