package org.javers.spring;

import org.javers.common.collections.Sets;
import org.javers.core.Javers;
import org.javers.spring.aspect.JaversCommitAdvice;
import org.javers.spring.aspect.MethodEqualsBasedMatcher;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Pawel Szymczyk
 */
public class JaversPostProcessor implements BeanPostProcessor {

    private final Javers javers;
    private final AuthorProvider authorProvider;

    public JaversPostProcessor(Javers javers) {
        this(javers, new DefaultAuthorProvider());
    }

    public JaversPostProcessor(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return proxy(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

    private Object proxy(Object bean) {
        Class<? extends Object> beanClazz = bean.getClass();

        Set<Method> advicedMethods = new HashSet<>();

        if (isAuditableClass(beanClazz)) {
            advicedMethods.addAll(Sets.asSet(beanClazz.getDeclaredMethods()));
        } else {
            for (Method method : beanClazz.getDeclaredMethods()) {
                if (isAuditableMethod(method)) {
                    advicedMethods.add(method);
                }
            }
        }

        ProxyFactory proxyFactory = proxyFactoryWithAdvisors(bean, advicedMethods);

        return proxyFactory.getAdvisors().length > 0 ? proxyFactory.getProxy() : bean;
    }

    private ProxyFactory proxyFactoryWithAdvisors(Object bean, Set<Method> methods) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(bean);
        addAdvisors(proxyFactory, methods);
        return proxyFactory;
    }

    private void addAdvisors(ProxyFactory proxyFactory, Set<Method> methods) {
        for (Method method : methods) {
            proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new MethodEqualsBasedMatcher(method),
                    new JaversCommitAdvice(javers, authorProvider.provide())));
        }
    }

    private boolean isAuditableClass(Class bean) {
        return bean.isAnnotationPresent(JaversAuditable.class);
    }

    private boolean isAuditableMethod(Method method) {
        return method.isAnnotationPresent(JaversAuditable.class);
    }
}
