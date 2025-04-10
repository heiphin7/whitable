package com.api.whitable.repository;

import com.api.whitable.model.CuisineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuisineTypeRepository extends JpaRepository<CuisineType, Long> {
    CuisineType findByName(String name);
}
