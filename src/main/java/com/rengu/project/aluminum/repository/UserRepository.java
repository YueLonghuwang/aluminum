package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<UserEntity> findByDepartment(Pageable pageable, DepartmentEntity departmentEntity);

    Set<UserEntity> findByDepartment(DepartmentEntity departmentEntity);
}
