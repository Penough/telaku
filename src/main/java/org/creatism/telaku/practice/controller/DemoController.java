package org.creatism.telaku.practice.controller;

import org.creatism.telaku.practice.sip.SipClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private SipClient client;

    @PutMapping("/register")
    public String register() {
        try {
            client.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

    @PutMapping("/call/{phone}")
    public String call(@PathVariable String phone) {
        try {
            client.call(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }

}
