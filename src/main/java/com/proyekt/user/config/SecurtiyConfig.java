package com.proyekt.user.config;


import com.proyekt.user.jwt.AuthEntryPoint;
import com.proyekt.user.jwt.JwtAutohenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurtiyConfig {

    public static final String AUTHENTICATE ="/api/auth/authenticate";

    public static final String REGISTER = "/api/auth/register";

    public static final String REFRESH = "/api/auth/refresh";

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAutohenticationFilter jwtAutohenticationFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    // Swagger endpoints
    public static final String[] SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",      // ✅ /** ilə
            "/swagger-ui.html",
            "/swagger-resources/**", // ✅ Əlavə
            "/webjars/**"            // ✅ Əlavə
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // bu methoda gelen adress filtire tabe deyil

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf ->csrf.disable())
                .authorizeHttpRequests(request ->
                        request.requestMatchers(AUTHENTICATE, REGISTER, REFRESH).permitAll()
                                .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(exception->exception  // 403 xetasini 401 cevrririk
                        .authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAutohenticationFilter, UsernamePasswordAuthenticationFilter.class); // security 2 si xaric hamisini filtere salacaq filtere salmaq ucunse
        // bunu yonlendirir (8.0) - a
        return http.build();
    }
}