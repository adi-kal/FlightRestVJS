package com.ak.FB.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ak.FB.model.Traveler;

@Repository
public interface TravelerRepository extends JpaRepository<Traveler, Long> {

   Traveler findByEmailAndMemberId(String email, Long memberId);


}

