package com.zhihu.e2e;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** 问题域：创建/详情（含作者）/分页列表/失败路径。 */
class QuestionFlowE2ETest extends E2eTestBase {

    @Test
    void create_and_getDetail_withAuthor() {
        String token = newUser(randomName());
        var created = post("/api/v1/questions",
                Map.of("title", "如何从零复刻知乎？", "body", "如题，求路线图。"), token);
        assertThat(created.get("code").asInt()).as("%s", created).isZero();
        long qid = created.get("data").get("id").asLong();

        var detail = get("/api/v1/questions/" + qid);
        assertThat(detail.get("code").asInt()).isZero();
        assertThat(detail.get("data").get("title").asText()).isEqualTo("如何从零复刻知乎？");
        assertThat(detail.get("data").get("author").get("username").asText()).isNotBlank();
        assertThat(detail.get("data").get("answerCount").asInt()).isZero();
    }

    @Test
    void list_paged_newestFirst() {
        String token = newUser(randomName());
        for (int i = 1; i <= 3; i++) {
            var res = post("/api/v1/questions",
                    Map.of("title", "问题" + i, "body", "内容" + i), token);
            assertThat(res.get("code").asInt()).as("%s", res).isZero();
        }
        var page0 = get("/api/v1/questions?page=0&size=2");
        assertThat(page0.get("code").asInt()).isZero();
        assertThat(page0.get("data").get("items").size()).isEqualTo(2);
        assertThat(page0.get("data").get("total").asLong()).isGreaterThanOrEqualTo(3);
        // 倒序：最新创建的"问题3"排第一
        assertThat(page0.get("data").get("items").get(0).get("title").asText()).isEqualTo("问题3");
    }

    @Test
    void get_nonexistent_returns10004() {
        var res = get("/api/v1/questions/999999999");
        assertThat(res.get("code").asInt()).isEqualTo(10004);
    }

    @Test
    void create_withoutToken_returns10002() {
        var res = post("/api/v1/questions", Map.of("title", "t", "body", "b"), null);
        assertThat(res.get("code").asInt()).isEqualTo(10002);
    }

    @Test
    void create_blankTitle_returns10001() {
        String token = newUser(randomName());
        var res = post("/api/v1/questions", Map.of("title", "", "body", "b"), token);
        assertThat(res.get("code").asInt()).isEqualTo(10001);
    }
}
