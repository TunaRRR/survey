package org.survey.user.security;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.survey.user.model.User;
import org.survey.user.repository.UserRepository;

/**
 * Spring security AuthenticationProvider which authenticates using
 * UserRepository.
 * 
 * @see http://tedyoung.me/2011/06/21/spring-security-custom-authenticators/
 * @see http 
 *      ://samerabdelkafi.wordpress.com/2011/01/16/secure-your-web-application
 *      -with-spring-security/
 */
@Slf4j
public class UserRepositoryAuthenticationProvider implements
        AuthenticationProvider {
    @Autowired
    UserRepository userRepository;

    /**
     * Authenticate using UserRepository.
     */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        log.debug("authenticate");
        User user = userRepository.findByUsername(authentication.getName());
        return authenticateUser(user, authentication);
    }

    private Authentication authenticateUser(User user,
            Authentication authentication) {
        if (user != null
                && authentication.getCredentials().equals(user.getPassword())) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new GrantedAuthorityImpl(user.getRole().name()));
            return new UsernamePasswordAuthenticationToken(
                    authentication.getName(), authentication.getCredentials(),
                    authorities);
        } else {
            return null;
//            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}