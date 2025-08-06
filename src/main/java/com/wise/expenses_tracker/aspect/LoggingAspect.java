package com.wise.expenses_tracker.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.wise.expenses_tracker.service.interfaces.CategoryService.getCategoryById(..))")
    public void categoryServiceGetCategoryById() {}

    @Pointcut("execution(* com.wise.expenses_tracker.service.interfaces.CategoryService.updateCategory(..))")
    public void categoryServiceUpdateCategory() {}

    @Before("categoryServiceGetCategoryById()")
    public void logBeforeGetCategoryById(JoinPoint joinPoint) {
        System.out.println("Before executing getCategoryById: " + joinPoint.getArgs()[0]);
    }

    @Around("categoryServiceUpdateCategory")
    public Object logBeforeAndAfterUpdateCategory(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before executing update category: " + joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        System.out.println("After executing update category: " + joinPoint.getArgs()[0] + " returned: " + result);

        return result;
    }
}
