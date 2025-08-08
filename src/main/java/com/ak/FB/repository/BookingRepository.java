package com.ak.FB.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.ak.FB.model.Bookings;

@Repository
public interface BookingRepository extends JpaRepository<Bookings, Long> {

    @Query("SELECT b FROM Bookings b WHERE b.member_id = :id")
    List<Bookings> findByMemberId(@Param("id") Long id);
}

