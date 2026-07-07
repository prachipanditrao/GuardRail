package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderLifecycleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCompleteOrderLifecycle() {
        // 1. POST /orders - Create a new order matching the contract
        Map<String, Object> createRequest = new HashMap<>();
        createRequest.put("customerId", UUID.randomUUID().toString());
        
        Map<String, Object> item = new HashMap<>();
        item.put("productId", UUID.randomUUID().toString());
        item.put("quantity", 3);
        createRequest.put("items", List.of(item));

        ResponseEntity<Map> postResponse = restTemplate.postForEntity("/orders", createRequest, Map.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode(), "Should successfully create order");
        assertNotNull(postResponse.getBody());
        
        String orderId = (String) postResponse.getBody().get("id");
        assertEquals("CREATED", postResponse.getBody().get("status"));

        // 2. GET /orders/{id} - Fetch the newly created order
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("/orders/" + orderId, Map.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("CREATED", getResponse.getBody().get("status"));

        // 3. PUT /orders/{id}/status - Transition status to CONFIRMED
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("status", "CONFIRMED");
        HttpEntity<Map> putEntity = new HttpEntity<>(updateRequest, headers);
        
        ResponseEntity<Map> putResponse = restTemplate.exchange(
            "/orders/" + orderId + "/status", 
            HttpMethod.PUT, 
            putEntity, 
            Map.class
        );
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // 4. GET /orders/{id} again - Verify the state machine mutation held deterministically
        ResponseEntity<Map> finalGet = restTemplate.getForEntity("/orders/" + orderId, Map.class);
        assertEquals("CONFIRMED", finalGet.getBody().get("status"), "State mutation should be verified");
    }
}