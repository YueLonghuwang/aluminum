package com.rengu.project.aluminum.exception;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;

/**
 * Author: XYmar
 * Date: 2019/6/28 10:47
 */
public class ProcessException extends ApplicationException {
    public ProcessException(ApplicationMessageEnum applicationMessageEnum) {
        super(applicationMessageEnum);
    }
}
