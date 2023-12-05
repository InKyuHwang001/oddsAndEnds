package com.oddsandends.api.service;

import com.oddsandends.api.domain.Session;
import com.oddsandends.api.domain.Member;
import com.oddsandends.api.exception.InvalidSignInInformation;
import com.oddsandends.api.repository.MemberRepository;
import com.oddsandends.api.request.Login;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository userRepository;

    @Transactional
    public String signIn(Login login){
        Member user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidSignInInformation::new);
        Session session = user.addSession();

        return session.getAccessToken();
    }
}
