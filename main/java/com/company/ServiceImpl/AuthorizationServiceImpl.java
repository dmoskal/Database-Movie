package com.company.ServiceImpl;

import com.company.Entity.User;
import com.company.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private CookieService cookieService;
    private SecurityUserService securityUserService;
    private UserService userService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @Autowired
    public AuthorizationServiceImpl(CookieService cookieService,
                                    SecurityUserService securityUserService,
                                    UserService userService) {
        this.cookieService = cookieService;
        this.securityUserService = securityUserService;
        this.userService = userService;
    }

    @Override
    public void setRequestSessionSecurity(final HttpServletRequest request,
                                          final HttpSession session,
                                          final SecurityContext securityContext) {
        this.request = request;
        this.session = session;
        securityUserService.setContext(securityContext);
    }

    @Override
    public void setRequestResponseSessionSecurity(final HttpServletRequest request,
                                                  final HttpServletResponse response,
                                                  final HttpSession session,
                                                  final SecurityContext securityContext) {
        this.request = request;
        this.response = response;
        this.session = session;
        securityUserService.setContext(securityContext);
    }

    @Override
    public boolean isLogged() {
        if (cookieService.isCookie(request, "username")
                && session.getAttribute("user") != null) {
            return true;
        } else if (cookieService.isCookie(request, "username")) {
            restoreUser();

            return true;
        } else if(!cookieService.isCookie(request, "username")
                      && session.getAttribute("user") != null) {
            logout();

            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean isLoginProcess() {
        return !cookieService.isCookie(request, "username")
                && !securityUserService.isAnonymousUser();
    }

    @Override
    public void login() {
        String username = securityUserService.getUsername();

        User user = userService.findUserByUsername(username);
        session.setAttribute("user", user);

        cookieService.addCookie(response, "username", user.getUsername());
    }

    @Override
    public User getUser() {
        return userService.findUserByUsername(securityUserService.getUsername());
    }

    @Override
    public void checkOrRestoreUser() {
        if (cookieService.isCookie(request, "username")
                && session.getAttribute("user") == null) {
            restoreUser();
        }
    }

    @Override
    public void restoreUserAfterActivationAccount(final Long id) {
        User user = userService.getUser(id);

        securityUserService.createUsernamePasswordAuthenticationToken(user.getUsername(),
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

    private void restoreUser() {
        String usernameFromCookie = cookieService.getValueCookie(request, "username");

        User user = userService.findUserByUsername(usernameFromCookie);

        session.setAttribute("user", user);

        securityUserService.createUsernamePasswordAuthenticationToken(user.getUsername(),
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

    private void logout() {
        SecurityContextHolder.clearContext();
        session.setAttribute("user", null);
    }
}
