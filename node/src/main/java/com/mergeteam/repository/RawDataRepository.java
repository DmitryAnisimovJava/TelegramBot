package com.mergeteam.repository;

import com.mergeteam.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
