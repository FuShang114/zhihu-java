package com.zhihu.e2e;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** 回答域：创建/列表（含作者与赞同数）/问题冗余计数同步。 */
class AnswerFlowE2ETest extends E2eTestBase {

    @Test
    void answer_flow_creates_lists_syncsCount() {
        String asker = newUser(randomName());
        long qid = post("/api/v1/questions",
                Map.of("title", "MySQL 冗余计数的坑？", "body", "如题"), asker)
                .get("data").get("id").asLong();

        String answerer = newUser(randomName());
        var created = post("/api/v1/questions/" + qid + "/answers",
                Map.of("body", "坑在于并发下读改写会丢更新。"), answerer);
        assertThat(created.get("code").asInt()).as("%s", created).isZero();

        var list = get("/api/v1/questions/" + qid + "/answers");
        assertThat(list.get("code").asInt()).isZero();
        assertThat(list.get("data").size()).isEqualTo(1);
        assertThat(list.get("data").get(0).get("body").asText()).contains("并发");
        assertThat(list.get("data").get(0).get("voteupCount").asInt()).isZero();
        assertThat(list.get("data").get(0).get("author").get("username").asText()).isNotBlank();

        // 冗余计数已同步（J4 会讲它的并发缺陷，这里先验证功能）
        var detail = get("/api/v1/questions/" + qid);
        assertThat(detail.get("data").get("answerCount").asInt()).isEqualTo(1);
    }

    @Test
    void answer_nonexistentQuestion_returns10004() {
        String token = newUser(randomName());
        var res = post("/api/v1/questions/999999999/answers", Map.of("body", "b"), token);
        assertThat(res.get("code").asInt()).isEqualTo(10004);
    }

    @Test
    void answer_withoutToken_returns10002() {
        String asker = newUser(randomName());
        long qid = post("/api/v1/questions", Map.of("title", "t", "body", "b"), asker)
                .get("data").get("id").asLong();
        var res = post("/api/v1/questions/" + qid + "/answers", Map.of("body", "b"), null);
        assertThat(res.get("code").asInt()).isEqualTo(10002);
    }
}
