package com.rengu.project.aluminum.exception;

import com.rengu.project.aluminum.enums.ApplicationMessageEnum;

/**
 * com.rengu.project.aluminum.exception
 *
 * @author hanchangming
 * @date 2019-06-11
 */
public class FileException extends ApplicationException {

    public FileException(ApplicationMessageEnum applicationMessageEnum) {
        super(applicationMessageEnum);
    }
}
