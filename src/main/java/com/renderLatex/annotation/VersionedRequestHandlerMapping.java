package com.renderLatex.annotation;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Special implementation of RequestMappingHandlerMapping that optionally adds version suffixes to controller URLs.
 */
public class VersionedRequestHandlerMapping extends RequestMappingHandlerMapping {
    /**
     * Utility function to manually add routes from a given handler instance.
     *
     * Only for testing!
     */
    public void populateFromHandler(Object handler) {
        detectHandlerMethods(handler);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info != null) {
            ApiVersion methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
            if (methodAnnotation != null) {
                // Prepend our version mapping to the real method mapping.
                info = createApiVersionInfo(methodAnnotation).combine(info);
            }

            ApiVersion typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
            if (methodAnnotation == null && typeAnnotation != null) {
                // Append our version mapping to the real controller mapping.
                info = createApiVersionInfo(typeAnnotation).combine(info);
            }
        }
        return info;
    }

    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation) {
        return new RequestMappingInfo(
                new PatternsRequestCondition(annotation.value(), getUrlPathHelper(), getPathMatcher(),
                        useSuffixPatternMatch(), useTrailingSlashMatch(), getFileExtensions()),
                null, null, null, null, null, null);
    }
}
