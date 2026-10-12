package com.zhihu.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** 投票状态机：赞同/反对/取消/改投幂等，与冗余计数的联动。 */
class VoteFlowE2ETest extends E2eTestBase {

    long questionId;
    long answerId;
    String answerer;   // 回答作者（不能赞自己）
    String voter;      // 独立投票人

    @BeforeEach
    void setup() {
        answerer = newUser(randomName());
        String asker = newUser(randomName());
        questionId = post("/api/v1/questions", Map.of("title", "投票测试", "body", "b"), asker)
                .get("data").get("id").asLong();
        answerId = post("/api/v1/questions/" + questionId + "/answers",
                Map.of("body", "测试回答"), answerer).get("data").get("id").asLong();
        voter = newUser(randomName());
    }

    private JsonNode vote(String type, String token) {
        return put("/api/v1/answers/" + answerId + "/vote", Map.of("type", type), token);
    }

    @Test
    void vote_up_thenCountIncreases() {
        var res = vote("UP", voter);
        assertThat(res.get("code").asInt()).as("%s", res).isZero();
        assertThat(res.get("data").get("voteupCount").asInt()).isEqualTo(1);
    }

    @Test
    void vote_upAgain_isIdempotent() {
        vote("UP", voter);
        var res = vote("UP", voter);
        assertThat(res.get("code").asInt()).isZero();
        assertThat(res.get("data").get("voteupCount").asInt()).isEqualTo(1);
    }

    @Test
    void vote_switchToDown_decreasesCount() {
        vote("UP", voter);
        var res = vote("DOWN", voter);
        assertThat(res.get("code").asInt()).isZero();
        assertThat(res.get("data").get("voteupCount").asInt()).isEqualTo(0);
    }

    @Test
    void vote_cancel_afterUp_decreasesCount() {
        vote("UP", voter);
        var res = vote("NONE", voter);
        assertThat(res.get("code").asInt()).isZero();
        assertThat(res.get("data").get("voteupCount").asInt()).isEqualTo(0);
    }

    @Test
    void vote_ownAnswer_returns40001() {
        var res = vote("UP", answerer);
        assertThat(res.get("code").asInt()).isEqualTo(40001); // 互动域专属码（4xxxx），而非通用 10003
    }

    @Test
    void vote_withoutToken_returns10002() {
        var res = vote("UP", null);
        assertThat(res.get("code").asInt()).isEqualTo(10002);
    }

    @Test
    void vote_nonexistentAnswer_returns10004() {
        String token = newUser(randomName());
        var res = put("/api/v1/answers/999999999/vote", Map.of("type", "UP"), token);
        assertThat(res.get("code").asInt()).isEqualTo(10004);
    }

    @Test
    void answerList_reflectsVoteupCount() {
        vote("UP", voter);
        var list = get("/api/v1/questions/" + questionId + "/answers");
        assertThat(list.get("code").asInt()).isZero();
        assertThat(list.get("data").get(0).get("voteupCount").asInt()).isEqualTo(1);
    }
}
