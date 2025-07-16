package fun.cyhgraph.handler;

import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.exception.BaseException;
import fun.cyhgraph.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，能对项目中抛出的异常进行捕获和处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result<String> handleRedisDown(RedisConnectionFailureException e) {
        log.warn("Redis服务不可用: {}", e.getMessage());
        return Result.success("操作成功（缓存不可用）");
    }
    @ExceptionHandler
    public Result excepitonHandler(BaseException ex){
        log.info("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        System.out.println("什么逆天sql错误到是打印啊！！！" + message);
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXiST;
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
