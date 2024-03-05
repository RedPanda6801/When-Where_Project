package com.example.whenwhere.Config;


import com.example.whenwhere.Jwt.JwtAccessDeniedHandler;
import com.example.whenwhere.Jwt.JwtAuthenticationEntryPoint;
import com.example.whenwhere.Jwt.JwtSecurityConfig;
import com.example.whenwhere.Jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig{
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2UserService oAuth2UserService;



    public SecurityConfig(
        TokenProvider tokenProvider,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtAccessDeniedHandler jwtAccessDeniedHandler,
        OAuth2UserService oAuth2UserService
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(csrf -> csrf.disable())
                // 기존에 만들었던 클래스로 예외처리
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .loginPage("/oauth")
                        .successHandler(successHandler())
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                )
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // 모든 이에게 허용
                        .requestMatchers(
                                new AntPathRequestMatcher("/oauth/**"),
                                new AntPathRequestMatcher("/local/oauth2/code/kakao"),
                                new AntPathRequestMatcher("/api/user/**"),
                                new AntPathRequestMatcher("/api/group/**")
                            ).permitAll()
                        // 회원인 이에게 허용
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/schedule/**"),
                                new AntPathRequestMatcher("/api/group/get-members/**"),
                                new AntPathRequestMatcher("/api/apply/apply-group/**")
                            ).hasAuthority("ROLE_USER")
                        // 그룹 호스트에게 적용
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/apply/get-apply/**"),
                                new AntPathRequestMatcher("/api/apply/process-apply/**")
                        ).hasAuthority("ROLE_HOST")
                        .anyRequest().authenticated()
                )
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return ((request, response, authentication) -> {
            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            String id = defaultOAuth2User.getAttributes().get("id").toString();
            String body = """
                    {"id":"%s"}
                    """.formatted(id);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();
        });
    }
}


