package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.ModelResourceEntity;
import com.rengu.project.aluminum.entity.ModelResourceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 10:45
 */
@Repository
public interface ModelResourceHistoryRepository extends JpaRepository<ModelResourceHistory, String> {
    List<ModelResourceHistory> findByModelResourceEntity(ModelResourceEntity modelResourceEntity);
}
