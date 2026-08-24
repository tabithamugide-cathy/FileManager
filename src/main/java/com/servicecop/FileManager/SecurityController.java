package com.servicecop.FileManager;

import com.servicecop.FileManager.security.MyUserDetailService;
import com.servicecop.FileManager.webtoken.JwtService;
import com.servicecop.FileManager.webtoken.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class SecurityController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailService myUserDetailService;

    @GetMapping("/home")
    public String handleWelcome(){
        return "Welcome to home";
    }
    @GetMapping("/user/home")
    public String handleUserHome(){
        return "Welcome user";
    }
    @GetMapping("/admin/home")
    public String handleAdminHome(){
        return "Welcome admin";
    }

//    @GetMapping("/login")
//    public String handleLogin(){
//        return "custom_login";
//    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()
        ));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(myUserDetailService.loadUserByUsername(loginForm.username()));
        }
        else{
            throw new UsernameNotFoundException("Invalid Credentials");
        }

    }

}


