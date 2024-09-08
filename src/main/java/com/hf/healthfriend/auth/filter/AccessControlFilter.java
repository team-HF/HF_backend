package com.hf.healthfriend.auth.filter;

import com.hf.healthfriend.auth.accesscontrol.AccessControlTrigger;
import com.hf.healthfriend.auth.accesscontrol.AccessController;
import com.hf.healthfriend.auth.accesscontrol.requestpath.RequestPathTree;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * {@link org.springframework.context.annotation.Profile}이 "accesscontrol"로 지정되어 있지 않으면
 * 해당 필터는 적용되지 않는다.
 * @author PGD
 * @see AccessController
 * @see AccessControlTrigger
 * @see RequestPathTree
 * @see com.hf.healthfriend.auth.accesscontrol.requestpath.RequestPathNode
 */
@Slf4j
public class AccessControlFilter extends OncePerRequestFilter {
    private final ApplicationContext context;
    private final RequestPathTree<BeanAndMethod> accessControls;

    public AccessControlFilter(ApplicationContext context) {
        this.context = context;
        this.accessControls = new RequestPathTree<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.context.getBeansWithAnnotation(AccessController.class)
                .values()
                .forEach((bean) -> {
                    Method[] methods = AopProxyUtils.ultimateTargetClass(bean).getMethods();
                    for (Method method : methods) {
                        AccessControlTrigger trigger = method.getAnnotation(AccessControlTrigger.class);
                        if (trigger == null) {
                            log.warn("AccessControlTrigger annotation is missing for {}.{}",
                                    bean.getClass(),
                                    method.getName());
                            continue;
                        }
                        String httpMethod = trigger.method().trim().toUpperCase();
                        if (!isMethodNameAcceptable(httpMethod)) {
                            log.warn("HttpMethod is not acceptable: {}", httpMethod);
                            return;
                        }
                        String path = trigger.path().trim();
                        log.trace("path={}, method={}", path, httpMethod);
                        this.accessControls.add(path, HttpMethod.valueOf(httpMethod), new BeanAndMethod(bean, method));
                    }
                });
        log.trace("{}", this.accessControls);
    }

    private boolean isMethodNameAcceptable(String methodName) {
        return Set.of("GET", "POST", "DELETE", "PATCH", "PUT", "HEAD")
                .contains(methodName);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    private static class BeanAndMethod {
        private Object bean;
        private Method method;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod().toUpperCase());
        log.trace("{} {}", httpMethod, request.getRequestURI());
        String path = request.getRequestURI();

        BeanAndMethod beanAndMethod = this.accessControls.getElement(path, httpMethod).orElse(null);

        if (beanAndMethod == null) {
            if (log.isTraceEnabled()) {
                log.trace("No access control for {} {}", httpMethod, path);
                log.trace("Just proceed");
            }
            filterChain.doFilter(request, response);
            return;
        }

        Object accessControlBean = beanAndMethod.getBean();
        Method accessControlMethod = beanAndMethod.getMethod();
        try {
            Class<?>[] parameterTypes = accessControlMethod.getParameterTypes();
            if (parameterTypes.length != 2
                    || !parameterTypes[0].isAssignableFrom(BearerTokenAuthentication.class)
                    || !parameterTypes[1].isAssignableFrom(HttpServletRequest.class)) {
                throw new RuntimeException("Parameter types of access control is wrong: "
                        + accessControlBean.getClass() + "." + accessControlMethod.getName());
            }
            Boolean result = (Boolean)accessControlMethod.invoke(accessControlBean,
                    SecurityContextHolder.getContext().getAuthentication(),
                    request);
            if (result) {
                log.trace("Access Control: Request for {} {} is allowed", httpMethod, path);
                filterChain.doFilter(request, response);
                return;
            }
            log.warn("Access Control: Request for {} {} is not allowed", httpMethod, path);
        } catch (ClassCastException e) {
            throw new RuntimeException(
                    String.format(
                            "Access control method %s.%s doesn't return a boolean",
                            accessControlBean.getClass(),
                            accessControlMethod.getName()
                    ),
                    e
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.warn("An exception has occured for {} {}", httpMethod, path, e.getTargetException());
            log.warn("Access control method={}.{}", accessControlBean.getClass(), accessControlMethod.getName());
        }
        throw new AccessDeniedException("Access Denied from AccessControl"); // TODO: 구체적인 예외 정의해서 던지기 (AccessDeniedExceptionResolverFilter에서 받아서 ErrorCode 추가할 수 있게)
    }
}
