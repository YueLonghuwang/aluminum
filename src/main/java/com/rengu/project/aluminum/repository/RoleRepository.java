package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {

    boolean existsByName(String name);

    Optional<RoleEntity> findByName(String name);
}