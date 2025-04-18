package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the AuthPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthPermissionRepository extends JpaRepository<AuthPermission, Long>, JpaSpecificationExecutor<AuthPermission> {
    @Query(
        "select authPermission from AuthPermission authPermission left join  authPermission.authFunctions authFunctions left join authFunctions.authGroups authGroups where authGroups.id =:idGruppo"
    )
    List<AuthPermission> findAllPermissionsByGroupId(@Param("idGruppo") Long idGroup);
}
