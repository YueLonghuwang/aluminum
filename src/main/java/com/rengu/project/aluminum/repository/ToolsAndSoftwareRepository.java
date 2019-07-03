package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.ToolsAndSoftwareEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * author : yaojiahao
 * Date: 2019/6/13 14:45
 **/

@Repository
public interface ToolsAndSoftwareRepository extends JpaRepository<ToolsAndSoftwareEntity, String>, JpaSpecificationExecutor<ToolsAndSoftwareEntity> {
    boolean existsByNameAndVersionAndStatusIn(String name, String version, int[] status);

    Page<ModelResourceEntity> findBySecurityClassificationLessThanEqualAndStatusIn(Pageable pageable, int securityClassification, int[] status);

    ToolsAndSoftwareEntity findByProcessId(String processId);
    boolean existsByProcessId(String processId);

}
