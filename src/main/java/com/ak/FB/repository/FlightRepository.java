package com.ak.FB.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ak.FB.model.Flight;

import jakarta.transaction.Transactional;

@Repository
public interface FlightRepository extends JpaRepository<Flight,Long>{
    
    @Transactional
    @Query("select fi from Flight fi where fi.departure_date = :start and fi.from_city_id = :from and fi.to_city_id = :to")
    public List<Flight> findFlights(LocalDate start,Long from, Long to);
}
