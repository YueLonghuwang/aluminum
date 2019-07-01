package com.rengu.project.aluminum.repository;

import com.rengu.project.aluminum.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author : yaojiahao
 * Date: 2019/6/17 10:19
 **/
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, String>, JpaSpecificationExecutor<MessageEntity> {
    List<MessageEntity> findByArrangedPersonName(String arrangedPersonName);

    List<MessageEntity> findByArrangedPersonNameAndIfRead(String arrangedPersonName, boolean ifRead);

    Page<MessageEntity> findAll(Pageable pageable);
    Long countByArrangedPersonNameAndIfRead(String arrangedPersonName, boolean ifRead);
}
