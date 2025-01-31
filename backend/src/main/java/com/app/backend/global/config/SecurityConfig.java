package com.app.backend.global.config;

import java.util.Arrays;

import com.app.backend.domain.member.jwt.JwtAuthenticationFilter;
import com.app.backend.domain.member.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtProvider jwtProvider;
	private final String[] allowedOrigins = {
		"http://localhost:3000",  // React
		"http://localhost:5173",  // Vite
		"http://localhost:8080"   // Spring Boot
	};

	@Bean       // 비밀번호 암호화
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			.authorizeHttpRequests(request -> request
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()      // 없으면 스웨거 안열림 1
				.requestMatchers("/v3/api-docs/**").permitAll()      // 없으면 스웨거 안열림 2
				.requestMatchers("/api/**").permitAll()      // 없으면 스웨거 안열림 3
				.requestMatchers("/api/v1/members/info").authenticated()
				.anyRequest().authenticated())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
		;

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
