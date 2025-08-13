package com.wise.expenses_tracker.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.wise.expenses_tracker.service.interfaces.AuthenticationService.register(..))")
    public void register() {}

    @Pointcut("execution(* com.wise.expenses_tracker.controller.CategoryController.getAllCategories(..))")
    public void expensesServiceGetAllCategories() {}

    @Pointcut("execution(* com.wise.expenses_tracker.service.interfaces.ExpensesService.getCategoryExpenses(..))")
    public void expensesServiceGetCategoryExpenses() {}

    @Before("expensesServiceGetAllCategories()")
    public void logBeforeGetAllCategories(JoinPoint joinPoint) {
        System.out.println("Before executing getAllCategories: " + joinPoint.getArgs()[0]);
    }

    @Around("register()")
    public Object register(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before executing register with ID: " + joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        System.out.println("After executing register, returned: " + result);

        return result;
    }

    @Before("expensesServiceGetCategoryExpenses()")
    public void logBeforeGetCategoryExpenses(JoinPoint joinPoint) {
        System.out.println("Before executing getCategoryExpenses: " + joinPoint.getArgs()[0]);
    }

}
