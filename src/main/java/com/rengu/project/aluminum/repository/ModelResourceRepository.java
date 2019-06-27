package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:21
 **/
@Repository
public interface ModelResourceRepository extends JpaRepository<ModelResourceEntity, String>, JpaSpecificationExecutor<ModelResourceEntity> {
    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Optional<ModelResourceEntity> findByNameAndVersionAndStatusIn(String name, String version, int[] status);
    Page<ModelResourceEntity> findBySecurityClassificationLessThanEqualAndStatus(Pageable pageable, int securityClassification, int status);
}
