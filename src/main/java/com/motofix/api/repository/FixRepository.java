package com.motofix.api.repository;

import com.motofix.api.model.Fix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixRepository extends JpaRepository<Fix, Long> {
}