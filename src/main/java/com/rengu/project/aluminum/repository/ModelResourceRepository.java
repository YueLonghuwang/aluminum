package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:21
 **/
@Repository
public interface ModelResourceRepository extends JpaRepository<ModelResourceEntity, String> {
    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Page<ModelResourceEntity> findBySecurityClassificationLessThanEqualAndStatus(Pageable pageable, int securityClassification, int status);
}
