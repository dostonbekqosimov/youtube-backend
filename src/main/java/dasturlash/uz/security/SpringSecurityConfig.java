package dasturlash.uz.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    public static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };


    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {


        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http

                .authorizeHttpRequests(hrr -> {
                    hrr
                            // Authentication APIs - open to all users
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/attach/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/channels/**").permitAll()
                            .requestMatchers("api/videos/category").permitAll()
                            .requestMatchers("api/videos/title").permitAll()
                            .requestMatchers("api/videos/watch").permitAll()
                            .requestMatchers("api/videos/channel/**").permitAll()
                            .requestMatchers("api/videos/tag").permitAll()
                            .requestMatchers(AUTH_WHITELIST).permitAll()

                            //Authentication APIs - open to authenticated users
                            .requestMatchers("profile/updateEmail").authenticated()
                            .requestMatchers("/profile/changePassword").authenticated()
                            .requestMatchers("/profile/updateDetails").authenticated()
                            .requestMatchers("/profile/confirm/**").authenticated()
                            .requestMatchers("/profile/updateAttach/**").authenticated()
                            .requestMatchers("/profile/getProfileDetails/**").authenticated()
                            .requestMatchers("/playlist/create").authenticated()
                            .requestMatchers("/playlist/change-status").authenticated()
                            .requestMatchers("/playlist/update/*").authenticated()
                            //Authentication APIs - open to ADMIN and USER role
                            .requestMatchers("/playlist/delete").hasAnyRole("USER", "ADMIN")
                            //Authentication APIs - open to ADMIN role
                            .requestMatchers("/profile/create").hasRole("ADMIN")
                            .requestMatchers("/profile/getAll").hasRole("ADMIN")


                            .anyRequest()
                            .authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(httpSecurityCorsConfigurer -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowCredentials(true); // Add this line

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);
                    httpSecurityCorsConfigurer.configurationSource(source);
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                );


        return http.build();
    }


}
