package com.servicecop.FileManager;

import com.servicecop.FileManager.security.MyUserDetailService;
import com.servicecop.FileManager.webtoken.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   @Autowired
   private JwtService jwtService;

   @Autowired
   private MyUserDetailService myUserDetailService;

    @Override
    //check for authorization header
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authheader = request.getHeader("Authorization");
        if(authheader == null || !authheader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        //pass username and validate jwt token
        String jwt = authheader.substring(7);
        String username = jwtService.extractUsername(jwt);
        if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
          UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
          if(userDetails != null && jwtService.isTokenValid(jwt)){
              UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                      username,
                      userDetails.getPassword(),
                      userDetails.getAuthorities()
              );//set request as logged in
              authenticationToken.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));
              SecurityContextHolder.getContext().setAuthentication(authenticationToken);

          }
        }
        filterChain.doFilter(request, response);
    }
}
