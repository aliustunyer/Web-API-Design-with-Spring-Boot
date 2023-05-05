package com.promineotech.jeep.controller.support;

public class CreateOrderTestSupport extends BaseTest {
  protected String createOrderBody() {
    //@formatter: off
    return "{ \"customer\":\"MORISON_LINA\"," +
        "\"model\":\"WRANGLER\"," +
        "\"trim\":\"Sport Altitude\"," +
        "\"doors\":4," +
        "\"color\":\"EXT_NACHO\"," +
        "\"engine\":\"2_0_TURBO\"," +
        "\"tire\":\"35_TOYO\"," +
        "\"options\":[" +
        "\"DOOR_QUAD_4\"," +
        "\"EXT_AEV_LIFT\"," +
        "\"EXT_WARN_WINCH\"," +
        "\"EXT_WARN_BUMPER_FRONT\"," +
        "\"EXT_WARN_BUMPER_REAR\"," +
        "\"EXT_ARB_COMPRESSOR\"" +
        "]" +
        "}";
    //formatter : on
  }
}
