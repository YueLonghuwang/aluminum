package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String> {

    boolean existsByName(String name);
}
