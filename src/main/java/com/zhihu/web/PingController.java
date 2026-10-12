package com.zhihu.web;

import com.zhihu.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("pong");
    }
}
