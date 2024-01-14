package com.wongweiye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

// https://www.baeldung.com/spring-webclient-resttemplate - Blocking Client based on the thread-per-request model.
// https://www.baeldung.com/spring-5-webclient - Reactive, non-blocking solution
// https://stackoverflow.com/questions/35998790/resttemplate-how-to-send-url-and-query-parameters-together
@RequestMapping(path = "/v2", produces = "application/json")
@RestController
public class ExchangeController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/get/payment/info")
    public ResponseEntity<String> GetInfo(@RequestParam("channelId") long channelId){

        String url = "http://localhost:8082/v2/payment/fund";
        String url2 = "http://localhost:8082/v2/planets/{planet}/moons/{moon}";

        // URI (URL) path variable
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("planet", "Mars");
        urlParams.put("moon", "Phobos");

        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url2)
                // Add query parameter
                .queryParam("firstName", "Mark")
                .queryParam("lastName", "Watney");

        URI uri = UriComponentsBuilder.fromUriString(url).build().toUri();
        uri = UriComponentsBuilder.fromUri(uri).queryParam("channelId", channelId).build().toUri();

        URI uri2 = UriComponentsBuilder.fromUriString(url2).build().toUri();
        uri2 = builder.buildAndExpand(urlParams).toUri();

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Do something with the response body
            String body = response.getBody();
        } else {
            System.out.println("Error of calling API");
        }
        return response;
    }
}
