package com.zzy.techInterviewWeb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.techInterviewWeb.model.dto.question.QuestionQueryRequest;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionVO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Question service
 */
public interface QuestionService extends IService<Question> {

    /**
     * Validate data
     *
     * @param question The question object
     * @param add      Whether the validation is for new data
     */
    void validQuestion(Question question, boolean add);

    /**
     * Get query conditions
     *
     * @param questionQueryRequest The query request object
     * @return The query wrapper
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * Get question encapsulation
     *
     * @param question The question object
     * @param request  The request object
     * @return The question view object
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * Get paginated question encapsulation
     *
     * @param questionPage The question page object
     * @param request      The request object
     * @return The paginated question view object
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * Get paginated list of questions (Admin only)
     *
     * @param questionQueryRequest The request containing pagination details
     * @return A paginated response of questions
     */
    Page<Question> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest);


    /**
     * Query questions from ES (Elasticsearch)
     *
     * @param questionQueryRequest The query request object
     * @return A paginated response of questions
     */
    Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest);

    void batchDeleteQuestions(List<Long> questionIdList);


    /**


     * AI Generate Questions


     * @param questionType QuestionType


     * @param number Question，


     * @param user creater


     * @return ture / false


     */


    boolean aiGenerateQuestions(String questionType, int number, User user);
}
