package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.MessageEntity;
import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.repository.MessageRepository;
import com.rengu.project.aluminum.service.MessageService;
import com.rengu.project.aluminum.specification.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.rengu.project.aluminum.specification.SpecificationBuilder.selectFrom;

/**
 * author : yaojiahao
 * Date: 2019/6/17 16:22
 **/
@RestController
@RequestMapping(value = "/messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    @Autowired
    public MessageController(MessageService messageService, MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    // 查看全部通知
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_AUDIT')")
    public ResultEntity getAll(@PathVariable(value = "userId") String userId) {
        return new ResultEntity<>(messageService.getMessagesByUser(userId));
    }

    // 显示全部通知
    @GetMapping
    public ResultEntity<Page<MessageEntity>> getAllMessage(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResultEntity<>(messageService.getAllMessage(pageable));
    }
    // 标记某条通知已读
    @PatchMapping
    public ResultEntity readAlready(String messageId) {
        return new ResultEntity<>(messageService.readAlready(messageId));
    }

    // 标记某用户的所有通知全部已读
    @PatchMapping("/readAll")
    public ResultEntity readAll(String userId) {
        return new ResultEntity<>(messageService.readAll(userId));
    }

    // 清空所有已读通知
    @PostMapping(value = "/clearAllRead")
    public ResultEntity clearAllRead(String userId) {
        return new ResultEntity<>(messageService.clearAllRead(userId));
    }

    // 根据状态查询通知
    @PostMapping(value = "/findByIfRead")
    public ResultEntity findByIfRead(String userId, boolean ifRead) {
        return new ResultEntity<>(messageService.findByIfRead(userId, ifRead));
    }

    // 根据关键字查询
    @PostMapping("/KeyWord")
    public ResultEntity findByKeyWord(@RequestBody Filter filter) {
        return new ResultEntity(selectFrom(messageRepository).where(filter).findAll());
    }
}
