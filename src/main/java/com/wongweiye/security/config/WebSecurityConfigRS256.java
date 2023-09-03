package com.wongweiye.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wongweiye.security.RSAKeyProperties;
import com.wongweiye.security.config.service.AuthTokenFilter;
import com.wongweiye.security.config.service.AuthEntryPointJwt;
import com.wongweiye.security.config.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// For Spring Boot 2.7, WebSecurityConfigurerAdapter is deprecated. Below code is for Spring Boot 2.7 >= above.
// For Spring Boot 3 project, please refer to https://www.bezkoder.com/websecurityconfigureradapter-deprecated-spring-boot/
// https://www.danvega.dev/blog/2022/09/06/spring-security-jwt/ mainly reference these two and combine

// Key concepts - JWT, resource server, authorization server etc
// Normally there will be separate authorization server handles JWT authorization in large mirco-services application,
// but in this example only need resource server, so no need authorization server for now, so question is when we move away from self-signed JWT?
// usually cooperate will use identity provider service like OKta and Auth0, they can be counted as authorization server
// https://oauth.net/2/grant-types/ - there is 4 major grant types for Oauth2, we are using the Password Grant type now because easy to learn,
// the major one is Authorization Code type, also we didn't implement refresh token now yet, cooperate will implement refresh token  when using identity provider service
// in spring security, authentication and authorization module is separate, therefore we can integrate third party library in each module by our choice

// if we use application KeyGeneratorUtils and Jwks class will do the similar stuff for us when application boots up then no need to run below command else need to run
// openssl genrsa -out keypair.pem 2048 - generate the private key
// openssl rsa -in keypair.pem -pubout -out public.pem - writing out public key
// openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem - encoded private key to pm encoded pkcs8 fromat as required by the application

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfigRS256 {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RSAKeyProperties rsaKeyProperties;

    public WebSecurityConfigRS256(RSAKeyProperties rsaKeyProperties) {
        this.rsaKeyProperties = rsaKeyProperties;
    }

// Spring official not recommend this approach
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//
//        return authConfig.getAuthenticationManager();
//    }

    // authentication is done by AuthenticationManager
    // its concrete class was primary ProviderManager, ProviderManager manage many AuthenticationProvider instance, Spring security allows multiple AuthenticationProvider exist
    // to achieve many types of authenticate method,
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    // Encode means encrypt the password to the format we wanted, here we choose BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Below is no password encoder means plain text for process password
    //    @Bean
    //    public static NoOpPasswordEncoder passwordEncoder() {
    //        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    //    }

    // spring security use servlet filter to achieve authentication/authorization
    // spring uses delegatingFilterProxy to bridge spring to java web servlet in filter chain
    // delegatingFilterProxy use FilterChainProxy to manage 0-n securityFilterChain, so we can configure our own securityFilterChain
    // in spring security 5.8.x, security filter chain will load default filters which is 12
    // these filters like BearerTokenAuthenticationFilter, SecurityContextPersistenceFilter etc
    // until this point, if we want to access any of the endpoint on browser, it will prompt login dialog and required username and password to authenticate
    // when set OAuth2ResourceServerConfigurer configure option to jwt(Customizer), we need to either supply a Jwk Set Uri, Jwk decoder instance or JwtDecoder bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                //.oauth2ResourceServer(configure -> configure.jwt(Customizer.withDefaults()))
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/h2/**").permitAll()
                .antMatchers("/actuator/health/**").permitAll()
                .anyRequest().authenticated();

        http.csrf().disable();
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        // In JwtAuthenticationProvider authenticate method, inside it will use JwtAuthenticationConverter to extract authorities using extractAuthorities method
        // jwtGrantedAuthoritiesConverter.convert(jwt) to convert JWT to AbstractAuthenticationToken we used to return for authenticated,
        // by default it will look for JWT claims name scope or scp, and mapped that claims value like this format prefix SCOPE_ + claims value which will causing error for 403
        // so we need to configure JwtGrantedAuthoritiesConverter setAuthorityPrefix to "", then will map claims value to SimpleGrantedAuthority as ROLE_XXX instead of SCOPE_ROLE_XXX
        // GrantedAuthority will decide which protected resource we can access or not

        // either we do in this way or we can create our own CustomJwtConverter to do mapping by ourself like
        // JwtAuthenticationProvider.setJwtAuthenticationConverter(new xxxCustomJwtConverter())
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authConverter;
    }

    // we are using asymmetric key to encrypted and decrypted, here we are using RS256 algorithm to sign the JWT, the current JWA recommend algorithm for generate JWT
    // https://codecurated.com/blog/introduction-to-jwt-jws-jwe-jwa-jwk/
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).privateKey(rsaKeyProperties.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        // use this as our JwtDecoder by using the public we set in configuration class to build and return
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
    }

}
