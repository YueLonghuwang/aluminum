package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.AlgorithmAndServerEntity;
import com.rengu.project.aluminum.entity.ModelResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:31
 **/
@Repository
public interface AlgorithmAndServerRepository extends JpaRepository<AlgorithmAndServerEntity, String> {
    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Page<ModelResourceEntity> findBySecurityClassificationLessThanEqualAndStatus(Pageable pageable, int securityClassification, int status);
}
