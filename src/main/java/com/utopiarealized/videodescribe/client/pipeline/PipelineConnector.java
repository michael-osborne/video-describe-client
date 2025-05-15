package com.utopiarealized.videodescribe.client.pipeline;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.annotation.Produce;
import com.utopiarealized.videodescribe.client.pipeline.model.PipelineData;
import java.lang.reflect.Method;

@Component
public class PipelineConnector  {


    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;


    @PostConstruct
    public void start() {
        Map<String, Object> beans = ApplicationContextHolder.getContext().getBeansWithAnnotation(Service.class);
        for (Object bean : beans.values()) {
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(Consume.class)) {
                    Consume consume = method.getAnnotation(Consume.class);
                    registerConsumer(bean, method, consume.threads());
                } else {
                    if (method.isAnnotationPresent(Produce.class)) {
                        Produce produce = method.getAnnotation(Produce.class);
                        registerProducer(bean, method, produce.threads());
                    }
                }
            }
        }
    }
    

    private void registerConsumer(Object bean, Method method, int threads) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("Method " + method.getName() + " on bean " + bean.getClass().getName() + " must have exactly one parameter");
        }

        Class<?> parameterType = parameterTypes[0];
        if (!PipelineData.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException("Method " + method.getName() + " on bean " + bean.getClass().getName() + " must have a parameter that extends PipelineData");
        }
        
        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("Method " + method.getName() + " on bean " + bean.getClass().getName()
                    + " must have a return type of void");
        }
        
        pipelineOrchestrator.buildPipelineConsumer(threads, bean, method, parameterType);
    }

    private void registerProducer(Object bean, Method method, int threads) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 0) {
            throw new IllegalArgumentException("Method " + method.getName() + " on bean " + bean.getClass().getName()
                    + " must haveno parameters");
        }


        if (method.getReturnType() != void.class) {
            throw new IllegalArgumentException("Method " + method.getName() + " on bean " + bean.getClass().getName()
                    + " must have a return type of void");
        }
        pipelineOrchestrator.buildPipelineProducer(threads, bean, method);
    }
}