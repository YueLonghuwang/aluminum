package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ApplicationRecord;
import com.rengu.project.aluminum.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * author : yaojiahao
 * Date: 2019/7/1 16:11
 **/
@Repository
public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord, String> {

    Optional<ApplicationRecord> findByProcessId(String processId);

    boolean existsByProcessId(String processId);
    List<ApplicationRecord> findByUsers(UserEntity userEntity);
    /* Optional<ApplicationRecord> findByModelResource(ModelResourceEntity modelResourceEntity);

     Optional<ApplicationRecord> findByAlgorithmServer(AlgorithmAndServerEntity algorithmAndServerEntity);
     Optional<ApplicationRecord> findByToolsSoftware(ToolsAndSoftwareEntity toolsAndSoftwareEntity);

     Optional<ApplicationRecord> findByStandard(StandardEntity standardEntity);

     Page<ApplicationRecord> findByApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(Pageable pageable, int applicationStatus, int currentStatus, int securityClassification);

     Page<ApplicationRecord> findByResourceTypeAndCurrentStatusAndSecurityClassificationLessThanEqual(Pageable pageable, int resourceType, int currentStatus, int securityClassification);

     Page<ApplicationRecord> findByResourceTypeAndApplicationStatusAndSecurityClassificationLessThanEqual(Pageable pageable, int resourceType, int applicationStatus, int securityClassification);

     Page<ApplicationRecord> findByResourceTypeAndApplicationStatusAndCurrentStatus(Pageable pageable, int resourceType, int applicationStatus, int currentStatus);

     Page<ApplicationRecord> findByResourceType(Pageable pageable, int resourceType);
 */    Page<ApplicationRecord> findByResourceTypeAndApplicationStatusAndCurrentStatusAndSecurityClassificationLessThanEqual(Pageable pageable, int resourceType, int applicationStatus, int currentStatus, int securityClassification);
}
