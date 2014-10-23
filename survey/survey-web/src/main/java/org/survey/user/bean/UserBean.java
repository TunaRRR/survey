package org.survey.user.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class UserBean {
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return authentication.getName();
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication.getAuthorities().iterator().hasNext()) {
            return authentication.getAuthorities().iterator().next().toString();
        }
        return null;
    }
}