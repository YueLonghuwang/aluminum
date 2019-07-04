package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.StandardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 10:46
 */
@Repository
public interface StandardHistoryRepository extends JpaRepository<StandardHistory, String> {
}
