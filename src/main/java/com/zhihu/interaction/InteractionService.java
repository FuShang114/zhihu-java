package com.zhihu.interaction;

import com.zhihu.common.BusinessException;
import com.zhihu.common.ErrorCode;
import com.zhihu.content.Answer;
import com.zhihu.content.AnswerRepository;
import com.zhihu.content.Question;
import com.zhihu.content.QuestionRepository;
import com.zhihu.interaction.dto.FavoriteItemRequest;
import com.zhihu.interaction.dto.FolderCreateRequest;
import com.zhihu.interaction.dto.FolderView;
import com.zhihu.interaction.dto.ItemView;
import com.zhihu.interaction.dto.TargetSummary;
import com.zhihu.interaction.dto.VoteRequest;
import com.zhihu.interaction.dto.VoteView;
import com.zhihu.interaction.VoteType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InteractionService {

    private final VoteRepository voteRepository;
    private final FavoriteFolderRepository folderRepository;
    private final FavoriteItemRepository itemRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public InteractionService(VoteRepository voteRepository, FavoriteFolderRepository folderRepository,
                              FavoriteItemRepository itemRepository, AnswerRepository answerRepository,
                              QuestionRepository questionRepository) {
        this.voteRepository = voteRepository;
        this.folderRepository = folderRepository;
        this.itemRepository = itemRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    // ---------- 投票 ----------

    @Transactional
    public VoteView vote(long userId, long answerId, VoteRequest req) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (answer.getStatus() != Answer.STATUS_NORMAL) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (req.type() != VoteType.NONE && answer.getAuthorId().equals(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_VOTE_OWN);
        }

        Vote existing = voteRepository
                .findByUserIdAndTargetTypeAndTargetId(userId, "ANSWER", answerId)
                .orElse(null);
        int count = answer.getVoteupCount();

        // 状态机：null→UP(+1)/DOWN(不变)；同向=幂等；改投/取消按旧方向修正计数
        if (existing == null) {
            if (req.type() == VoteType.NONE) {
                return new VoteView(count);
            }
            Vote v = new Vote();
            v.setUserId(userId);
            v.setTargetType("ANSWER");
            v.setTargetId(answerId);
            v.setType(req.type().value());
            voteRepository.save(v);
            if (req.type() == VoteType.UP) {
                count++;
                answer.setVoteupCount(count);
                answerRepository.save(answer);
            }
            return new VoteView(count);
        }

        if (existing.getType() == req.type().value()) {
            return new VoteView(count); // 幂等
        }

        int oldType = existing.getType(); // 先留底再动手——否则减数判断读到的是改过的新值（E2E 抓到的真 bug）
        if (req.type() == VoteType.NONE) {
            voteRepository.delete(existing);
        } else {
            existing.setType(req.type().value());
            voteRepository.save(existing);
        }
        if (oldType == VoteType.UP.value()) {
            count--;
        }
        if (req.type() == VoteType.UP) {
            count++;
        }
        if (count != answer.getVoteupCount()) {
            answer.setVoteupCount(count);
            answerRepository.save(answer);
        }
        return new VoteView(count);
    }

    // ---------- 收藏 ----------

    @Transactional
    public FolderView createFolder(long userId, FolderCreateRequest req) {
        FavoriteFolder folder = new FavoriteFolder();
        folder.setUserId(userId);
        folder.setTitle(req.title());
        folder.setIsPublic(req.isPublic());
        folderRepository.save(folder);
        return FolderView.from(folder);
    }

    @Transactional(readOnly = true)
    public List<FolderView> listFolders(long userId) {
        return folderRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(FolderView::from)
                .toList();
    }

    @Transactional
    public ItemView addItem(long userId, long folderId, FavoriteItemRequest req) {
        FavoriteFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!folder.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        TargetType targetType = TargetType.parse(req.targetType());
        if (!targetExists(targetType, req.targetId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (itemRepository.existsByFolderIdAndTargetTypeAndTargetId(folderId, targetType.name(), req.targetId())) {
            throw new BusinessException(ErrorCode.ALREADY_FAVORITED);
        }
        FavoriteItem item = new FavoriteItem();
        item.setFolderId(folderId);
        item.setTargetType(targetType.name());
        item.setTargetId(req.targetId());
        itemRepository.save(item);
        return new ItemView(item.getId(), targetType.name(), req.targetId(),
                summary(targetType, req.targetId()));
    }

    @Transactional(readOnly = true)
    public List<ItemView> listItems(long userId, long folderId) {
        FavoriteFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!folder.getIsPublic() && !folder.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN); // 私密夹仅主人可见
        }
        return itemRepository.findByFolderIdOrderByIdDesc(folderId).stream()
                .map(i -> new ItemView(i.getId(), i.getTargetType(), i.getTargetId(),
                        summary(TargetType.parse(i.getTargetType()), i.getTargetId())))
                .toList();
    }

    private boolean targetExists(TargetType type, long id) {
        return switch (type) {
            case ANSWER -> answerRepository.findById(id)
                    .map(a -> a.getStatus() == Answer.STATUS_NORMAL).orElse(false);
            case QUESTION -> questionRepository.findById(id)
                    .map(q -> q.getStatus() == Question.STATUS_NORMAL).orElse(false);
        };
    }

    private TargetSummary summary(TargetType type, long id) {
        return switch (type) {
            case ANSWER -> answerRepository.findById(id)
                    .map(a -> new TargetSummary(null, excerpt(a.getBody())))
                    .orElse(new TargetSummary(null, "内容已失效"));
            case QUESTION -> questionRepository.findById(id)
                    .map(q -> new TargetSummary(q.getTitle(), null))
                    .orElse(new TargetSummary("内容已失效", null));
        };
    }

    private String excerpt(String body) {
        return body.length() <= 50 ? body : body.substring(0, 50);
    }
}
