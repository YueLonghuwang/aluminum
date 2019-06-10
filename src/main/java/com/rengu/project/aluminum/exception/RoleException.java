package com.rengu.project.aluminum.exception;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;

/**
 * com.rengu.project.aluminum.exception
 *
 * @author hanchangming
 * @date 2019-06-10
 */
public class RoleException extends ApplicationException {

    public RoleException(ApplicationMessageEnum applicationMessageEnum) {
        super(applicationMessageEnum);
    }
}