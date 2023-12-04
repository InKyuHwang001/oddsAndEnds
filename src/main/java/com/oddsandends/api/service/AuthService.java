package com.oddsandends.api.service;

import com.oddsandends.api.domain.User;
import com.oddsandends.api.exception.InvalidSignInInformation;
import com.oddsandends.api.repository.UserRepository;
import com.oddsandends.api.request.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void signin(Login login){
        User user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidSignInInformation::new);
        user.addSession();
    }
}
