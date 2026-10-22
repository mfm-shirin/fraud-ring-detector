package com.example.fraudring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        String adminPassword = requirePassword("ADMIN_PASSWORD");
        String analystPassword = requirePassword("ANALYST_PASSWORD");
        String userPassword = requirePassword("USER_PASSWORD");

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetails analyst = User.withUsername("analyst")
                .password(passwordEncoder.encode(analystPassword))
                .roles("ANALYST")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, analyst, user);
    }

    private String requirePassword(String variableName) {
        String password = System.getenv(variableName);

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Required environment variable is missing: "
                            + variableName);
        }

        return password;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login")
            )
            .authorizeHttpRequests(authorize -> authorize

                .requestMatchers(
                    "/",
                    "/login.html",
                    "/css/**",
                    "/js/**",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers("/login").permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/transactions"
                ).hasAnyRole("ADMIN", "ANALYST")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/transactions"
                ).hasAnyRole("ADMIN", "USER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/fraud-alerts"
                ).hasAnyRole("ADMIN", "ANALYST")

                .requestMatchers("/index.html").authenticated()

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/index.html", true)
                .failureUrl("/login.html?error")
                .permitAll()
            )

            .exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    request -> request.getRequestURI().startsWith("/api/")
                )
            )

            .httpBasic(httpBasic -> httpBasic.disable())

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
