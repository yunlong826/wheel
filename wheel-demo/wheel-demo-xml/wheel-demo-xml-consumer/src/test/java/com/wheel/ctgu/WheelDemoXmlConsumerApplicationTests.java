package com.wheel.ctgu;

import com.wheel.ctgu.spring.annonation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class WheelDemoXmlConsumerApplicationTests {

    @Reference
    private DemoService demoService;

    @Test
    void contextLoads() {
        demoService.sayHello("111111111");
    }

}
