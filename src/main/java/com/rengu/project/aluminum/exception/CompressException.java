package com.rengu.project.aluminum.exception;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;

/**
 * com.rengu.project.aluminum.exception
 *
 * @author hanchangming
 * @date 2019-06-12
 */
public class CompressException extends ApplicationException {

    public CompressException(ApplicationMessageEnum applicationMessageEnum) {
        super(applicationMessageEnum);
    }
}
