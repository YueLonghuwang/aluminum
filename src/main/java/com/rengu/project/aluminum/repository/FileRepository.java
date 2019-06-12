package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.FileEntity;
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
public interface FileRepository extends JpaRepository<FileEntity, String> {

    boolean existsByMD5(String md5);

    Optional<FileEntity> findByMD5(String md5);
}
