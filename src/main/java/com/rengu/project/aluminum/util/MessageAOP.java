package com.rengu.project.aluminum.util;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.controller.AlgorithmAndServerController;
import com.rengu.project.aluminum.controller.ModelResourceController;
import com.rengu.project.aluminum.controller.StandardController;
import com.rengu.project.aluminum.controller.UserController;
import com.rengu.project.aluminum.entity.*;
import com.rengu.project.aluminum.repository.MessageRepository;
import com.rengu.project.aluminum.repository.UserRepository;
import com.rengu.project.aluminum.service.MessageService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author : yaojiahao
 * Date: 2019/6/17 8:57
 **/

@Aspect
@Component
@Slf4j
public class MessageAOP {
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageAOP(MessageRepository messageRepository, MessageService messageService, UserService userService, UserRepository userRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Pointcut(value = "execution(public * com.rengu.project.aluminum.controller..*(..))")
    private void requestPonitCut() {

    }

    @AfterReturning(pointcut = "requestPonitCut()", returning = "resultEntity")
    public void doAfterReturning(JoinPoint joinPoint, ResultEntity resultEntity) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        // 返回一个java.security.Principal  对象，该对象包含当前授权用户的名称
        if (httpServletRequest.getUserPrincipal() != null) {
            String mainOperatorName = null;
            String arrangedPersonName = null;
            int messageOperate = ApplicationConfig.ARRANGE_NONE_OPERATE;
            int mainBody = ApplicationConfig.MAINBODY_NONE;
            String description = "";
            // 用户接口
            if (joinPoint.getTarget().getClass().equals(UserController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                System.out.println(type);
                UserEntity userEntity = (UserEntity) resultEntity.getData();
                mainOperatorName = userService.getUserByUsername("admin").getUsername();                         // 操作人
                arrangedPersonName = userEntity.getUsername();
                switch (joinPoint.getSignature().getName()) {
                    case "updateSecurityClassificationById": {
                        messageOperate = ApplicationConfig.ARRANGE_ROLE_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_USERS;
                        StringBuilder stringBuilder = new StringBuilder();
                        description = "系统管理员已将您的密级更新为: " + userEntity.getSecurityClassification();
                        break;
                    }
                    case "": {

                    }
                }
            }
            // 模型资源管理
            if (joinPoint.getTarget().getClass().equals(ModelResourceController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                ModelResourceEntity modelResourceEntity = (ModelResourceEntity) resultEntity.getData();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您创建了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您删除了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您更新了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                    case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }
                }

            }
            // 标准规范管理
            if (joinPoint.getTarget().getClass().equals(StandardController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                StandardEntity standardEntity = (StandardEntity) resultEntity.getData();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您创建了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您删除了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您更新了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }
                }

            }
            // 公共算法/服务管理
            if (joinPoint.getTarget().getClass().equals(AlgorithmAndServerController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                AlgorithmAndServerEntity algorithmAndServerEntity = (AlgorithmAndServerEntity) resultEntity.getData();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您创建了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您删除了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您更新了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                    case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }
                }

            }
            // 工具/软件管理
            if (joinPoint.getTarget().getClass().equals(ToolsAndSoftwareEntity.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                ToolsAndSoftwareEntity toolsAndSoftwareEntity = (ToolsAndSoftwareEntity) resultEntity.getData();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您创建了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您删除了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainBody = ApplicationConfig.MAINBODY_MODEL;
                        description = "您更新了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }
                }

            }
            if (messageOperate != ApplicationConfig.ARRANGE_NONE_OPERATE || !StringUtils.isEmpty(description)) {
                MessageEntity message = new MessageEntity();
                message.setMainOperatorName(mainOperatorName);
                message.setArrangedPersonName(arrangedPersonName);
                message.setMessageOperate(messageOperate);
                message.setMainBody(mainBody);
                message.setDescription(description);
                messageRepository.save(message);
            }
            List<UserEntity> userEntityList = userRepository.findAll();
            for (UserEntity userEntity : userEntityList) {
                Long count = messageRepository.countByArrangedPersonNameAndIfRead(userEntity.getUsername(), false);
                simpMessagingTemplate.convertAndSend("/personalInfo/" + userEntity.getUsername(), new ResultEntity<>(count));
            }
        }
    }
}
