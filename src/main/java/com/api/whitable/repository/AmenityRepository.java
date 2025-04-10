package com.api.whitable.repository;

import com.api.whitable.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Amenity findByName(String name);
}
