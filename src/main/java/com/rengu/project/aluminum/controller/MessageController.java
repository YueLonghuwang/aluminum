package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * author : yaojiahao
 * Date: 2019/6/17 16:22
 **/
@RestController
@RequestMapping(value = "/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // 查看全部通知
    @GetMapping("/{userId}")
    public ResultEntity getAll(@PathVariable(value = "userId") String userId) {
        return new ResultEntity<>(messageService.getMessagesByUser(userId));
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
}
