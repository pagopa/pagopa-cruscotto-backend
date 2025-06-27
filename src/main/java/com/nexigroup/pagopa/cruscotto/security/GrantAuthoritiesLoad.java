package com.nexigroup.pagopa.cruscotto.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.cache.Cache;
import javax.cache.CacheManager;

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
        Cache<Object, Object> cache = cacheManager.getCache(AuthoritiesConstants.PRINCIPAL);

        String key = subject.concat("_").concat(iat).concat("_").concat(typeLogin);

        Set<GrantedAuthority> grantedAuthoritiesConverted = new HashSet<>();

        if (cache != null && cache.get(key) != null) {
            grantedAuthoritiesConverted.addAll((Collection)cache.get(key));
        }

        if (grantedAuthoritiesConverted.isEmpty()) {
            //carico in cache le Authorities dell'utente

            GrantedAuthority groupAuthority = grantedAuthorities
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Group is not defined"));
            
            if(groupAuthority.getAuthority() != null && groupAuthority.getAuthority().compareTo(Constants.ROLE_PASSWORD_EXPIRED) == 0) {
            	grantedAuthoritiesConverted.add(new SimpleGrantedAuthority(AuthoritiesConstants.PASSWORD_MODIFICATION));
			} else {

	            Optional<AuthGroup> group = authGroupRepository.findOneByNome(groupAuthority.getAuthority());
	
	            AuthGroup authGroup = group.orElseThrow(() -> new IllegalArgumentException("Group not found"));
	
	            List<AuthPermission> functions = authPermissionRepository.findAllPermissionsByGroupId(authGroup.getId());
	
	            grantedAuthoritiesConverted = functions
	                .stream()
	                .map(function -> new SimpleGrantedAuthority(function.getModulo() + "." + function.getNome()))
	                .collect(Collectors.toSet());
			}
            
            cache.put(key, grantedAuthoritiesConverted);
        }

        return grantedAuthoritiesConverted;
    }
}
