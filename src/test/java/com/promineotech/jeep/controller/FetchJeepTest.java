package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.math.BigDecimal;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Jeep;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FetchJeepTest extends FetchJeepTestSupport{
  
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
    List<Jeep> expected = buildExpected();
    assertThat(response.getBody()).isEqualTo(expected);
  }

  private List<Jeep> buildExpected() {
    List<Jeep> list = new LinkedList<>();
    
    //@formatter : off
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(2)
        .wheelSize(17)
        .basePrice(new BigDecimal("28475.00"))
        .build());
        
    list.add(Jeep.builder()
        .modelId(JeepModel.WRANGLER)
        .trimLevel("Sport")
        .numDoors(4)
        .wheelSize(17)
        .basePrice(new BigDecimal("31975.00"))
        .build());
  //@formatter : off
    
    return list;
  }

}
