package com.mergeteam.service.impl;

import com.mergeteam.crypto.CryptoTool;
import com.mergeteam.entity.AppUser;
import com.mergeteam.enums.UserState;
import com.mergeteam.repository.AppUserRepository;
import com.mergeteam.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final CryptoTool cryptoTool;
    @Value("${telegram.service.mail.uri}")
    private String mailServiceUri;

    @Transactional
    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return """
                    Вам на почту уже отправлено письмо.
                    Перейдите по ссылке в письме для подтверждения регистрации.""";
        }
        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        this.appUserRepository.save(appUser);
        return "Введите, пожалуйста, ваш email";
    }

    @Override
    public String setEmail(AppUser appUser, @Email String email) {

    }

}
