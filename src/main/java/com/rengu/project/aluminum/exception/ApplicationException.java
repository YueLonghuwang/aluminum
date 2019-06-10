package com.rengu.project.aluminum.exception;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;

/**
 * com.rengu.project.aluminum.exception
 *
 * @author hanchangming
 * @date 2019-06-10
 */

public abstract class ApplicationException extends RuntimeException {

    private ApplicationMessageEnum applicationMessageEnum;

    public ApplicationException(ApplicationMessageEnum applicationMessageEnum) {
        super(applicationMessageEnum.getMessage());
        this.applicationMessageEnum = applicationMessageEnum;
    }

    public ApplicationMessageEnum getMessageEnum() {
        return applicationMessageEnum;
    }
}

