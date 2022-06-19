package com.checkconcepts.persistence.dao;

import com.checkconcepts.persistence.model.User;
import com.checkconcepts.persistence.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    UserLocation findByCountryAndUser(String country, User user);

}
