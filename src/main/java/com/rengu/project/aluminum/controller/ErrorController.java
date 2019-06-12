package com.rengu.project.aluminum.controller;

import com.rengu.project.aluminum.entity.ResultEntity;
import com.rengu.project.aluminum.enums.ApplicationMessageEnum;
import com.rengu.project.aluminum.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * com.rengu.project.aluminum.controller
 *
 * @author hanchangming
 * @date 2019-06-10
 */

@Slf4j
@RestControllerAdvice
public class ErrorController {

    // 应用异常捕获
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = ApplicationException.class)
    public ResultEntity<ApplicationException> applicationExceptionHandler(ApplicationException applicationException) {
        log.error(applicationException.getMessage(), applicationException);
        return new ResultEntity<>(applicationException.getMessageEnum(), applicationException);
    }

    // 运行时异常捕获
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResultEntity exceptionHandler(Exception exception) {
        String errorMessage = exception.getMessage() == null ? ApplicationMessageEnum.SYSTEM_ERROR.getMessage() : exception.getMessage();
        log.error(errorMessage, exception);
        return new ResultEntity<>(ApplicationMessageEnum.SYSTEM_ERROR, exception);
    }
}

