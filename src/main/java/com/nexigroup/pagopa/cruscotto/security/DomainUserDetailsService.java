package com.nexigroup.pagopa.cruscotto.security;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthUserRepository;
import com.nexigroup.pagopa.cruscotto.service.util.PasswordExpiredUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final AuthUserRepository authUserRepository;

    private final AuthPermissionRepository authPermissionRepository;

    public DomainUserDetailsService(AuthUserRepository authUserRepository, AuthPermissionRepository authPermissionRepository) {
        this.authUserRepository = authUserRepository;
        this.authPermissionRepository = authPermissionRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);

        Optional<AuthUser> user = authUserRepository.findOneByLoginAndNotDeleted(login, AuthenticationType.FORM_LOGIN);

        AuthUser authUser = user.orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found"));

        if (!authUser.getActivated()) {
            throw new UserNotActivatedException("User " + login + " was not activated");
        }

        boolean accountNonLocked = !authUser.isBlocked();
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialNonExpired = true;

        credentialNonExpired = PasswordExpiredUtils.isPasswordNonExpired(
            authUser.getLastPasswordChangeDate(),
            authUser.getPasswordExpiredDay()
        );

        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

        if (credentialNonExpired) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authUser.getGroup().getNome()));
            //            grantedAuthorities.addAll(getAuthorities(user.get()));
        } else {
            grantedAuthorities.add(new SimpleGrantedAuthority(Constants.ROLE_PASSWORD_EXPIRED));
            //            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(AuthoritiesConstants.GTW_MODIFICA_PASSWORD);
            //            SimpleGrantedAuthority grantedAuthorityInfoAccount = new SimpleGrantedAuthority(AuthoritiesConstants.GTW_INFO_ACCOUNT);
            //
            //            grantedAuthorities.add(grantedAuthority);
            //            grantedAuthorities.add(grantedAuthorityInfoAccount);
        }

        return new org.springframework.security.core.userdetails.User(
            authUser.getLogin(),
            authUser.getPassword(),
            enabled,
            accountNonExpired,
            true,
            accountNonLocked,
            grantedAuthorities
        );
    }

    @Transactional(readOnly = true)
    public Set<GrantedAuthority> getAuthorities(AuthUser user) {
        List<AuthPermission> permissions = authPermissionRepository.findAllPermissionsByGroupId(user.getGroup().getId());

        return permissions
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getModulo() + "." + permission.getNome()))
            .collect(Collectors.toSet());
    }
}
