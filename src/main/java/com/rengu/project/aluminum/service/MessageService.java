package com.rengu.project.aluminum.service;

import com.rengu.project.aluminum.entity.MessageEntity;
import com.rengu.project.aluminum.entity.UserEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.ResourceException;
import com.rengu.project.aluminum.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * author : yaojiahao
 * Date: 2019/6/17 10:21
 **/

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    // 保存通知
    public MessageEntity saveMessageEntity(MessageEntity message) {
        return messageRepository.save(message);
    }

    // 标记已读
    public MessageEntity readAlready(String messageId) {
        MessageEntity message = getMessageEntityById(messageId);
        message.setIfRead(true);
        return messageRepository.save(message);
    }

    // 全部已读
    public List<MessageEntity> readAll(String userId) {
        List<MessageEntity> messageList = getMessagesByUser(userId);
        for (MessageEntity message : messageList) {
            message.setIfRead(true);
        }
        return messageRepository.saveAll(messageList);
    }

    // 清空所有已读通知
    public List<MessageEntity> clearAllRead(String userId) {
        UserEntity users = userService.getUserById(userId);
        List<MessageEntity> messageList = messageRepository.findByArrangedPersonNameAndIfRead(users.getUsername(), true);
        messageRepository.deleteAll(messageList);
        return messageList;
    }

    // 根据状态查询通知: 查看所有已读未读消息
    public List<MessageEntity> findByIfRead(String userId, boolean ifRead) {
        UserEntity users = userService.getUserById(userId);
        return messageRepository.findByArrangedPersonNameAndIfRead(users.getUsername(), ifRead);
    }

    // 查看全部通知，根据被操作用户返回消息
    public List<MessageEntity> getMessagesByUser(String userId) {
        UserEntity users = userService.getUserById(userId);
        return messageRepository.findByArrangedPersonName(users.getUsername());
    }

    // 根据Id查询用户是否存在
    public boolean hasMessageEntityById(String messageId) {
        if (StringUtils.isEmpty(messageId)) {
            return false;
        }
        return messageRepository.existsById(messageId);
    }

    // 根据id查询用户
    @Cacheable(value = "User_Cache", key = "#messageId")
    public MessageEntity getMessageEntityById(String messageId) {
        if (!hasMessageEntityById(messageId)) {
            throw new ResourceException(ApplicationMessageEnum.MESSAGE_ID_NOT_FOUND_ERROR);
        }
        return messageRepository.findById(messageId).get();
    }
}
