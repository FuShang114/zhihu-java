package com.zhihu.e2e;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** M0 冒烟：服务能起、数据库能连、统一响应体契约生效。 */
class SmokeE2ETest extends E2eTestBase {

    @Test
    void ping_returnsUnifiedOk() {
        var res = get("/ping");
        assertThat(res.get("code").asInt()).isZero();
        assertThat(res.get("msg").asText()).isEqualTo("ok");
        assertThat(res.get("data").asText()).isEqualTo("pong");
    }
}
