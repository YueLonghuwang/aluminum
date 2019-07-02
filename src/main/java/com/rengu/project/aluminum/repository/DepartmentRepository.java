package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String>, JpaSpecificationExecutor<DepartmentRepository> {

    boolean existsByName(String name);

    Optional<DepartmentEntity> findByName(String departmentName);
}
