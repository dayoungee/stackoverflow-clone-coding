package com.rainbow.sof.domain.user.config;
import com.rainbow.sof.domain.user.auth.handler.authError.UserAuthenticationEntryPoint;
import com.rainbow.sof.domain.user.auth.handler.oauthHandler.OAuth2SuccessHandler;
import com.rainbow.sof.domain.user.auth.jwt.DelegateTokenService;
import com.rainbow.sof.domain.user.auth.jwt.JwtTokenizer;
import com.rainbow.sof.domain.user.auth.util.service.CustomOAuth2UserService;
import com.rainbow.sof.domain.user.config.CustomFilterConfigurer;
import com.rainbow.sof.domain.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.rainbow.sof.domain.user.util.CustomEnumUri.*;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final DelegateTokenService delegateTokenService;
    private final CustomOAuth2UserService oAuth2UserService;

    private final static String USER_DETAIL_URL="/api/v1/users";

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, DelegateTokenService delegateTokenService, CustomOAuth2UserService oAuth2UserService) {
        this.jwtTokenizer = jwtTokenizer;
        this.delegateTokenService = delegateTokenService;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new UserAuthenticationEntryPoint())
                .and()
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/v1/oath/login")
                        .userInfoEndpoint()
                        .userService(oAuth2UserService)
                        .and()
                        .successHandler(new OAuth2SuccessHandler(delegateTokenService))
                )
                .apply(customFilterConfigurers())
                .and()
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .antMatchers("/api/v1/oath/login").permitAll()
                        .antMatchers(HttpMethod.PATCH,"/api/v1/questions/**").hasRole("USER")
                        .antMatchers(HttpMethod.POST,"/api/v1/questions/**").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE,"/api/v1/questions/**").hasRole("USER")
                        .antMatchers(USER_DETAIL_URL+"/**").authenticated()// /api/v1/users 의 하위 경로는 인증되야지만 접근가능하다
                        .antMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public CustomFilterConfigurer customFilterConfigurers(){
        return new CustomFilterConfigurer(jwtTokenizer, delegateTokenService);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://ec2-52-78-15-107.ap-northeast-2.compute.amazonaws.com","http://localhost:3000"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
        /*CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        //configuration.addAllowedOrigin("http://ec2-52-78-15-107.ap-northeast-2.compute.amazonaws.com");
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(
                List.of("Authorization","X-AUTH-TOKEN")
        );

        configuration.setExposedHeaders(
                Arrays.asList("Content-Type","X-AUTH-TOKEN","Authorization")
        );

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;*/
    }




}
