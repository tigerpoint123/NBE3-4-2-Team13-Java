package com.app.backend.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.app.backend.domain.member.jwt.JwtAuthenticationFilter;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.oauth.OAuth2SuccessHandler;
import com.app.backend.domain.member.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtProvider jwtProvider;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
		http
			.headers(headers -> headers
				.frameOptions(options -> options
					.sameOrigin()
					.contentSecurityPolicy(csp -> csp
						.policyDirectives("""
								default-src 'self';
								script-src 'self' 'nonce-{nonce}';
								style-src 'self' 'nonce-{nonce}';
								img-src 'self' data: https:;
								object-src 'none';
								base-uri 'self';
								connect-src 'self' https://kauth.kakao.com https://kapi.kakao.com;
								frame-ancestors 'self';
								form-action 'self';
								require-trusted-types-for 'script'
							""")))
				.contentSecurityPolicy(csp ->
					csp.policyDirectives(
						"default-src 'self'; frame-ancestors 'self'; form-action 'self'; block-all-mixed-content"))
				.referrerPolicy(referrer ->
					referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN))
				.permissionsPolicyHeader(permissions ->
					permissions.policy("camera=(), microphone=(), geolocation=()")))
			.authorizeHttpRequests(request -> request
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/swagger-ui/**").permitAll()      // 없으면 스웨거 안열림 1
				.requestMatchers("/v3/api-docs/**").permitAll()      // 없으면 스웨거 안열림 2
				.requestMatchers("/api/**").permitAll()      // 없으면 스웨거 안열림 3
				.requestMatchers("/api/v1/members/kakao/**").permitAll()
				.requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/*").permitAll()
				.anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
					.baseUri("/oauth2/authorization/kakao"))  // 카카오 로그인 시작점
				.redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
					.baseUri("/login/oauth2/code/*"))  // 카카오 인증 후 백엔드 콜백
				.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(oAuth2SuccessHandler))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf
				.ignoringRequestMatchers("/api/**", "/h2-console/**")
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
		;

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setExposedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
