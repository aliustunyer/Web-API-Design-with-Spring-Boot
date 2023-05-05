package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import com.promineotech.jeep.Constants;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;
import com.promineotech.jeep.entity.Jeep;


class FetchJeepTest {
  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
  @ActiveProfiles("test")

  @Sql(scripts = {
        "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
        "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
      config = @SqlConfig(encoding = "utf-8"))
  
  class TestsThatDoNotPolluteTheApplicationContext extends FetchJeepTestSupport{
    
  @Autowired
  private JdbcTemplate jdbcTemplate;
  
 
  @Test
  void testDb() {
    // to read the number of rows in the table 
    int numrows =JdbcTestUtils.countRowsInTable(jdbcTemplate, "customers");
    System.out.println("number of rows =" + numrows);
  }
  
  @ParameterizedTest
  @MethodSource("com.promineotech.jeep.controller.FetchJeepTest#parametersForInvalidInput")
  void testThatAnErrorMessageIsReturnedWhenAnInvalidValueIsSupplied(
      String model, String trim, String Reason ) {
    
    String uri = 
        String.format ("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
    
    ResponseEntity<Map<String, Object>> response = 
       getRestTemplate()
       .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
    
    //Then : a not found (404) status code is returned
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    
    //And : an error message is returned
    Map<String, Object> error = response.getBody();
    
    assertErrorMessageValid(error, HttpStatus.BAD_REQUEST);
    
  }
  
  @Test
  void testThatAnErrorMessageIsReturnedWhenAnUnknownTrimIsSupplied() {
    
    //Given : a valid model, trim and URI
    JeepModel model = JeepModel.WRANGLER;
    String trim = "Unknown Value";
    String uri = 
        String.format ("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
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
    
    assertErrorMessageValid(error, HttpStatus.NOT_FOUND);
    
  }
 
  @Test
  void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
    
    //Given : a valid model, trim and URI
    JeepModel model = JeepModel.WRANGLER;
    String trim = "Sport";
    String uri = 
        String.format ("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
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
  }
  static Stream<Arguments>parametersForInvalidInput(){
    //@formatter: 0ff
  return Stream.of(
   arguments("WRANGLER", "@#$%@$%#", "Trim contains non-alpha-numeric chars"),
   arguments("WRANGLER", "C".repeat(Constants.TRIM_MAX_LENGTH + 1), "Trim contains non-alpha-numeric chars"),
   arguments("INVALID", "Sport", "Model is not enum value")
        );
    //@formatter: on
  }
  @Nested
  @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
  @ActiveProfiles("test")

  @Sql(scripts = {
        "classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
        "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, 
      config = @SqlConfig(encoding = "utf-8"))
  class TestsThatPolluteTheApplicationContext extends FetchJeepTestSupport{
    @MockBean
    private JeepSalesService jeepSalesService;
    @Test
    void testThatAnUnplannedErrorResultsInA500Statu() {
      //When : a connection is made to the URI
      JeepModel model = JeepModel.WRANGLER;
      String trim = "Sport";
      String uri = 
          String.format ("%s?model=%s&trim=%s", getBaseUriForJeeps(), model, trim);
       
      doThrow (new RuntimeException("Ouch!")).when(jeepSalesService).
       fetchJeeps(model, trim);
      
      ResponseEntity<Map<String, Object>> response = 
         getRestTemplate()
         .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
      
   //Then : an internal server error (500) status is returned
   assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      
      //And : an error message is returned
      Map<String, Object> error = response.getBody();
      
      assertErrorMessageValid(error, HttpStatus.INTERNAL_SERVER_ERROR);
      
    }
  }
}
