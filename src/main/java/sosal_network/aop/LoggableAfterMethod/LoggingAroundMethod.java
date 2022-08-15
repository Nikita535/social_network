package sosal_network.aop.LoggableAfterMethod;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAroundMethod {

    @Pointcut("@annotation(LoggableAroundMethod)")
    public void executeLogging(){}


    @Around("executeLogging()")
    public Object logMethodCall(ProceedingJoinPoint proceedingJoinPoint)throws Throwable{
        long startTime= System.currentTimeMillis();

        Object returnValue = proceedingJoinPoint.proceed();
        StringBuilder message =new StringBuilder("Method: ");
        message.append(proceedingJoinPoint.getSignature().getName()).append(" !");

        long spendTime = System.currentTimeMillis()-startTime;
        message.append("time: ").append(spendTime).append(" ms");
        log.info(message.toString());
        return returnValue;
    }
}
