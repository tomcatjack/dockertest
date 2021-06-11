package com.luqi.dockertest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author changanKing
 * @date 2021/2/26 19:08
 */
@Slf4j
@Controller
public class IndexController {

    @GetMapping({"/","/index"})
    public String index(){
        log.info("{==================初始化........=================}");
        return "index";
    }

}
