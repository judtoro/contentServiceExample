package com.fox.platform.cntsrvex.infra.conf;

import com.fox.platform.lib.cfg.ServiceConfig;

/**
 * Micro service configuration
 * @author juan.toro
 *
 */
public interface ContentServiceExampleConfig  extends ServiceConfig {

  /**
   * Event bus address for Alejandra implementation.
   * @return
   */
  public String getAddressAle();

  /**
   * Event bus address for Juan implementation.
   * @return
   */
  public String getAddressJuan();

  /**
   * Omnix elastic search port.
   * @return
   */
  public int getOmnixPort();


}
