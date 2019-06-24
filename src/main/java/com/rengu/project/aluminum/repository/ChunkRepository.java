package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * author : yaojiahao
 * Date: 2019/6/21 19:34
 **/
@Repository
public interface ChunkRepository extends JpaRepository<ChunkEntity, String> {
}
