package com.mysite.sbb.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 스프링 시큐리티 설정. 의존성만 넣으면 기본값: 모든 요청 인증 필요(→ /login), POST·PUT·DELETE 는 CSRF 토큰 검사
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        // 문자열 패턴을 바로 쓴다. (교재의 new AntPathRequestMatcher(...)는 Security 7에서 제거됨)
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/resource/**").permitAll()   // 정적 파일(009-01에서 /resource 아래로 모음)
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/**").permitAll()            // 지금은 전부 허용. 로그인이 생기면(016~) 좁힌다
                        .anyRequest().authenticated()
                )
                // H2 콘솔은 화면을 frame 으로 나눠 쓴다. 기본값(DENY)이면 깨지므로 같은 사이트의 frame 은 허용
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                // H2 콘솔의 폼에는 CSRF 토큰이 없으므로 그 경로만 CSRF 검사에서 제외
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // 폼 로그인: 로그인 화면은 우리가 만든 /users/login, 같은 주소로 POST 하면 Security 가 처리
                // 성공하면 / 로, 실패하면 /users/login?error 로
                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/")
                )
                // 로그아웃은 POST /users/logout 만 받는다(기본값). GET 링크로는 로그아웃되지 않는다.
                // → 다른 사이트의 <img src=".../logout"> 같은 것으로 몰래 로그아웃시키는 것을 막는다(CSRF 토큰 필요)
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    // 비밀번호 암호화기. BCrypt: 같은 비밀번호도 매번 다른 해시(솔트 포함), 원래 값으로 되돌릴 수 없다
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
