package com.mecaps.ridingBookingSystem.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GoogleMapService {
    @Value("${google.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double getDistanceInKm(String origin, String destination) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", origin)
                .queryParam("destinations", destination)
                .queryParam("key", googleApiKey)
                .toUriString();


        // It extract data from map into km and actual direction
        Map response = restTemplate.getForObject(url, Map.class);
        Map element = (Map) ((Map) ((Map) ((Map) ((Map) ((Map) response.get("rows")).get(0)).get("elements")).get(0)));
        Map distance = (Map) element.get("distance");
        String distanceText = (String) distance.get("text");
        return Double.parseDouble(distanceText.replaceAll("[^0-9.]", ""));
    }

public double calculateFare(String origin, String destination){
double distanceInKm = getDistanceInKm(origin, destination);
double farePerKm  = distanceInKm * 10.0;
return farePerKm;

        }
    }

