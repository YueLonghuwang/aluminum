package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.AlgorithmAndServerEntity;
import com.rengu.project.aluminum.entity.ModelResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:31
 **/
@Repository
public interface AlgorithmAndServerRepository extends JpaRepository<AlgorithmAndServerEntity, String>, JpaSpecificationExecutor<AlgorithmAndServerEntity> {
    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Page<ModelResourceEntity> findBySecurityClassificationLessThanEqualAndStatusIn(Pageable pageable, int securityClassification, int[] status);

    AlgorithmAndServerEntity findByProcessId(String processId);
    boolean existsByProcessId(String processId);
}
