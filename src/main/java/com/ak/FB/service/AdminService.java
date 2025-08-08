package com.ak.FB.service;

import com.ak.FB.model.*;
import com.ak.FB.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CitiesRepository citiesRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TravelerRepository travelerRepository;

    public void addFlight(Flight flight) {
        flightRepository.save(flight);
    }

    public void addCity(Cities city) {
        citiesRepository.save(city);
    }

    public boolean deleteUserByEmail(String email) {
        Member user = memberRepository.findByEmail(email);
        if (user != null) {
            memberRepository.delete(user);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getBookedFlights() {
        List<Bookings> bookings = bookingRepository.findAll();

        // Group by flight_id and count
        Map<Long, Long> flightCounts = bookings.stream()
            .collect(Collectors.groupingBy(b -> b.getFlight_id().longValue(), Collectors.counting()));

        List<Flight> flights = flightRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Flight flight : flights) {
            if (flightCounts.containsKey(flight.getId())) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("flight_no", flight.getFlight_no());
                obj.put("name", flight.getName());
                obj.put("from_city", flight.getFrom_city_id());
                obj.put("to_city", flight.getTo_city_id());
                obj.put("departure_date", flight.getDeparture_date());
                obj.put("departure_time", flight.getDeparture_time());
                obj.put("booked_count", flightCounts.get(flight.getId()));
                result.add(obj);
            }
        }

        return result;
    }

    public List<Map<String, Object>> getBookedUsers() {
        List<Bookings> bookings = bookingRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Bookings booking : bookings) {
            Traveler t = travelerRepository.findById(booking.getTraveler_id()).orElse(null);
            if (t != null) {
                Map<String, Object> obj = new HashMap<>();
                obj.put("email", t.getEmail());
                obj.put("flight_no", booking.getTicketno());
                obj.put("name", t.getFirst_name() + " " + t.getSurname());
                obj.put("gender", t.getGender());
                obj.put("age", t.getAge());
                result.add(obj);
            }
        }

        return result;
    }
}
