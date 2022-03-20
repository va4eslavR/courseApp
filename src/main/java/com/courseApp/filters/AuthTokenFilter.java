package com.courseApp.filters;

import com.courseApp.services.AppUserDetailsService;
import com.courseApp.utility.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Autowired
    private AppUserDetailsService userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String bearer = parseJwt(request);
            logger.info("==========================\n bearer from request: {}\n==========================\n"
                    , bearer);
            if (bearer != null && jwtUtils.validateJwtToken(bearer)) {
                String username = jwtUtils.getUsernameFromToken(bearer);
                logger.info("username from bearer from filter" + username);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("cannot set user authentication:{0}", e);
        }
        filterChain.doFilter(request,response);
    }

    private String parseJwt(HttpServletRequest request) {
        logger.info( "parse request for restricted content :{}",request.toString());
        String headerAuth = request.getHeader("Authorization");
        logger.info("authorization:{}",headerAuth);
        return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")?headerAuth.substring(7): null;
    }
}
