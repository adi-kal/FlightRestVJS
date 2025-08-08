package com.ak.FB.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "traveler")
public class Traveler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private String first_name;
    private String surname;
    private int age;
    private String gender;
    private String email;
    private LocalDate created;

    public Traveler(){}

    public Traveler(Long memid,String first_name, String surname, int age, String gender,String email) {
        this.memberId = memid;
        this.first_name = first_name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }

    public Traveler(Long id,Long memid, String first_name, String surname, int age, String gender) {
        this.id = id;
        this.memberId = memid;
        this.first_name = first_name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Long getId() {
        return id;
    }

public Long getMemberId() {
    return memberId;
}

public void setMemberId(Long memberID) {
    this.memberId = memberID; // ✅ Use the parameter
}




    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id::" + id + "\n" +
                "first_name='" + first_name + "\n" +
                "surname='" + surname + "\n" +
                "age=" + age +"\n" +
                "gender=" + gender +"\n" +
                "created=" + created+"\n" +
                "}";
    }
}
