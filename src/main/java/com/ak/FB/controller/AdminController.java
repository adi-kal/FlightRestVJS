package com.ak.FB.controller;

import com.ak.FB.model.Bookings;
import com.ak.FB.model.Cities;
import com.ak.FB.model.Flight;
import com.ak.FB.service.AdminService;
import com.ak.FB.service.FlightManager;
import com.ak.FB.service.UserManager;
import com.ak.FB.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/api/admin")
public class AdminController {

    private final BookingRepository bRepository;

    @Autowired
    private UserManager uManager;

    @Autowired
    private AdminService adminService;

    @Autowired
    private FlightManager fManager;

    @Autowired
    public AdminController(BookingRepository bRepository) {
        this.bRepository = bRepository;
    }

    // ✅ Add new flight
    @PostMapping("/flight/add")
    public Map<String, String> addFlight(@RequestBody Flight flight) {
        adminService.addFlight(flight);
        return Collections.singletonMap("message", "Flight added successfully");
    }

    // ✅ Add new city
    @PostMapping("/cities/add")
    public Map<String, String> addCity(@RequestBody Cities city) {
        adminService.addCity(city);
        return Collections.singletonMap("message", "City added successfully");
    }

    // ✅ Delete user by ID
    @PostMapping("/user/delete")
    public Map<String, Object> deleteUserByEmail(@RequestBody Map<String, Object> data) {
        String email = (String) data.get("email");
        Map<String, Object> result = new HashMap<>();

        boolean success = adminService.deleteUserByEmail(email);
        if (success) {
            result.put("status", 200);
            result.put("deleted", true);
            result.put("message", "User deleted successfully");
        } else {
            result.put("status", 404);
            result.put("deleted", false);
            result.put("message", "User not found");
        }

        return result;
    }


    // ✅ Get all bookings
    @GetMapping("/bookings")
    public List<Bookings> getAllBookings() {
        return bRepository.findAll();
    }

    // ✅ Get only booked flights
    @GetMapping("/booked-flights")
    public List<Map<String, Object>> getBookedFlights() {
        return adminService.getBookedFlights();
    }

    // ✅ Get only users who booked
    @GetMapping("/booked-users")
    public List<Map<String, Object>> getBookedUsers() {
        return adminService.getBookedUsers();
    }
}
