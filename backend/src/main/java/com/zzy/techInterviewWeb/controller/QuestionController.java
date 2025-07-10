package com.zzy.techInterviewWeb.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.techInterviewWeb.annotation.AuthCheck;
import com.zzy.techInterviewWeb.common.BaseResponse;
import com.zzy.techInterviewWeb.common.DeleteRequest;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.common.ResultUtils;
import com.zzy.techInterviewWeb.constant.UserConstant;
import com.zzy.techInterviewWeb.exception.BusinessException;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.model.dto.question.QuestionAIGenerateRequest;
import com.zzy.techInterviewWeb.model.dto.question.QuestionAddRequest;
import com.zzy.techInterviewWeb.model.dto.question.QuestionEditRequest;
import com.zzy.techInterviewWeb.model.dto.question.QuestionQueryRequest;
import com.zzy.techInterviewWeb.model.dto.question.QuestionUpdateRequest;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionVO;
import com.zzy.techInterviewWeb.service.QuestionBankQuestionService;
import com.zzy.techInterviewWeb.service.QuestionService;
import com.zzy.techInterviewWeb.service.UserService;
import java.util.List;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * question controller
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Autowired(required = false)
    private QuestionService questionService;


    @Autowired(required = false)
    private UserService userService;
    @Autowired(required = false)
    private QuestionBankQuestionService questionBankQuestionService;

    // region CRUD

    /**
     * Add a new question
     *
     * @param questionAddRequest the request object containing question details
     * @param request            the HTTP servlet request
     * @return a response containing the ID of the newly created question
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // Convert DTO to Entity
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }

        // Validate data
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        // Save to database
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // Return new question ID
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * Delete a question
     *
     * @param deleteRequest the request containing the ID of the question to delete
     * @param request       the HTTP servlet request
     * @return a response indicating success or failure
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest,
            HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // Check if the question exists
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Allow only admin or creator to delete
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // Perform database operation
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Update a question (Admin only)
     *
     * @param questionUpdateRequest the request containing updated question details
     * @return a response indicating success or failure
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(
            @RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // Convert DTO to Entity
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }

        // Validate data
        questionService.validQuestion(question, false);
        // Check if the question exists
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Update database
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Get question details by ID (wrapped in a VO)
     *
     * @param id      the ID of the question
     * @param request the HTTP servlet request
     * @return a response containing the question VO
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // Query database
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // Return wrapped object
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * Get paginated list of questions (Admin only)
     *
     * @param questionQueryRequest the request containing pagination details
     * @return a paginated response of questions
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(questionService.listQuestionByPage(questionQueryRequest));

    }

    /**
     * Get paginated list of questions (wrapped in VO)
     *
     * @param questionQueryRequest the request containing pagination details
     * @param request              the HTTP servlet request
     * @return a paginated response of question VOs
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // Prevent scraping
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // Query database
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // Return wrapped response
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * Get paginated list of questions created by the logged-in user
     *
     * @param questionQueryRequest the request containing pagination details
     * @param request              the HTTP servlet request
     * @return a paginated response of question VOs
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // Add additional condition to query only the logged-in user's data
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // Prevent scraping
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // Query database
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // Return wrapped response
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * Edit a question (for user access)
     *
     * @param questionEditRequest the request containing updated question details
     * @param request             the HTTP servlet request
     * @return a response indicating success or failure
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest,
            HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // Convert DTO to Entity
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        // Validate data
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // Check if the question exists
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Allow editing by creator or admin
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // Update database
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long size = questionQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.searchFromEs(questionQueryRequest);
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * AI-generated questions (Admin only)
     *
     * @param questionAIGenerateRequest Request parameters
     * @param request HTTP request
     * @return Whether the generation was successful
     */
    @PostMapping("/ai/generate/question")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // Authorization check: must have admin role
    public BaseResponse<Boolean> aiGenerateQuestions(@RequestBody QuestionAIGenerateRequest questionAIGenerateRequest, HttpServletRequest request) {

        String questionType = questionAIGenerateRequest.getQuestionType(); // Get the question type
        int number = questionAIGenerateRequest.getNumber(); // Get the number of questions to generate

        // Validate parameters
        ThrowUtils.throwIf(StrUtil.isBlank(questionType), ErrorCode.PARAMS_ERROR, "Question type cannot be blank");
        ThrowUtils.throwIf(number <= 0, ErrorCode.PARAMS_ERROR, "Number of questions must be greater than 0");

        // Get current user
        User loginUser = userService.getLoginUser(request);

        // Generate questions using AI
        questionService.aiGenerateQuestions(questionType, number, loginUser);

        // Return result
        return ResultUtils.success(true);
    }


}

