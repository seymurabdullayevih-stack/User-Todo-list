package com.proyekt.user.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import java.io.IOException;

//KODUN MƏQSƏDI:
//Bu filter qapıçı (doorman) kimidir:
//
//Hər sorğu gələndə JWT token yoxlanır
//Token düzgündürsə və vaxtı bitməyibsə istifadəçi içəri buraxılır
//Token yoxdursa, səhvdirsə və ya vaxtı bitibsə sorğu bloklana bilər

@Component
public class JwtAutohenticationFilter extends OncePerRequestFilter {  // (8.0)

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String header;
        String token;
        String username;  // ici doldurulur asagida

       header = request.getHeader("Authorization");
       ;

        if (header == null || !header.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }
       token = header.substring(7);// tokeni alirsan

        String requestPath = request.getRequestURI();

        System.out.println("1");
        try {
            username = jwtService.getUserNameByToken(token);  // burada token yoxlamaq ucun (4.0) a atir oda icinde (3.0) a isledir

            System.out.println("2");
           Object currentAuth = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

               UserDetails userDetails  = userDetailsService.loadUserByUsername(username); // burada gedib DB den maraqlanir bele bir username varmi (7.0) a gedir


                boolean expired = jwtService.isTokenExpired(token);

                if (userDetails != null && !jwtService.isTokenExpired(token)){   // username doldursa ve vaxt kecmiyibse bunu (5.0) ile yoxluyuruq.

                    // Useri iceriye alina biler SecurtyContexe qoyulur


                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities());

                    authentication.setDetails(userDetails);

                    SecurityContextHolder.getContext().setAuthentication(authentication); // istifadeci artiq taninir

                }

            }

        }catch (Exception s) {

            System.out.println("token vaxti kecib yada umumi bir problem var");
        }

        filterChain.doFilter(request,response);   // Filterlər zəncirində növbəti filterə keçid edilir
                                                  // Əgər zənciردə başqa filter varsa, ona keçir
                                                  // Əgər filter qalmayıbsa, sorğu son nöqtəyə (controller, servlet və s.) çatır
    }
}
