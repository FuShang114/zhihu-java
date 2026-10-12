package com.zhihu.user;

import com.zhihu.common.Result;
import com.zhihu.security.CurrentUser;
import com.zhihu.user.dto.LoginRequest;
import com.zhihu.user.dto.LoginView;
import com.zhihu.user.dto.RegisterRequest;
import com.zhihu.user.dto.UserView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserView> register(@Valid @RequestBody RegisterRequest req) {
        return Result.ok(userService.register(req));
    }

    @PostMapping("/login")
    public Result<LoginView> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(userService.login(req));
    }

    @GetMapping("/me")
    public Result<UserView> me(@CurrentUser Long userId) {
        return Result.ok(userService.me(userId));
    }
}
