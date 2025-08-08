package com.ak.FB.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
public class UserManager {

    private MemberRepository mRepository;
    private BookingRepository bRepository;
    private TravelerRepository tRepository;
    private FlightRepository fRepository;
    private CitiesRepository cRepository;

    public UserManager(MemberRepository mr, BookingRepository br, TravelerRepository tr, FlightRepository fr, CitiesRepository cr){
        this.mRepository = mr;
        this.bRepository = br;
        this.tRepository = tr;
        this.fRepository = fr;
        this.cRepository = cr;
    }
    

    public Integer newSign_up(Map<Object,Object> data){

        Member mem = extractMemberFrom_request(data);
        mRepository.save(mem);
    
        return 200;
    }


    private Member extractMemberFrom_request(Map<Object,Object> data) {
        
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        String email = (String) data.get("email");

        return new Member(username,email,password);
    }


public Member loginUser(Map<Object, Object> data) {
    String email = data.get("email").toString();
    String password = data.get("password").toString();

    Member member = mRepository.findByEmail(email);

    if (member == null) return null; // User not found
    if (!member.getPassword().equals(password)) return null; // Password doesn't match

    return member; // ✅ return full user
}
   


  public Integer deleteUser (Map<Object, Object> data) {
    String email = data.get("email").toString();
    String password = data.get("password").toString();
    
    Member member = mRepository.findByEmail(email);
    if (member == null) {
        return 1; // User not found
    }

    if (!member.getPassword().equals(password)) {
        return 2; // Password does not match
    }

    // Proceed with deletion logic
    mRepository.delete(member);
    return 0; // User deleted successfully
}


    public Map<String,Object> fetchUsr_data(String email) {
        
        Member mem = mRepository.findByEmail(email);
       List<Bookings> bookings = bRepository.findByMemberId(mem.getId()); 
        Map<String,Object> userReleventInfo = getAllRelevents(bookings);
        Map<String,Object> returnObj = new HashMap<>();
        returnObj.put("user_info", userReleventInfo);
        return returnObj;
        
    }

    private Map<String,Object> getAllRelevents(List<Bookings> blist){

        List<Traveler> tlist = new ArrayList<>();
        List<Flight> flist = new ArrayList<>();
        List<Cities> clist = new ArrayList<>();
        List<String> seatnolist = new ArrayList<>();
        Map<String,Object> returnObj = new HashMap<>();

        for(Bookings b : blist){
            Optional<Traveler> texist = tRepository.findById(Long.parseLong(b.getTraveler_id().toString()));

            if(texist.isPresent()){
                tlist.add(texist.get());
            }

            Optional<Flight> fexist = fRepository.findById(Long.parseLong(b.getFlight_id().toString()));

            if(fexist.isPresent()){
                flist.add(fexist.get());
            }

            Optional<Cities> fcexist = cRepository.findById(Long.parseLong(b.getFrom_city_id().toString()));

            if(fcexist.isPresent()){
                clist.add(fcexist.get());
            }

            Optional<Cities> tcexist = cRepository.findById(Long.parseLong(b.getTo_city_id().toString()));

            if(tcexist.isPresent()){
                clist.add(tcexist.get());
            }

            seatnolist.add(b.getSeatno());

        }

        returnObj.put("traveler_info", tlist);
        returnObj.put("flight_info", flist);
        returnObj.put("cities_info", clist);
        returnObj.put("seatno",seatnolist);

        return returnObj;

    }
public int deleteUserById(Long id) {
    try {
        mRepository.deleteById(id);  // assuming mRepository is your MemberRepository
        return 200;
    } catch (Exception e) {
        e.printStackTrace();
        return 1; // or custom error code
    }
}

}
