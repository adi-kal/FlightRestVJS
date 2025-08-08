package com.ak.FB.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ak.FB.model.Cities;


@Repository
public interface CitiesRepository extends JpaRepository<Cities,Long>{

    @Query("select c from Cities c where name in (:from,:to)")
    public List<Cities> findCities(String from, String to);
    
}
