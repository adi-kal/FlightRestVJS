package com.ak.FB.controller;
import com.ak.FB.model.Member; 
import java.util.HashMap;
import java.util.Map;
import java.util.List;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ak.FB.model.Bookings;
import com.ak.FB.model.Cities;
import com.ak.FB.service.FlightManager;


@RestController
//@CrossOrigin("*")
@RequestMapping("/v1/api")
public class FlightController {
    private FlightManager fManager;

    public FlightController(FlightManager fm){
        this.fManager = fm;
    }

    @PostMapping("/flight/{useremail}/book")
    public Map<String,Object> bookFlight(@PathVariable(name = "useremail") String email,@RequestBody Map<Object,Object> data){
        Integer booked = fManager.bookTheFlight(email,data);
        Map<String,Object> returnObj = new HashMap<>();

        switch(booked){

            case 1 -> {
                returnObj.put("booked", false);
                returnObj.put("message", "user not found");
                return returnObj;
            }
            case 2 -> {
                returnObj.put("booked", false);
                returnObj.put("message", "flight not found");
                return returnObj;
            }
        }
           // ✅ Fetch member and their bookings
    Member member = fManager.getMemberByEmail(email);
    List<Bookings> bookings = fManager.getBookingsByMemberId(member.getId());

    // ✅ Check if booking exists
    if (bookings != null && !bookings.isEmpty()) {
        Bookings latest = bookings.get(bookings.size() - 1);
        returnObj.put("ticketno", latest.getTicketno());
    } else {
        returnObj.put("ticketno", "Unavailable");
    }

        returnObj.put("booked", true);
        
        returnObj.put("message", "happy journey");
        
        return returnObj;
    }

    
    //fetch available flight for user source destination interest
    @PostMapping("/flight/{from}/{to}")
    public Map<String,Object> findFlight(@PathVariable(name = "from") String from,@PathVariable("to") String to,@RequestBody Map<Object,Object> data){
        return fManager.lookForFlight(from,to,data);
    }

    //book the flight on this endpoint
    //takes email from url + flight id to book against + traveller details

}

