package com.wongweiye.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wongweiye.security.Jwks;
//import com.wongweiye.security.RSAKeyProperties;
import com.wongweiye.security.config.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import org.springframework.web.client.RestTemplate;

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

    //@Autowired
    //private RSAKeyProperties rsaKeyProperties;

    // no need to put @Autowired on field injection or constructor injection else will throw
    // Consider defining a bean of type 'com.nimbusds.jose.jwk.RSAKey' in your configuration.
    private RSAKey rsaKey;

//    public WebSecurityConfigRS256(RSAKeyProperties rsaKeyProperties) {
//        this.rsaKeyProperties = rsaKeyProperties;
//    }


    // in spring security > 5.8, 默认AuthenticationManager 全局配置还是有效, but if we want to define custom Authentication Provider, we can create our class to implements AuthenticationProvider interface

    //    @Autowired
    //    private CustomXXXAuthenticationProvider customXXXAuthenticationProvider;

    //    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    //        AuthenticationManagerBuilder authenticationManagerBuilder =
    //                http.getSharedObject(AuthenticationManagerBuilder.class);
    //        authenticationManagerBuilder.authenticationProvider(customXXXAuthenticationProvider);
    //        authenticationManagerBuilder.authenticationProvider(authProvider());
    //
    //        return authenticationManagerBuilder.build();
    //    }

    // Spring official not recommend this approach
    //    @Bean
    //    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //
    //        return authConfig.getAuthenticationManager();
    //    }


    // in spring security, system support simultaneous multiple different way of authenticate, different authenticate way correspond different AuthenticationProvider
    // so a complete flow might supply by different AuthenticationProvider, ProviderManager stored a list of AuthenticationProvider, it will iterate every AuthenticationProvider
    // to execute identity authenticate, as long as ony of one AuthenticationProvider return authentication result object with authenticated equal true

    // normally ProviderManager can set one AuthenticationManager(normally is providerManager), because there is possible of many ProviderManager, they will share one parent
    // sometimes when application like resources /api/text we can set it to use first ProviderManager, second resources /api/test we set it use second ProviderManager, rest of resources uri then will default let parent to do authenticate
    // when child ProviderManager authenticate not success, it will still able to look back parent to do authenticate, parent is normally is created by spring security
    // parent AuthenticationManager is kind of like global resource as the backup of all AuthenticationManager(ProviderManager)
    // and by default parent AuthenticationManager(ProviderManager) will have DaoAuthenticationProvider

    // AuthenticationManager - is the authentication manager in spring security concept, it defined spring security filter execute authenticate operation
    // ProviderManager - AuthenticationManager implementation class, spring security when authenticate default use this
    // AuthenticationProvider - can be DaoAuthenticationProvider or JwtAuthenticationProvider, this provider we can inject according different type of authenticate method

    // when run Auth Controller endpoints without bearer token, it will invoke DaoAuthenticationProvider
    // run rest of the Controller endpoints with bearer token, it will invoke JwtAuthenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    // Encode means encrypt the password to the format we wanted, here we choose BCrypt
    // refer https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-dpe
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

        // in this application we are based on AOP - MethodSecurityInterceptor to manage access control
        // difference between method and filter way is that FilterSecurityInterceptor will do pre process before request,
        // MethodSecurityInterceptor will do pre process and post process within request which is AROUND in AOP
        // based on filter can refer to below example
        // .antMatchers("/h2/**").hasRole("USER").permitAll()
        // in newer version >= 5.8 .authorizeRequests().antMatchers change to use .authorizeHttpRequests().requestMatchers(xxx)

        // in spring security version >= 5.8, it replaces FilterSecurityInterceptor with AuthorizationFilter
        // Authorize HttpServletRequests with AuthorizationFilter - https://docs.spring.io/spring-security/reference/5.8/servlet/authorization/authorize-requests.html
        // Authorize HttpServletRequest with FilterSecurityInterceptor - https://docs.spring.io/spring-security/reference/5.8/servlet/authorization/authorize-http-requests.html
        http.cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(x -> x.jwt()) // lambda style same as above line
                //.oauth2ResourceServer(configure -> configure.jwt(Customizer.withDefaults()))
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/h2/**").permitAll()
                .antMatchers("/actuator/health/**").permitAll()

                .anyRequest().authenticated();
                //.anyRequest().authenticated().and().oauth2Login();

        http.csrf().disable();

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        // Before hit request/method endpoints In JwtAuthenticationProvider authenticate method, inside it will use JwtAuthenticationConverter.convert(jwt)
        // to extract authorities from JWT to construct JwtAuthenticationToken and return as AbstractAuthenticationToken we used to return for authenticated,
        // by default it will look for JWT claims name scope or scp, and mapped that claims value like this format prefix SCOPE_ + claims value which will causing error for 403
        // due to method haven't @PreAuthorize("hasAuthority('SCOPE_xxx')"), in RBDC(role based data access) design, we will insert ROLE_xxx into roles table
        // so we need to configure JwtGrantedAuthoritiesConverter setAuthorityPrefix to "", then will map claims value to SimpleGrantedAuthority as ROLE_XXX instead of SCOPE_ROLE_XXX
        // GrantedAuthority will decide which protected resource we can access or not

        // either we do in this way or we can create our own CustomJwtConverter to do mapping by ourselves like
        // JwtAuthenticationProvider.setJwtAuthenticationConverter(new xxxCustomJwtConverter())
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // commented out below when we have added the @PreAuthorize("hasAuthority('SCOPE_xxx')") on the endpoints
        //grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authConverter;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    // we are using asymmetric key to encrypted and decrypted, here we are using RS256 algorithm to sign the JWT, the current JWA recommend algorithm for generate JWT
    // https://codecurated.com/blog/introduction-to-jwt-jws-jwe-jwa-jwk/
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        //JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey()).privateKey(rsaKeyProperties.privateKey()).build();
        //JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        // use this as our JwtDecoder by using the public key we set in configuration class to build and return
        // also this JwtDecoder bean for this oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

}
