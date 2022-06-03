package com.wheel.ctgu;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


public class WheelDemoXmlProviderApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(WheelDemoXmlProviderApplication.class, args);
//    }

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("wheel-provider.xml");
        context.start();
        System.in.read();
    }

}
