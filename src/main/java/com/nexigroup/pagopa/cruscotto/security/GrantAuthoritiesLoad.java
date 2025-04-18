package com.nexigroup.pagopa.cruscotto.security;

import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class GrantAuthoritiesLoad {

    private final CacheManager cacheManager;

    private final AuthGroupRepository authGroupRepository;

    private final AuthPermissionRepository authPermissionRepository;

    public GrantAuthoritiesLoad(
        CacheManager cacheManager,
        AuthGroupRepository authGroupRepository,
        AuthPermissionRepository authPermissionRepository
    ) {
        this.cacheManager = cacheManager;
        this.authGroupRepository = authGroupRepository;
        this.authPermissionRepository = authPermissionRepository;
    }

    public Collection<GrantedAuthority> load(Map<String, Object> claims, String subject, String iat, String typeLogin) {
        Collection<GrantedAuthority> grantedAuthorities = SecurityUtils.extractAuthorityFromClaims(claims);
        Cache cache = cacheManager.getCache(AuthoritiesConstants.PRINCIPAL);

        String key = subject.concat("_").concat(iat).concat("_").concat(typeLogin);

        Set<GrantedAuthority> grantedAuthoritiesConverted = new HashSet<>();

        if (cache != null && cache.get(key) != null) {
            grantedAuthoritiesConverted.addAll(cache.get(key, Collection.class));
        }

        if (grantedAuthoritiesConverted.isEmpty()) {
            //carico su Hazelcast le Authorities dell'utente

            GrantedAuthority groupAuthority = grantedAuthorities
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Group is not defined"));

            Optional<AuthGroup> group = authGroupRepository.findOneByNome(groupAuthority.getAuthority());

            AuthGroup authGroup = group.orElseThrow(() -> new IllegalArgumentException("Group not found"));

            List<AuthPermission> functions = authPermissionRepository.findAllPermissionsByGroupId(authGroup.getId());

            grantedAuthoritiesConverted = functions
                .stream()
                .map(function -> new SimpleGrantedAuthority(function.getModulo() + "." + function.getNome()))
                .collect(Collectors.toSet());

            cache.put(key, grantedAuthoritiesConverted);
        }

        return grantedAuthoritiesConverted;
    }
}
