package com.luqi.dockertest;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("test")
    public String test(){
        System.out.println("hello - docker - test");
        return "hello - docker - test";
    }

    @PostMapping(params = {"method=cainiao.bms.order.consign.confirm"},
        produces = "application/xml;charset=UTF-8")
    public Object deliveryXml(@RequestBody @Validated BmsOrderConfirmRequest request) {

        return null;
    }
}
