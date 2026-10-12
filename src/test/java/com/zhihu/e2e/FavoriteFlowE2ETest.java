package com.zhihu.e2e;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** 收藏域：收藏夹创建/列表、收藏条目（多态目标）、重复收藏、私密夹权限。 */
class FavoriteFlowE2ETest extends E2eTestBase {

    @Test
    void createFolder_and_listMine() {
        String token = newUser(randomName());
        var created = post("/api/v1/favorite-folders",
                Map.of("title", "默认收藏夹", "isPublic", true), token);
        assertThat(created.get("code").asInt()).as("%s", created).isZero();
        assertThat(created.get("data").get("title").asText()).isEqualTo("默认收藏夹");

        var list = get("/api/v1/favorite-folders", token);
        assertThat(list.get("code").asInt()).isZero();
        assertThat(list.get("data").size()).isEqualTo(1);
    }

    @Test
    void favoriteAnswer_flow_withTargetSummary() {
        String owner = newUser(randomName());
        long fid = post("/api/v1/favorite-folders",
                Map.of("title", "技术好文", "isPublic", true), owner)
                .get("data").get("id").asLong();

        String asker = newUser(randomName());
        long qid = post("/api/v1/questions", Map.of("title", "收藏目标问题", "body", "b"), asker)
                .get("data").get("id").asLong();
        String answerer = newUser(randomName());
        long aid = post("/api/v1/questions/" + qid + "/answers",
                Map.of("body", "值得收藏的答案内容"), answerer).get("data").get("id").asLong();

        var added = post("/api/v1/favorite-folders/" + fid + "/items",
                Map.of("targetType", "ANSWER", "targetId", aid), owner);
        assertThat(added.get("code").asInt()).as("%s", added).isZero();

        var items = get("/api/v1/favorite-folders/" + fid + "/items", owner);
        assertThat(items.get("code").asInt()).isZero();
        assertThat(items.get("data").size()).isEqualTo(1);
        assertThat(items.get("data").get(0).get("targetId").asLong()).isEqualTo(aid);
        assertThat(items.get("data").get(0).get("summary").get("body").asText()).contains("值得收藏");
    }

    @Test
    void favorite_duplicate_returns40009() {
        String owner = newUser(randomName());
        long fid = post("/api/v1/favorite-folders",
                Map.of("title", "夹", "isPublic", true), owner).get("data").get("id").asLong();
        String asker = newUser(randomName());
        long qid = post("/api/v1/questions", Map.of("title", "t", "body", "b"), asker)
                .get("data").get("id").asLong();

        post("/api/v1/favorite-folders/" + fid + "/items",
                Map.of("targetType", "QUESTION", "targetId", qid), owner);
        var again = post("/api/v1/favorite-folders/" + fid + "/items",
                Map.of("targetType", "QUESTION", "targetId", qid), owner);
        assertThat(again.get("code").asInt()).isEqualTo(40009);
    }

    @Test
    void favorite_privateFolder_hiddenFromOthers() {
        String owner = newUser(randomName());
        long fid = post("/api/v1/favorite-folders",
                Map.of("title", "私密夹", "isPublic", false), owner).get("data").get("id").asLong();

        String stranger = newUser(randomName());
        var res = get("/api/v1/favorite-folders/" + fid + "/items", stranger);
        assertThat(res.get("code").asInt()).isEqualTo(10003);

        // 主人自己可见
        var own = get("/api/v1/favorite-folders/" + fid + "/items", owner);
        assertThat(own.get("code").asInt()).isZero();
    }

    @Test
    void favorite_toNonexistentFolder_returns10004() {
        String token = newUser(randomName());
        var res = post("/api/v1/favorite-folders/999999999/items",
                Map.of("targetType", "QUESTION", "targetId", 1), token);
        assertThat(res.get("code").asInt()).isEqualTo(10004);
    }

    @Test
    void favorite_unknownTargetType_returns10001() {
        String token = newUser(randomName());
        long fid = post("/api/v1/favorite-folders",
                Map.of("title", "夹", "isPublic", true), token).get("data").get("id").asLong();
        var res = post("/api/v1/favorite-folders/" + fid + "/items",
                Map.of("targetType", "VIDEO", "targetId", 1), token);
        assertThat(res.get("code").asInt()).isEqualTo(10001);
    }
}
