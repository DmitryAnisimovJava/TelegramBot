package com.mergeteam.repository;

import com.mergeteam.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoRepository extends JpaRepository<AppPhoto, Long> {
}
