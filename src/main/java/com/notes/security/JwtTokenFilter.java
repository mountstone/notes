package com.notes.security;

import com.notes.repository.UserRepository;
import com.notes.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

//public class JwtTokenFilter extends GenericFilterBean {
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private String header="Authorization";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        getTokenString(httpServletRequest.getHeader(this.header)).ifPresent(

                token->{
                    jwtService.getSubjectFromToken(token).ifPresent(
                            id->{
                                if(SecurityContextHolder.getContext().getAuthentication()==null ){
                                    userRepository.findById(id).ifPresent(
                                            user->{

                                                UsernamePasswordAuthenticationToken authenticationToken=
                                                        new UsernamePasswordAuthenticationToken(
                                                                user,
                                                                null,
                                                                Collections.emptyList());
                                                authenticationToken.setDetails(
                                                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                                                );
                                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                            }
                                    );
                                }
                            }
                    );
                }
        );

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


    private Optional<String> getTokenString(String header) {
        if (header == null) {
            return Optional.empty();
        } else {
            String[] splited = header.split(" ");
            if (splited.length < 2) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(splited[1]);
            }
        }
    }
}
