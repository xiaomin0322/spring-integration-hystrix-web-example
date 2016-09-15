package cn.eakay.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.eakay.service.Service;

/**
 * Created by zzm on 16/4/15.
 */

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    Service service;


    @RequestMapping(value = "/getUserHystrix", method = RequestMethod.GET)
    public String getUser() {
        return service.get("user");
    }


    @RequestMapping(value = "/getText", method = RequestMethod.GET)
    public String getText() {
        return "Hello World";
    }

}
