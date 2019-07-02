package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * author : yaojiahao
 * Date: 2019/7/1 16:11
 **/
@Repository
public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord, String> {
    Optional<ApplicationRecord> findByModelResource(ModelResourceEntity modelResourceEntity);

    Optional<ApplicationRecord> findByAlgorithmServer(AlgorithmAndServerEntity algorithmAndServerEntity);

    Optional<ApplicationRecord> findByToolsSoftware(ToolsAndSoftwareEntity toolsAndSoftwareEntity);

    Optional<ApplicationRecord> findByStandard(StandardEntity standardEntity);
}
