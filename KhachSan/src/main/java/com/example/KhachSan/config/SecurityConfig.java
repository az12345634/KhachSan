package com.example.KhachSan.config;

import com.example.KhachSan.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) {
        try {
            builder.userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((author) -> author.requestMatchers("/", "/login").permitAll() // config cho phép vào page login mà không cần đăng nhập
                        .requestMatchers("/views/**").permitAll() // config chỉ cho phép vào url /views/home/index khi có quyền admin
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN") // chỉ cho phép truy cập vào url /product/** khi có quyền admin
                        .requestMatchers("/views/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/image/storage").permitAll()
                        .requestMatchers("/invoice/**").permitAll()
                        .requestMatchers("https://sandbox.vnpayment.vn/paymentv2/**").permitAll() // Cho phép truy cập vào URL của VNPay
                        .requestMatchers("/process-after-login").hasAnyRole(new String[]{"ADMIN", "USER"}) // cho phép truy cập khi có quyền user hoặc admin
                        .requestMatchers("/login/**", "/common/**","/assets/**", "/frontend/**", "/image/**").permitAll()
                ).formLogin(form ->
                        form.
                                loginPage("/login") // GET  
                                .loginProcessingUrl("/authentication") // POST
                                .defaultSuccessUrl("/process-after-login")
                                .failureUrl("/login?login_status=1").permitAll()
                ).logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll());
        return http.build();
    }

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    }
}
