package com.elearning.configs.security;


import com.elearning.controller.JwtController;
import com.elearning.security.SecurityUserDetail;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtController jwtController;
    @Autowired
    private UserDetailsService userDetailsService;

    private HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(HandlerExceptionResolver resolver) {
        exceptionResolver = resolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.contains("/password/reset");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws SignatureException, ServletException, IOException {
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {

            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;
            final String userId;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtController.extractUsername(jwt);
            userId = jwtController.extractUserId(jwt);

            if (userEmail != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityUserDetail userDetails = (SecurityUserDetail) this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtController.isValidToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
            }else{
                throw new SignatureException("JWT_ERROR");
            }
        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }

}
