package com.zhihu.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E 测试基架：真实 Spring Boot 服务（随机端口）+ 真实 MySQL（zhihu_test 库，create-drop 自动重建）。
 * 所有 E2E 测试类继承它，通过 get/send 方法发真实 HTTP 并拿到 JSON 树自行断言业务码。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class E2eTestBase {

    protected final ObjectMapper om = new ObjectMapper();

    @Value("${local.server.port}")
    int port;

    RestClient rest;

    @BeforeEach
    void initRest() {
        rest = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    /** GET，响应体解析为 JSON 树。任何 HTTP 状态都放行，由测试自行断言业务码 */
    protected JsonNode get(String uri) {
        return parse(execute(rest.get().uri(uri)));
    }

    /** 带 Bearer token 的 GET */
    protected JsonNode get(String uri, String token) {
        return parse(execute(rest.get().uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)));
    }

    /** POST + JSON 体 + 可选 Bearer */
    protected JsonNode post(String uri, Object body, String token) {
        RestClient.RequestHeadersSpec<?> spec = rest.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
        if (token != null) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return parse(execute(spec));
    }

    /** PUT + JSON 体 + Bearer */
    protected JsonNode put(String uri, Object body, String token) {
        RestClient.RequestHeadersSpec<?> spec = rest.put().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return parse(execute(spec));
    }

    private ResponseEntity<String> execute(RestClient.RequestHeadersSpec<?> spec) {
        return spec.accept(MediaType.APPLICATION_JSON)
                .retrieve()
                // 压制 4xx/5xx 自动抛异常：E2E 要拿到错误响应体自己断言业务码
                .onStatus(status -> false, (req, res) -> {
                })
                .toEntity(String.class);
    }

    protected JsonNode parse(ResponseEntity<String> entity) {
        try {
            return om.readTree(entity.getBody() == null ? "{}" : entity.getBody());
        } catch (Exception e) {
            throw new IllegalStateException("响应不是合法 JSON: " + entity.getBody(), e);
        }
    }

    /** 注册一个随机用户并登录，返回其 JWT（新测试用户的快捷工厂） */
    protected String newUser(String username) {
        JsonNode reg = post("/api/v1/users/register",
                Map.of("username", username, "password", "password123", "nickname", "nick-" + username), null);
        assertThat(reg.get("code").asInt()).as("注册应成功: %s", reg).isZero();
        JsonNode login = post("/api/v1/users/login",
                Map.of("username", username, "password", "password123"), null);
        assertThat(login.get("code").asInt()).as("登录应成功: %s", login).isZero();
        return login.get("data").get("token").asText();
    }

    protected String randomName() {
        return "u" + System.nanoTime();
    }
}
