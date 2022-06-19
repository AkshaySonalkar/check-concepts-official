package com.checkconcepts.persistence.dao;

import com.checkconcepts.persistence.model.NewLocationToken;
import com.checkconcepts.persistence.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

    NewLocationToken findByToken(String token);

    NewLocationToken findByUserLocation(UserLocation userLocation);

}
