package com.pixo.security;
import com.pixo.repositories.*;
import com.pixo.security.jwt.JwtAuthEntryPoint;
import com.pixo.security.jwt.JwtAuthTokenFilter;
import com.pixo.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

//    @Autowired
//    private UserRepository userRepository;
//
//    @Bean
//    public PasswordEncoder encoder() {
//        return new BCryptPasswordEncoder(11);
//    }
//
//
////    @Override
////    public UserDetailsService userDetailsServiceBean() throws Exception {
////        return new SSUserDetailsService(userRepository);
////    }
//
//    @Override
//	protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//		http.authorizeRequests().antMatchers("/assets/**", "/bootstrap3/**", "/", "/signup/**").permitAll()
//		.anyRequest().authenticated();
//		http
//		.formLogin().failureUrl("/login?error")
//		.defaultSuccessUrl("/")
//        .loginPage("/login")
//		.permitAll()
//		.and()
//		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
//		.permitAll();
//	}
//
//
//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//                auth
//                .userDetailsService(userDetailsServiceBean())
//                .passwordEncoder(encoder());
//    }
	
	@Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthTokenFilter authenticationJwtTokenFilter() {
        return new JwtAuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/bootstrap3/**").permitAll()
                .antMatchers("/signup/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/downloadFile/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}


