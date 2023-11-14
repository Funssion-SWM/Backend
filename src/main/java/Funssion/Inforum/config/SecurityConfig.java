package Funssion.Inforum.config;

import Funssion.Inforum.access_handler.*;
import Funssion.Inforum.domain.member.service.OAuthService;
import Funssion.Inforum.jwt.JwtSecurityConfig;
import Funssion.Inforum.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static Funssion.Inforum.common.constant.Role.EMPLOYER;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final NonSocialLoginFailureHandler nonSocialLoginFailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    //OAuth2LoginConfig에서 @Configuration으로 등록된 bean 주입
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuthService oAuthService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler oAuthAuthenticationFailureHandler;
    @Value("${jwt.domain}") private String domain;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf
                        (AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // enable h2-console
                .headers((headers) ->
                        headers.contentTypeOptions(contentTypeOptionsConfig ->
                                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST,"/users").permitAll()
                                .requestMatchers(HttpMethod.POST,"/users/employer").permitAll()
                                .requestMatchers(HttpMethod.GET,"/users/employer").hasRole(EMPLOYER.toString())
                                .requestMatchers(HttpMethod.POST, "/users/profile").hasRole(EMPLOYER.toString())
                                //users 포함한 end point 보안 적용
                                .requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                                .requestMatchers("/users/authenticate-email",
                                        "/users/authenticate-email/find",
                                        "/users/password",
                                        "/users/authenticate-code",
                                        "/users/check-duplication").permitAll()
                                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                                .requestMatchers(HttpMethod.POST,"/interview/questions/**").hasRole("EMPLOYER")
                                .requestMatchers(HttpMethod.GET,"/interview/answers/**").hasAnyRole("EMPLOYER","USER")
                                .requestMatchers(HttpMethod.GET,"/interview/start/**").hasRole("USER")
                                .requestMatchers(HttpMethod.GET,"/interview/continue/**").hasRole("USER")
                                .requestMatchers(HttpMethod.POST,"/interview/answers/**").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/score/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/score/rank/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/users/login").authenticated() //spring security filter에서 redirect
                                .requestMatchers(HttpMethod.GET,"/tags/**").permitAll()
                                .requestMatchers("/oauth2/authorization/**").permitAll()
                                .requestMatchers("/login/oauth2/code/**").permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/memos/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/questions/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/answers/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/series/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/mypage/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/comments/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/search/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/follows").permitAll()
                                .requestMatchers(HttpMethod.GET, "/followers").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v2/api-docs",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        /* swagger v3 */
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .anyRequest().authenticated() // 그 외 인증 없이 접근X
                )
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .userInfoEndpoint(it -> it.userService(oAuthService))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(oAuthAuthenticationFailureHandler))
                .formLogin((formLogin) -> formLogin
                        .loginProcessingUrl("/users/login")
                        .failureHandler(nonSocialLoginFailureHandler)
                        .successHandler(authenticationSuccessHandler).permitAll())
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .apply(new JwtSecurityConfig(tokenProvider,domain)); // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig class 적용

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}