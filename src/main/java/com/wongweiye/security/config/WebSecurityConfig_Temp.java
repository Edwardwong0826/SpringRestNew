//package com.wongweiye.security.config;
//
//import com.nimbusds.jose.JOSEException;
//import com.nimbusds.jose.jwk.JWKSet;
//import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.SecurityContext;
//import com.wongweiye.security.Jwks;
//import com.wongweiye.security.config.service.AuthTokenFilter;
//import com.wongweiye.security.config.service.AuthEntryPointJwt;
//import com.wongweiye.security.config.service.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//// For Spring Boot 2.7, WebSecurityConfigurerAdapter is deprecated. Below code is for Spring Boot 2.7 >= above.
//// For Spring Boot 3 project, please refer to https://www.bezkoder.com/websecurityconfigureradapter-deprecated-spring-boot/
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class WebSecurityConfig_Temp {
//
//    @Autowired
//    UserDetailsServiceImpl userDetailsService;
//
//    @Autowired
//    private AuthEntryPointJwt unauthorizedHandler;
//
//    private RSAKey rsaKey;
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }
//
//
//    // seems like not recommend this approach
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    // Encode means encrypt the password
//    @Bean
//    public PasswordEncoder passwordEncoder() {
////        PasswordEncoder passwordEncoder =
////                PasswordEncoderFactories.createDelegatingPasswordEncoder();
////        return passwordEncoder;
//        return new BCryptPasswordEncoder();
//    }
//
//    // Below is no password encoder
////    @Bean
////    public static NoOpPasswordEncoder passwordEncoder() {
////        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
////    }
//
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////
////        http.cors().and().csrf().disable()
////                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
////                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
////                .antMatchers("/api/test/**").permitAll()
////                .antMatchers("/h2/**").permitAll()
////                .antMatchers("/actuator/health/**").permitAll()
////                .anyRequest().authenticated();
////
////        http.authenticationProvider(authenticationProvider());
////
////        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.cors().and().csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
//                .antMatchers("/api/test/**").permitAll()
//                .antMatchers("/h2/**").permitAll()
//                .antMatchers("/actuator/health/**").permitAll()
//                .anyRequest().authenticated();
//
//        http.authenticationProvider(authenticationProvider());
//
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public JWKSource<SecurityContext> jwkSource() {
//        rsaKey = Jwks.generateRsa();
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
//    }
//
//    @Bean
//    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
//        return new NimbusJwtEncoder(jwks);
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder() throws JOSEException {
//        // use this as our JwtDecoder by using the public we set in configuration class to build and return
//        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
//    }
//
//
//}
