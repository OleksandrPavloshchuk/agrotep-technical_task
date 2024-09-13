package technikal.task.fishmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // TODO create users in database
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    final UserDetails adminUserDetails = User.withUsername("admin")
        .password(passwordEncoder().encode("admin"))
        .roles("ADMIN")
        .build();

    final UserDetails userUserDetails = User.withUsername("user")
        .password(passwordEncoder().encode("user"))
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager( adminUserDetails, userUserDetails);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers("/fish/create")
            .hasAuthority("ROLE_ADMIN"))
        .authorizeHttpRequests( authorizeHttpRequests -> authorizeHttpRequests
            .anyRequest()
            .authenticated())
        .formLogin(formLogin -> formLogin
            .loginPage("/login")
            .permitAll())
        .logout( logout -> logout
            .logoutUrl("/login?logout")
            .invalidateHttpSession(true));
    return httpSecurity.build();
  }

}
