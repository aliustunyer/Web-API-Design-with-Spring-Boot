package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Jeep;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")

@Sql(scripts = {
        "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
        "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
      config = @SqlConfig(encoding = "utf-8"))

class FetchJeepTest extends FetchJeepTestSupport{
  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  @Disabled
  @Test
  void testDb() {
    // to read the number of rows in the table 
    int numrows =JdbcTestUtils.countRowsInTable(jdbcTemplate, "customers");
    System.out.println("number of rows =" + numrows);
  }
  
  @Test
  void testThatAnErrorMessageIsReturnedWhenAnInvalidTrimIsSupplied() {
    
    //Given : a valid model, trim and URI
    JeepModel model = JeepModel.WRANGLER;
    String trim = "Invalid Value";
    String uri = 
        String.format ("%s?model=%s&trim=%s", getBaseUri(), model, trim);
    //System.out.println(uri);
    //When :  a connection is made to the URI
     
    //Declaring a variable response of type ResponseEntity<Map<String, Object>> 
    //a Spring framework class that represents an HTTP response, 
    //including headers, body, and status code. 
    //the body is expected to be a map with String keys and Object values
    //making an HTTP request to the API by RestTemplate class/exchange() method
   
    ResponseEntity<Map<String, Object>> response = 
       getRestTemplate()
       .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
    
    //Then : a not found (404) status code is returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    
    //And : an error message is returned
    Map<String, Object> error = response.getBody();
    
    //formatter: off
    assertThat(error)
    .containsKey("message")
    .containsEntry("status code", HttpStatus.NOT_FOUND.value())
    .containsEntry("uri","/jeeps")
    .containsKey("timestamp")
    .containsEntry("reason", HttpStatus.NOT_FOUND.getReasonPhrase());
    //formatter: on
    
  }
  @Disabled
  @Test
  void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
    
    //Given : a valid model, trim and URI
    JeepModel model = JeepModel.WRANGLER;
    String trim = "Sport";
    String uri = 
        String.format ("%s?model=%s&trim=%s", getBaseUri(), model, trim);
    //System.out.println(uri);
    //When :  a connection is made to the URI
    ResponseEntity<List<Jeep>> response = 
    getRestTemplate().exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    
    //Then : a success (OK-200) status code is returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    //And : the actual list returned is the same as the expected list
    List<Jeep> actual =response.getBody();
    List<Jeep> expected = buildExpected();
    
    //actual.forEach(jeep-> jeep.setModelPK(null));
    
    assertThat(actual).isEqualTo(expected);
  }

  private List<Jeep> buildExpected() {
    List<Jeep> list = new LinkedList<>();
    
    //@formatter : off
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(4)
        .wheelSize(17)
        .basePrice(new BigDecimal("31975.00"))
        .build());
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(2)
        .wheelSize(17)
        .basePrice(new BigDecimal("28475.00"))
        .build());
  //@formatter : off
    Collections.sort(list);
    return list;
  }

}
