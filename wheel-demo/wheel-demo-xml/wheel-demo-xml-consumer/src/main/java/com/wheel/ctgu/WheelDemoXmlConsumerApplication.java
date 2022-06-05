package com.wheel.ctgu;


import com.wheel.ctgu.spring.annonation.Reference;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class WheelDemoXmlConsumerApplication {

    @Reference
    private static DemoService demoService;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("wheel-consumer.xml");
        System.out.println(demoService.sayHello("1111111"));
    }


}
