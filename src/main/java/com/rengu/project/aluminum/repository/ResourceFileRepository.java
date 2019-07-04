package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.FileEntity;
import com.rengu.project.aluminum.entity.ResourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * com.rengu.project.aluminum.repository
 *
 * @author hanchangming
 * @date 2019-06-11
 */

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFileEntity, String> {

    boolean existsByNameAndExtensionAndParentNodeAndResourceIdAndFolderEquals(String name, String extension, ResourceFileEntity parentNode, String resourceId, boolean isFolder);

    Optional<ResourceFileEntity> findByNameAndExtensionAndParentNodeAndResourceIdAndFolderEquals(String name, String extension, ResourceFileEntity parentNode, String resourceId, boolean isFolder);

    Set<ResourceFileEntity> findByParentNodeAndResourceId(ResourceFileEntity parentNode, String resourceId);

    boolean existsByFileEntity(FileEntity fileEntity);

    List<ResourceFileEntity> findByResourceId(String resourceId);

    boolean existsByResourceId(String resourceId);

}
