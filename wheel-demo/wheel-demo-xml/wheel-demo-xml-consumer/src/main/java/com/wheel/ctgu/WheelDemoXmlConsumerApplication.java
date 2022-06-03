package com.wheel.ctgu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class WheelDemoXmlConsumerApplication {

    public static void main(String[] args) throws IOException {
//        SpringApplication.run(WheelDemoXmlConsumerApplication.class, args);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("wheel-consumer.xml");
        context.start();
        DemoService demoService = context.getBean("demoService",DemoService.class);
        demoService.sayHello("测试-------->");
        System.in.read();
    }

}
