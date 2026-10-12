package com.zhihu.e2e;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** 用户域全流程：注册→登录→me；各类失败路径的业务码契约。 */
class UserFlowE2ETest extends E2eTestBase {

    @Test
    void register_login_me_happyPath() {
        String username = randomName();
        var reg = post("/api/v1/users/register",
                Map.of("username", username, "password", "password123", "nickname", "尼克"), null);
        assertThat(reg.get("code").asInt()).as("%s", reg).isZero();
        assertThat(reg.get("data").get("username").asText()).isEqualTo(username);
        assertThat(reg.get("data").get("passwordHash")).isNull(); // 响应永不携带密码指纹

        var login = post("/api/v1/users/login",
                Map.of("username", username, "password", "password123"), null);
        assertThat(login.get("code").asInt()).as("%s", login).isZero();
        String token = login.get("data").get("token").asText();
        assertThat(token).isNotBlank();

        var me = get("/api/v1/users/me", token);
        assertThat(me.get("code").asInt()).as("%s", me).isZero();
        assertThat(me.get("data").get("username").asText()).isEqualTo(username);
    }

    @Test
    void register_duplicateUsername_returns20001() {
        String username = randomName();
        newUser(username);
        var res = post("/api/v1/users/register",
                Map.of("username", username, "password", "password123"), null);
        assertThat(res.get("code").asInt()).isEqualTo(20001);
    }

    @Test
    void register_missingParams_returns10001() {
        var res = post("/api/v1/users/register", Map.of("username", "only-name"), null);
        assertThat(res.get("code").asInt()).isEqualTo(10001);
    }

    @Test
    void register_shortPassword_returns10001() {
        var res = post("/api/v1/users/register",
                Map.of("username", randomName(), "password", "123"), null);
        assertThat(res.get("code").asInt()).isEqualTo(10001);
    }

    @Test
    void login_wrongPassword_returns20002() {
        String username = randomName();
        newUser(username);
        var res = post("/api/v1/users/login", Map.of("username", username, "password", "wrong-pass"), null);
        assertThat(res.get("code").asInt()).isEqualTo(20002);
    }

    @Test
    void login_unknownUser_returns20002_sameAsWrongPassword() {
        var res = post("/api/v1/users/login", Map.of("username", randomName(), "password", "whatever1"), null);
        assertThat(res.get("code").asInt()).isEqualTo(20002); // 与密码错误同一码：防用户枚举
    }

    @Test
    void me_withoutToken_returns10002() {
        var res = get("/api/v1/users/me");
        assertThat(res.get("code").asInt()).isEqualTo(10002);
    }

    @Test
    void me_withForgedToken_returns10002() {
        var res = get("/api/v1/users/me", "forged.token.value");
        assertThat(res.get("code").asInt()).isEqualTo(10002);
    }
}
