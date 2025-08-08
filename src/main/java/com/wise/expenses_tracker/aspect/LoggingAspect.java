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

    @Pointcut("execution(* com.wise.expenses_tracker.controller.ExpensesController.createExpense(..))")
    public void expensesServiceCreateExpense() {}

    @Pointcut("execution(* com.wise.expenses_tracker.controller.ExpensesController.updateExpense(..))")
    public void expensesServiceUpdateExpense() {}

    @Before("expensesServiceCreateExpense()")
    public void logBeforeCreateExpense(JoinPoint joinPoint) {
        System.out.println("Before executing createExpense: " + joinPoint.getArgs()[0]);
    }

    @Around("expensesServiceUpdateExpense()")
    public Object logBeforeAndAfterUpdateExpense(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before executing updateExpense with ID: " + joinPoint.getArgs()[0]);
        Object result = joinPoint.proceed();
        System.out.println("After executing updateExpense, returned: " + result);

        return result;
    }
    
}
