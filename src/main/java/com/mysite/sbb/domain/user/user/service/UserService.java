package com.mysite.sbb.domain.user.user.service;

import com.mysite.sbb.domain.user.user.entity.SiteUser;
import com.mysite.sbb.domain.user.user.repository.UserRepository;
import com.mysite.sbb.global.exception.DataNotFoundException;
import com.mysite.sbb.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입. 중복은 저장 전에 먼저 확인해 어느 칸이 문제인지 알려 준다.
    // (DB unique 제약은 동시에 가입하는 경우까지 막는 마지막 방어선)
    @Transactional
    public SiteUser join(String username, String password, String email) {
        if (userRepository.existsByUsername(username))
            throw new ServiceException("username", "이미 사용 중인 아이디입니다.");
        if (userRepository.existsByEmail(email))
            throw new ServiceException("email", "이미 사용 중인 이메일입니다.");

        // 비밀번호는 암호화해서 저장한다. 평문은 어디에도 남기지 않는다.
        return userRepository.save(new SiteUser(username, passwordEncoder.encode(password), email));
    }

    // 로그인한 사용자 이름(Principal.getName())으로 회원 엔티티를 찾는다
    @Transactional(readOnly = true)
    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("siteuser not found"));
    }
}
