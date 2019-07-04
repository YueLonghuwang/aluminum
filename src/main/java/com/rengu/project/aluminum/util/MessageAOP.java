package com.rengu.project.aluminum.util;

import com.rengu.project.aluminum.ApplicationConfig;
import com.rengu.project.aluminum.controller.*;
import com.rengu.project.aluminum.entity.*;
import com.rengu.project.aluminum.repository.MessageRepository;
import com.rengu.project.aluminum.repository.UserRepository;
import com.rengu.project.aluminum.service.MessageService;
import com.rengu.project.aluminum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.rengu.project.aluminum.controller.StandardController;

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
    private final ApplicationConfig applicationConfig;

    public MessageAOP(MessageRepository messageRepository, MessageService messageService, UserService userService, UserRepository userRepository, SimpMessagingTemplate simpMessagingTemplate, ApplicationConfig applicationConfig) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.applicationConfig = applicationConfig;
    }

    @Pointcut(value = "execution(public * com.rengu.project.aluminum.controller..*(..))")
    private void requestPonitCut() {

    }

    // 前置通知
    @Before("requestPonitCut()")
    public void test() {

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
                UserEntity userEntity = (UserEntity) resultEntity.getData();
                mainBody = ApplicationConfig.MAINBODY_USERS;
                arrangedPersonName = userEntity.getUsername();
                switch (joinPoint.getSignature().getName()) {
                    case "updateSecurityClassificationById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        String securityClassification = null;
                        switch (userEntity.getSecurityClassification()) {
                            case 0: {
                                securityClassification = "公开";
                                break;
                            }
                            case 1: {
                                securityClassification = "秘密";
                                break;
                            }
                            case 2: {
                                securityClassification = "机密";
                                break;
                            }
                        }
                        mainOperatorName = applicationConfig.getDEFAULT_SECURITY_ROLE_NAME(); // 操作人
                        description = "安全员已将 " + userEntity.getUsername() + "的密级更新为: " + securityClassification;
                        break;
                    }
                    case "updateUserByAdmin": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        description = "管理员已将 " + userEntity.getUsername() + " 的部门修改为: " + userEntity.getDepartment().getName();
                        break;
                    }
                    case "saveUserByAdmin": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = "管理员创建了 " + userEntity.getUsername() + " 用户";
                        break;
                    }
                    case "updatePwdByAdmin": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = "管理员修改了 " + userEntity.getUsername() + " 的密码";
                        break;
                    }
                    case "deleteUserById": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = "管理员删除了" + userEntity.getUsername() + " 用户";
                        break;
                    }
                }
            }
            // 部门管理
            if (joinPoint.getTarget().getClass().equals(DepartmentController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                mainBody = ApplicationConfig.MAINBODY_DEPARTMENT;
                switch (joinPoint.getSignature().getName()) {
                    case "saveDepartment": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        DepartmentEntity departmentEntity = (DepartmentEntity) resultEntity.getData();
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = "管理员创建了 " + departmentEntity.getName() + " 部门";
                        break;
                    }
                    case "deleteDepartmentById": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        DepartmentEntity departmentEntity = (DepartmentEntity) resultEntity.getData();
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = "管理员删除了 " + departmentEntity.getName() + " 部门";
                        break;
                    }
                    case "updateDepartmentById": {
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        DepartmentEntity departmentEntity = (DepartmentEntity) resultEntity.getData();
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = "管理员修改了 " + departmentEntity.getName() + " 部门属性";
                        break;
                    }
                    case "departmentAddUsersById": {
                        Set<UserEntity> userEntitySet = (Set<UserEntity>) resultEntity.getData();
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        StringBuilder stringBuilder = new StringBuilder();
                        String departmentName = null;
                        for (UserEntity userEntity : userEntitySet) {
                            stringBuilder.append(userEntity.getUsername()).append(" ");
                            departmentName = userEntity.getDepartment().getName();
                        }
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        description = "管理员添加了用户 " + stringBuilder + " 到" + departmentName + " 部门";
                        break;
                    }
                    case "departmentRemoveUsersById": {
                        Map map = (Map) resultEntity.getData();
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        description = "管理员将用户 " + map.get("username") + " 从" + map.get("departmentName") + " 部门移除";
                        break;
                    }
                    case "updateUserForDepartmentByAudit": {
                        UserEntity userEntity = (UserEntity) resultEntity.getData();
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        mainOperatorName = applicationConfig.getDEFAULT_ADMIN_ROLE_NAME();
                        description = "管理员修改了用户 " + userEntity.getUsername() + " 到" + userEntity.getDepartment().getName() + " 部门";
                        break;
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
                mainOperatorName = modelResourceEntity.getCreateUser().getUsername();
                mainBody = ApplicationConfig.MAINBODY_MODEL;
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = modelResourceEntity.getCreateUser().getUsername() + " 创建了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = modelResourceEntity.getCreateUser().getUsername() + " 删除了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = modelResourceEntity.getCreateUser().getUsername() + " 更新了" + modelResourceEntity.getName() + "模型资源";
                        break;
                    }
                   /* case "downloadResourceById": {
                        messageOperate = ApplicationConfig.DOWNLOAD_OPERATE;
                        break;
                    }*/
                }

            }
            // 标准规范管理
            if (joinPoint.getTarget().getClass().equals(StandardController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                mainBody = ApplicationConfig.MAINBODY_STANDARD;
                StandardEntity standardEntity = (StandardEntity) resultEntity.getData();
                mainOperatorName = standardEntity.getCreateUser().getUsername();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = standardEntity.getCreateUser().getUsername() + " 创建了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = standardEntity.getCreateUser().getUsername() + " 删除了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = standardEntity.getModifyUser().getUsername() + " 更新了" + standardEntity.getName() + "标准规范";
                        break;
                    }
                    /*case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }*/
                }

            }
            // 公共算法/服务管理
            if (joinPoint.getTarget().getClass().equals(AlgorithmAndServerController.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                mainBody = ApplicationConfig.MAINBODY_ALGORITHM_SERVER;
                AlgorithmAndServerEntity algorithmAndServerEntity = (AlgorithmAndServerEntity) resultEntity.getData();
                mainOperatorName = algorithmAndServerEntity.getCreateUser().getUsername();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = algorithmAndServerEntity.getCreateUser().getUsername() + " 创建了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = algorithmAndServerEntity.getCreateUser().getUsername() + " 删除了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = algorithmAndServerEntity.getModifyUser().getUsername() + " 更新了" + algorithmAndServerEntity.getName() + "公共算法/服务资源";
                        break;
                    }
                /*    case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }*/
                }

            }
            // 工具/软件管理
            if (joinPoint.getTarget().getClass().equals(ToolsAndSoftwareEntity.class)) {
                String type = resultEntity.getData().getClass().toString();
                if (type.equals("class java.util.ArrayList") || type.equals("class java.lang.Boolean") || type.equals("class java.util.HashMap") || type.equals("class org.springframework.data.domain.PageImpl")) {
                    return;
                }
                mainBody = ApplicationConfig.MAINBODY_TOOLS_SOFTWARE;
                ToolsAndSoftwareEntity toolsAndSoftwareEntity = (ToolsAndSoftwareEntity) resultEntity.getData();
                mainOperatorName = toolsAndSoftwareEntity.getCreateUser().getUsername();
                switch (joinPoint.getSignature().getName()) {
                    case "saveResource": {
                        messageOperate = ApplicationConfig.ADD_OPERATE;
                        description = toolsAndSoftwareEntity.getCreateUser().getUsername() + " 创建了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    case "deleteResourceById": {
                        messageOperate = ApplicationConfig.DELETE_OPERATE;
                        description = toolsAndSoftwareEntity.getCreateUser().getUsername() + " 删除了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    case "updateResourceById": {
                        messageOperate = ApplicationConfig.MODIFY_OPERATE;
                        description = toolsAndSoftwareEntity.getCreateUser().getUsername() + " 更新了" + toolsAndSoftwareEntity.getName() + "工具/软件管理资源";
                        break;
                    }
                    /*case "downloadResourceById": {
                        System.out.println(resultEntity.getData());
                        System.out.println(resultEntity);
                        break;
                    }*/
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
