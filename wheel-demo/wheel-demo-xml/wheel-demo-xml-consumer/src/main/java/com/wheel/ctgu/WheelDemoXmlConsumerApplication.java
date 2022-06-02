package com.wheel.ctgu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WheelDemoXmlConsumerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(WheelDemoXmlConsumerApplication.class, args);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("wheel-consumer.xml");
        context.start();
        DemoService demoService = context.getBean("demoService",DemoService.class);
        demoService.sayHello("测试-------->");
    }

}
