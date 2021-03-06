package org.javers.spring.aspect;

import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author Pawel Szymczyk
 */
public class MethodEqualsBasedMatcher extends DynamicMethodMatcherPointcut {
    private final Method method;

    public MethodEqualsBasedMatcher(Method method) {
        this.method = method;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
        return this.method.equals(method);
    }
}
