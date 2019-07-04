package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.AlgorithmAndServerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: yaojiahao
 * @Date: 2019/7/4 10:45
 */
@Repository
public interface AlgorithmAndServerHistoryRepository extends JpaRepository<AlgorithmAndServerHistory, String> {
}
