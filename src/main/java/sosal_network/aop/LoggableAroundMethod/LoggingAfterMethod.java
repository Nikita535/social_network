package sosal_network.aop.LoggableAroundMethod;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAfterMethod {


    @Pointcut("@annotation(LoggableAfterMethod)")
    public void executeLogging(){}



    @AfterReturning(pointcut = "executeLogging()",returning = "returnValue")
    public void logMethodCall(JoinPoint joinPoint,Object returnValue){
        StringBuilder message =new StringBuilder("Method: ");
        message.append(joinPoint.getSignature().getName()).append(" !");
        message.append("return: ").append(returnValue);

        log.info(message.toString());
    }

    @AfterThrowing(pointcut = "@annotation(LoggableAfterMethod)", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }
}
