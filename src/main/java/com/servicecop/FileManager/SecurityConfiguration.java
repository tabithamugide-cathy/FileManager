package com.servicecop.FileManager;

import com.servicecop.FileManager.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .authorizeHttpRequests(registry ->{
                    registry.requestMatchers("/home", "/register/**", "/authenticate").permitAll();
                    registry.requestMatchers("/user/**").hasRole("USER");
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.anyRequest().authenticated();
                })
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .formLogin(httpSecurityFormLoginConfigurer -> {
//                    httpSecurityFormLoginConfigurer.loginPage("/login").permitAll();} )
                .build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails normalUser = User.builder()
//                .username("tabitha")
//                .password("$2a$12$Vwplk2EUBC1fNLPdzjTiRu9BCPC6Q/69RImxdG6zC7d2P4z5urB62")
//                .roles("USER")
//                .build();
//
//    UserDetails adminUser = User.builder()
//            .username("admin")
//            .password("$2a$12$mw./QRATf3u3SKf7Po1bYufdCYGjFdGUBPmtEROkWSXO5dIK/Y7xO")
//            .roles("ADMIN", "USER")
//            .build();
//    return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }

    //Authentication
    @Bean
    public UserDetailsService userDetailsService(){
        return myUserDetailService;
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        // Pass UserDetailsService directly into the constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(myUserDetailService);
        //Set password encoder
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(authenticationProvider());
    }

    //passwordEncoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
