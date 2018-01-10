/**
 *
 */
package com.fox.platform.cntsrvex.infra.util;

/**
 * Class that handle the creation of queries.
 *
 * @author alejandra.ramirez
 *
 */
public class QueryUtil {

  private QueryUtil() {
    // Query class.
  }

  public static String getChannels(String countryId) {
    return "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"type.description\":\"olympicschannel\"}},"
        + "{\"nested\":{\"path\":\"groups\",\"query\":{\"bool\":"
        + "{\"must\":[{\"nested\":{\"path\":\"groups.feeds\",\"query\":"
        + "{\"match\":{\"groups.feeds.countryId\":" + "\"" + countryId + "\""
        + "}}}}]}},\"inner_hits\":{}}}]}},\"sort\":[{\"groups.fields.id.raw\":{\"nested_path"
        + "\":\"groups\",\"order\":\"asc\"}}]}";
  }
}
