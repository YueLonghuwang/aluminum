package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.DepartmentEntity;
import com.rengu.project.aluminum.entity.RoleEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByRoleEntities(Set<RoleEntity> roleEntity, Pageable pageable);
    Page<UserEntity> findAllByUsernameNot(String username, Pageable pageable);

    Page<UserEntity> findAllByUsernameNotAndUsernameNot(String username, String umbrages, Pageable pageable);
    boolean existsByUsername(String username);

    Page<UserEntity> findByDepartment(Pageable pageable, DepartmentEntity departmentEntity);

    Set<UserEntity> findByDepartment(DepartmentEntity departmentEntity);
}
