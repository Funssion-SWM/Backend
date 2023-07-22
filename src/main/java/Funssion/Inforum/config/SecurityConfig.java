package Funssion.Inforum.config;

import Funssion.Inforum.jwt.JwtAccessDeniedHandler;
import Funssion.Inforum.jwt.JwtAuthenticationEntryPoint;
import Funssion.Inforum.jwt.JwtSecurityConfig;
import Funssion.Inforum.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exceptionHandling) -> //컨트롤러의 예외처리를 담당하는 exception handler와는 다름.
                        exceptionHandling
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .formLogin((formLogin)->formLogin.disable())
                // enable h2-console
                .headers((headers)->
                        headers.contentTypeOptions(contentTypeOptionsConfig ->
                                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                // disable session
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests((authorizeRequests)->
                        authorizeRequests
                                //users 포함한 end point 보안 적용 X
                                .requestMatchers("/users/**").permitAll() // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다.
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers(HttpMethod.GET ,"/memos/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/mypage/**").permitAll()
                                .requestMatchers("/swagger-ui/**","/v2/api-docs",
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
                .exceptionHandling((exceptionHandling)->exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .apply(new JwtSecurityConfig(tokenProvider)); // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig class 적용

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}