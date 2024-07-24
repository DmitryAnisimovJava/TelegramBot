package com.mergeteam.service.impl;

import com.mergeteam.crypto.CryptoTool;
import com.mergeteam.entity.AppUser;
import com.mergeteam.repository.AppUserRepository;
import com.mergeteam.service.UserActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserRepository appUserRepository;
    private final CryptoTool cryptoTool;

    @Transactional
    @Override
    public boolean activation(String cryptoUserId) {
        Long id = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> userOptional = appUserRepository.findById(id);
        return userOptional.map(user -> {
            user.setActive(true);
            appUserRepository.save(user);
            return true;
        }).orElse(false);
    }
}
