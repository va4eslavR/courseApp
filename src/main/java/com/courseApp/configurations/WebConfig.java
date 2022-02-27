package com.courseApp.configurations;

import com.courseApp.services.oauth2customization.CustomOAuth2UserService;
import com.courseApp.services.oauth2customization.CustomOidcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final OidcUserService oidcUserService;
    @Autowired
    public WebConfig(CustomOAuth2UserService oAuth2UserService, CustomOidcService oidcUserService) {
        this.oAuth2UserService = oAuth2UserService;
        this.oidcUserService = oidcUserService;
    }

    protected void configure(HttpSecurity http ) throws Exception
    {
        http
                .authorizeRequests()
                .antMatchers("/","/authPage","/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login(x->x.userInfoEndpoint()
                        .userService(oAuth2UserService)
                        .oidcUserService(oidcUserService)
                )
                ;
    }


}
