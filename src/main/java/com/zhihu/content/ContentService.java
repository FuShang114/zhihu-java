package com.zhihu.content;

import com.zhihu.common.BusinessException;
import com.zhihu.common.ErrorCode;
import com.zhihu.content.dto.AnswerCreateRequest;
import com.zhihu.content.dto.AnswerView;
import com.zhihu.content.dto.QuestionCreateRequest;
import com.zhihu.content.dto.QuestionPage;
import com.zhihu.content.dto.QuestionView;
import com.zhihu.user.User;
import com.zhihu.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContentService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public ContentService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                          UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QuestionView createQuestion(long userId, QuestionCreateRequest req) {
        Question q = new Question();
        q.setAuthorId(userId);
        q.setTitle(req.title());
        q.setBody(req.body());
        questionRepository.save(q);
        return toView(q);
    }

    @Transactional(readOnly = true)
    public QuestionView getQuestion(long id) {
        return toView(findAlive(id));
    }

    public QuestionPage listQuestions(int page, int size) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 50);
        Page<Question> p = questionRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<QuestionView> items = p.getContent().stream().map(this::toView).toList();
        return new QuestionPage(items, page, size, p.getTotalElements());
    }

    @Transactional
    public AnswerView createAnswer(long userId, long questionId, AnswerCreateRequest req) {
        Question q = findAlive(questionId);
        Answer a = new Answer();
        a.setQuestionId(questionId);
        a.setAuthorId(userId);
        a.setBody(req.body());
        answerRepository.save(a);

        // 冗余计数同步：实体读改写非原子，并发下会丢更新——J4 赞同计数与性能课的主线议题（故意保留的种子）
        q.setAnswerCount(q.getAnswerCount() + 1);
        questionRepository.save(q);

        return AnswerView.from(a, userRepository.findById(userId).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<AnswerView> listAnswers(long questionId) {
        findAlive(questionId);
        // 注意：逐条查作者是 N+1 查询——数据量小无感，这正是性能优化课要现场消灭的第一个靶子
        return answerRepository.findByQuestionIdOrderByIdDesc(questionId).stream()
                .map(a -> AnswerView.from(a, userRepository.findById(a.getAuthorId()).orElse(null)))
                .toList();
    }

    private Question findAlive(long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (q.getStatus() != Question.STATUS_NORMAL) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return q;
    }

    private QuestionView toView(Question q) {
        User author = userRepository.findById(q.getAuthorId()).orElse(null);
        return QuestionView.from(q, author);
    }
}
