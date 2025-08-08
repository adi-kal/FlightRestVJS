package com.ak.FB.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ak.FB.helpers.ThreadManager;
import com.ak.FB.model.Bookings;
import com.ak.FB.model.Cities;
import com.ak.FB.model.Flight;
import com.ak.FB.model.Member;
import com.ak.FB.model.Traveler;
import com.ak.FB.repository.BookingRepository;
import com.ak.FB.repository.CitiesRepository;
import com.ak.FB.repository.FlightRepository;
import com.ak.FB.repository.MemberRepository;
import com.ak.FB.repository.TravelerRepository;

@Service
public class FlightManager {

    private FlightRepository fRepository;
    private CitiesRepository cRepository;
    private MemberRepository mRepository;
    private TravelerRepository tRepository;
    private BookingRepository bRepository;

    public FlightManager(FlightRepository fr, CitiesRepository cr, MemberRepository mr, TravelerRepository tr, BookingRepository br) {
        this.fRepository = fr;
        this.cRepository = cr;
        this.mRepository = mr;
        this.tRepository = tr;
        this.bRepository = br;
    }

    public Map<String, Object> lookForFlight(String from, String to, Map<Object, Object> data) {
        Callable<Map<String, Object>> lookFlights = () -> {
            Map<String, Object> returnObject = new HashMap<>();
            List<Flight> returnFListObject;

            Map<String, Cities> flightData = extractCitys(from, to, data);
            returnFListObject = fRepository.findFlights(LocalDate.parse(data.get("start").toString()), flightData.get("from").getId(), flightData.get("to").getId());
            returnObject.put("available", returnFListObject);
            return returnObject;
        };

        Future<Map<String, Object>> result = ThreadManager.executeThread(lookFlights);
        try {
            return result.get(); // This will block until the result is available
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exceptions as needed
        }
    }

private Map<String,Cities> extractCitys(String from, String to, Map<Object,Object> data){

    Map<String,Cities> cityData = new HashMap<>();

    List<Cities> availableCities = cRepository.findCities(from, to);

    // Log the cities being searched for
    System.out.println("Searching for cities from: " + from + " to: " + to);
    System.out.println("Available cities: " + availableCities);

    if (availableCities.isEmpty()) {
        throw new IllegalArgumentException("No cities found for the given from/to parameters.");
    }

    if (availableCities.size() < 2) {
        throw new IllegalArgumentException("Not enough cities found for the given from/to parameters.");
    }

    if (availableCities.get(0).getName().equals(from)) {
        cityData.put("from", availableCities.get(0));
        cityData.put("to", availableCities.get(1));
    } else {
        cityData.put("to", availableCities.get(0));
        cityData.put("from", availableCities.get(1));
    }

    return cityData;
}

    // Other methods remain unchanged...



    public Integer bookTheFlight(String email,Map<Object,Object> data){


        Map<String,Object> memberExist_Check = memberExist(email);
        if((Boolean) memberExist_Check.get("exist") == false){
            return 1;
        }

        Map<String,Object> flightExist_Check = flightExist(Long.parseLong(data.get("flight_id").toString()));
        if((Boolean) flightExist_Check.get("exist") == false){
            return 2;
        }

        Traveler t = extractTraveler_data(data);
        Member mem = (Member) memberExist_Check.get("type");
        System.out.println(mem);
        t.setMemberId(mem.getId());
        t.setCreated(LocalDate.now());

       Traveler savedTraveler = tRepository.save(t);


        Bookings bookObj = createBooking_Obj(flightExist_Check,t);
        bRepository.save(bookObj);
        return 200;

    }

    private Bookings createBooking_Obj(Map<String,Object> flightExist_Check, Traveler t) {
        Bookings b = new Bookings();
        b.setMember_id(t.getMemberId().intValue());
        b.setTraveler_id(t.getId().longValue());

        Flight flight =(Flight) flightExist_Check.get("type");
        b.setFlight_id(flight.getId().intValue());
        b.setTicketno(UUID.randomUUID().toString().split("-")[0]);
        b.setSeatno(generateRandomSeat());
        b.setDate(flight.getDeparture_date());
        b.setTime(flight.getDeparture_time());
        b.setPrice(flight.getPrice());
        b.setFrom_city_id(flight.getFrom_city_id());
        b.setTo_city_id(flight.getTo_city_id());

        return b;
        
    }

private Traveler mineTravelerData_db(Traveler t) {
    return tRepository.findByEmailAndMemberId(t.getEmail(), t.getMemberId());
}


    private Traveler extractTraveler_data(Map<Object,Object> data){

        Traveler trav = new Traveler();
        trav.setFirst_name(data.get("passenger_name").toString());
        trav.setSurname(data.get("passenger_surname").toString());
       trav.setAge(0); // Or set as optional
trav.setGender("N/A"); // Or null if allowed
        trav.setEmail(data.get("passenger_email").toString());

        return trav;
    }

    private Map<String,Object> flightExist(long flightId){

        Optional<Flight> flight = fRepository.findById(flightId);

        Flight exist = null;
        Map<String,Object> returnObj = new HashMap<>();

        if (flight.isPresent()) {
            exist = flight.get();
        }

        if(exist != null){
            returnObj.put("exist", true);
            returnObj.put("type", exist);
            return returnObj;
        }else{
            returnObj.put("exist", false);
            returnObj.put("type", null);
            return returnObj;
        }

    }

    private Map<String,Object> memberExist(String email){

        Member mem = mRepository.findByEmail(email);
        Map<String,Object> returnObj = new HashMap<>();
        
        if(mem != null){
            returnObj.put("exist", true);
            returnObj.put("type", mem);
            return returnObj;
        }else{
            returnObj.put("exist", false);
            returnObj.put("type", null);
            return returnObj;
        }
    }

public List<Cities> getAllCities() {
    return cRepository.findAll();
}

@Autowired
private BookingRepository bookingRepository;

private String generateRandomSeat() {
    String[] rows = {"A", "B", "C", "D", "E", "F"};
    int seatNum = (int) (Math.random() * 30) + 1; // 1 to 30
    String row = rows[(int) (Math.random() * rows.length)];
    return seatNum + row; // Example: "12A", "7C"
}
public Member getMemberByEmail(String email) {
    return mRepository.findByEmail(email);
}
public List<Bookings> getBookingsByMemberId(Long memberId) {
    return bRepository.findByMemberId(memberId);
}


}

