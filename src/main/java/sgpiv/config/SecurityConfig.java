package sgpiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/login",
                                "/registro",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .defaultSuccessUrl("/empresas", true)

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }
}