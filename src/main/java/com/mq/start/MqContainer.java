package com.mq.start;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MqContainer implements Container {

    public static final String DEFAULT_MQ_CONFIG_SPRING_XML = "classpath:mq-broker.xml";

    AbstractApplicationContext context = null;

    private static MqContainer container = new MqContainer();

    private MqContainer() {

    }

    public static MqContainer getInstance() {
        return container;
    }

    @Override
    public void start() {
        context = new ClassPathXmlApplicationContext(DEFAULT_MQ_CONFIG_SPRING_XML);
        context.start();
    }

    @Override
    public void stop() {
        if (context != null) {
            context.stop();
        }
    }

    @Override
    public ApplicationContext getContext() {
        return context;
    }

}
