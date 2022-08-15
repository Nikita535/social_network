package sosal_network.aop.LoggableBeforeMethod;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggingBeforeMethod {


    @Pointcut("@annotation(LoggableBeforeMethod)")
    public void executeLogging(){}



    @Before("executeLogging()")
    public void logMethodCall(JoinPoint joinPoint){
        StringBuilder message = new StringBuilder("Method: ");
        message.append(joinPoint.getSignature().getName()).append(" !");
        Arrays.stream(joinPoint.getArgs()).forEach(arg ->
                message.append("args: ").append(arg).append(" !"));

        log.info(message.toString());
    }

    @AfterThrowing(pointcut = "@annotation(LoggableBeforeMethod)", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }


}
