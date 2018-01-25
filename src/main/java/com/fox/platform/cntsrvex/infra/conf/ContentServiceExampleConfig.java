package com.fox.platform.cntsrvex.infra.conf;

import com.fox.platform.lib.cfg.ServiceConfig;

/**
 * Micro service configuration
 * @author juan.toro
 *
 */
public interface ContentServiceExampleConfig  extends ServiceConfig {

  /**
   * Event bus address.
   * @return
   */
  public String getAddress();

  /**
   * Omnix elastic search port.
   * @return
   */
  public int getOmnixPort();

  /**
   * Url to perform the elastic search
   * @return
   */
  public String getOmnixUrl();

  /**
   * Url path
   * @return
   */
  public String getOmnixPath();

  /**
   * Omnix payload
   * @return
   */
  public String getOmnixRequestPayload();


  /**
   * Endpoint path
   * @return
   */
  public String getPath();

}
