package com.ak.FB.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.ak.FB.model.Cities;
import com.ak.FB.service.FlightManager;

@RestController
//@CrossOrigin("*") // Or "*" for all origins
@RequestMapping("/v1/api")
public class CityController {

    private final FlightManager flightManager;

    public CityController(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    @GetMapping("/cities")
    public List<Map<String, Object>> getCities() {
        List<Cities> cities = flightManager.getAllCities();
        return cities.stream().map(city -> {
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("id", city.getId());
            cityMap.put("name", city.getName());
            return cityMap;
        }).collect(Collectors.toList());
    }
}
