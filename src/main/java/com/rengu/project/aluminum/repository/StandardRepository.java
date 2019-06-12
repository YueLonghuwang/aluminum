package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.StandardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Repository
public interface StandardRepository extends JpaRepository<StandardEntity, String> {

    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Page<StandardEntity> findBySecurityClassificationLessThanEqualAndStatus(Pageable pageable, int securityClassification, int status);
}
