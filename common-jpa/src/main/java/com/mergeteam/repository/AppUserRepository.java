package com.mergeteam.repository;

import com.mergeteam.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long telegramUserId);
    Optional<AppUser> findByEmail(String email);

}
