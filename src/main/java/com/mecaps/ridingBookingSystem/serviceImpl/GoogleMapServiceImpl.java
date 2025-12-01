package com.mecaps.ridingBookingSystem.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
/**
 * Service class responsible for interacting with Google Maps Distance Matrix API.
 * Provides utility methods to calculate distance and fare between two locations.
 */
@Service
@Slf4j
public class GoogleMapServiceImpl {
    @Value("${google.api.key}")
     private String googleApiKey;

    /**
     * RestTemplate used for making HTTP requests to Google APIs.
     */
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * This Method helps us to get the distance between two Location in km.
     * @param origin      starting point
     * @param destination  ending point
     * @return distance in kilometers (double)
     */

    public Double getDistanceInKm(String origin, String destination) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", origin)
                .queryParam("destinations", destination)
                .queryParam("key", googleApiKey)
                .toUriString();
//          return restTemplate.getForObject(url,Map.class);

        log.info(googleApiKey);

        // It extract data from map data to  into km and actual direction

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        System.out.println("Response: " + response);

        List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
        Map<String, Object> firstRow = rows.get(0);
        List<Map<String, Object>> elements = (List<Map<String, Object>>) firstRow.get("elements");
        Map<String, Object> firstElement = elements.get(0);
        Map<String, Object> distance = (Map<String, Object>) firstElement.get("distance");
        String distanceText = (String) distance.get("text");

        return Double.parseDouble(distanceText.replaceAll("[^0-9.]", ""));


    }

    /**
     * Calculates the fare between two locations based on distance.
     * 10 rs per Km
     * @param origin      pickup address or coordinates
     * @param destination drop address or coordinates
     * @return final fare amount
     */

public double calculateFare(String origin, String destination){
double distanceInKm = getDistanceInKm(origin, destination);
double farePerKm  = distanceInKm * 10.0;
return farePerKm;

}
    }

