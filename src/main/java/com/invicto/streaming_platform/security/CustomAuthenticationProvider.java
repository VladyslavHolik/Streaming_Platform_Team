package com.invicto.streaming_platform.security;

import com.invicto.streaming_platform.persistence.model.User;
import com.invicto.streaming_platform.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String input = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println(password);
        Optional<User> optionalUser = userService.findByLoginOrEmail(input);
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Unknown user "+input);
        }
        if (!password.equals(optionalUser.get().getPassword())) {
            throw new BadCredentialsException("Bad password");
        }
        System.out.println(input+" "+password);
        UserDetails principal = org.springframework.security.core.userdetails.User.builder()
                .username(optionalUser.get().getLogin())
                .password(optionalUser.get().getPassword())
                .roles("USER")
                .build();
        return new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
