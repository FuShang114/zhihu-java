package com.zhihu.user;

import com.zhihu.common.BusinessException;
import com.zhihu.common.ErrorCode;
import com.zhihu.security.JwtService;
import com.zhihu.user.dto.LoginRequest;
import com.zhihu.user.dto.LoginView;
import com.zhihu.user.dto.RegisterRequest;
import com.zhihu.user.dto.UserView;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserView register(RegisterRequest req) {
        // 先查重给友好错误；真正的并发裁判是唯一索引——败者走 catch（Go 版 1062 的 Java 平移）
        if (userRepository.existsByUsername(req.username())) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN);
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname() == null ? req.username() : req.nickname());
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN);
        }
        return UserView.from(user);
    }

    public LoginView login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username()).orElse(null);
        // 用户不存在与密码错误同一码：防用户枚举（Go 版同款设计）
        if (user == null || !passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }
        if (user.getStatus() != User.STATUS_NORMAL) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被封禁");
        }
        return new LoginView(jwtService.issue(user.getId(), user.getUsername()), UserView.from(user));
    }

    public UserView me(long userId) {
        return userRepository.findById(userId)
                .map(UserView::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}
