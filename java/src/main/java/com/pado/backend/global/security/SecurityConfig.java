// package com.pado.backend.global.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//                 .csrf(csrf -> csrf.disable())
//                 .authorizeHttpRequests(auth -> auth
//                                 .anyRequest().permitAll()
//                 )
//                 .formLogin(login -> login
//                                 .disable()
//                                 .loginPage("/login") // 로그인 페이지 리디렉션 방지
//                 )
//                 .httpBasic(basic -> basic.disable())
//                 .logout(logout -> logout.disable());

//     return http.build();
// }
// }
