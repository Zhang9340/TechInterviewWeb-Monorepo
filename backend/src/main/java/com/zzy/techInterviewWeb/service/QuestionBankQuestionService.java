package com.zzy.techInterviewWeb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.zzy.techInterviewWeb.model.entity.QuestionBankQuestion;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionBankQuestionVO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Question Bank Service
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * Validate data
     *
     * @param questionBankQuestion
     * @param add                  Whether to validate data for creation
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * Get query conditions
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(
            QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * Get encapsulated question bank
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion,
            HttpServletRequest request);

    /**
     * Get paginated encapsulated question bank
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(
            Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);


    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions);

    void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId, User loginUser);

    void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId);


}
