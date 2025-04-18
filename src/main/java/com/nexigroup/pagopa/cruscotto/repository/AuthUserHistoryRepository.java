package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AuthUserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserHistory entity.
 */
@Repository
public interface AuthUserHistoryRepository extends JpaRepository<AuthUserHistory, Long> {}
