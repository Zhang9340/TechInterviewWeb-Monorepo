package com.zzy.techInterviewWeb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.techInterviewWeb.annotation.AuthCheck;
import com.zzy.techInterviewWeb.common.BaseResponse;
import com.zzy.techInterviewWeb.common.DeleteRequest;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.common.ResultUtils;
import com.zzy.techInterviewWeb.constant.UserConstant;
import com.zzy.techInterviewWeb.exception.BusinessException;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionBatchAddRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionBatchRemoveRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionRemoveRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionUpdateRequest;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBatchDeleteRequest;
import com.zzy.techInterviewWeb.model.entity.QuestionBankQuestion;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionBankQuestionVO;
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
 * Question Bank API
 */
@RestController
@RequestMapping("/questionBankQuestion")
@Slf4j
public class QuestionBankQuestionController {

    @Autowired(required = false)
    private QuestionBankQuestionService questionBankQuestionService;

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private QuestionService questionService;

    // region CRUD Operations

    /**
     * Create Question Bank
     *
     * @param questionBankQuestionAddRequest Request object for adding a question bank
     * @param request                        HTTP request
     * @return Response containing the ID of the newly created question bank
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBankQuestion(
            @RequestBody QuestionBankQuestionAddRequest questionBankQuestionAddRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQuestionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // TODO: Convert entity and DTO here
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestionAddRequest, questionBankQuestion);
        // Data validation
        questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, true);
        // TODO: Populate default values
        User loginUser = userService.getLoginUser(request);
        questionBankQuestion.setUserId(loginUser.getId());
        // Save to database
        boolean result = questionBankQuestionService.save(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // Return the ID of the newly created entry
        long newQuestionBankQuestionId = questionBankQuestion.getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
     * Delete Question Bank
     *
     * @param deleteRequest Request object for deletion
     * @param request       HTTP request
     * @return Response indicating whether the deletion was successful
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(
            @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // Check if the entry exists
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Allow deletion only by the owner or an admin
        if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(
                request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // Perform database operation
        boolean result = questionBankQuestionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Update Question Bank (Admin Only)
     *
     * @param questionBankQuestionUpdateRequest Request object for updating the question bank
     * @return Response indicating whether the update was successful
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBankQuestion(
            @RequestBody QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest) {
        if (questionBankQuestionUpdateRequest == null
                || questionBankQuestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO: Convert entity and DTO here
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestionUpdateRequest, questionBankQuestion);
        // Data validation
        questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, false);
        // Check if the entry exists
        long id = questionBankQuestionUpdateRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Perform database operation
        boolean result = questionBankQuestionService.updateById(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Get Question Bank by ID (VO Wrapper)
     *
     * @param id      ID of the question bank
     * @param request HTTP request
     * @return Response containing the VO representation of the question bank
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id,
            HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // Query the database
        QuestionBankQuestion questionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // Get the VO wrapper
        return ResultUtils.success(
                questionBankQuestionService.getQuestionBankQuestionVO(questionBankQuestion,
                        request));
    }

    /**
     * Paginated List of Question Banks (Admin Only)
     *
     * @param questionBankQuestionQueryRequest Request object for querying question banks
     * @return Paginated list of question banks
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(
            @RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        long current = questionBankQuestionQueryRequest.getCurrent();
        long size = questionBankQuestionQueryRequest.getPageSize();
        // Query the database
        Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(
                new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
        return ResultUtils.success(questionBankQuestionPage);
    }

    /**
     * Paginated List of Question Banks (VO Wrapper)
     *
     * @param questionBankQuestionQueryRequest Request object for querying question banks
     * @param request                          HTTP request
     * @return Paginated list of question banks with VO wrappers
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(
            @RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
            HttpServletRequest request) {
        long current = questionBankQuestionQueryRequest.getCurrent();
        long size = questionBankQuestionQueryRequest.getPageSize();
        // Limit crawling
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // Query the database
        Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(
                new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
        // Get VO wrappers
        return ResultUtils.success(
                questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage,
                        request));
    }

    /**
     * Paginated List of Question Banks Created by the Current User
     *
     * @param questionBankQuestionQueryRequest Request object for querying question banks
     * @param request                          HTTP request
     * @return Paginated list of question banks created by the logged-in user with VO wrappers
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(
            @RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQuestionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // Supplement query conditions to only fetch data for the current logged-in user
        User loginUser = userService.getLoginUser(request);
        questionBankQuestionQueryRequest.setUserId(loginUser.getId());
        long current = questionBankQuestionQueryRequest.getCurrent();
        long size = questionBankQuestionQueryRequest.getPageSize();
        // Limit crawling
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // Query the database
        Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(
                new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
        // Get VO wrappers
        return ResultUtils.success(
                questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage,
                        request));
    }


    /**
     * edit question and question bank relation
     *
     * @param questionBankQuestionRemoveRequest
     * @return Paginated list of question banks created by the logged-in user with VO wrappers
     */
    @PostMapping("/remove")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeQuestionBankQuestion(
            @RequestBody QuestionBankQuestionRemoveRequest questionBankQuestionRemoveRequest
    ) {
        // validation for param
        ThrowUtils.throwIf(questionBankQuestionRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestionRemoveRequest.getQuestionBankId();
        Long questionId = questionBankQuestionRemoveRequest.getQuestionId();
        ThrowUtils.throwIf(questionBankId == null || questionId == null, ErrorCode.PARAMS_ERROR);
        //
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(
                        QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionId, questionId)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
        boolean result = questionBankQuestionService.remove(lambdaQueryWrapper);
        return ResultUtils.success(result);
    }

    // endregion


    @PostMapping("/delete/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchDeleteQuestions(
            @RequestBody QuestionBatchDeleteRequest questionBatchDeleteRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionBatchDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        questionService.batchDeleteQuestions(questionBatchDeleteRequest.getQuestionIdList());
        return ResultUtils.success(true);
    }


    @PostMapping("/add/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchAddQuestionsToBank(
            @RequestBody QuestionBankQuestionBatchAddRequest questionBankQuestionBatchAddRequest,
            HttpServletRequest request
    ) {

        ThrowUtils.throwIf(questionBankQuestionBatchAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long questionBankId = questionBankQuestionBatchAddRequest.getQuestionBankId();
        List<Long> questionIdList = questionBankQuestionBatchAddRequest.getQuestionIdList();
        questionBankQuestionService.batchAddQuestionsToBank(questionIdList, questionBankId,
                loginUser);
        return ResultUtils.success(true);
    }

    @PostMapping("/remove/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchRemoveQuestionsFromBank(
            @RequestBody QuestionBankQuestionBatchRemoveRequest questionBankQuestionBatchRemoveRequest,
            HttpServletRequest request
    ) {

        ThrowUtils.throwIf(questionBankQuestionBatchRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestionBatchRemoveRequest.getQuestionBankId();
        List<Long> questionIdList = questionBankQuestionBatchRemoveRequest.getQuestionIdList();
        questionBankQuestionService.batchRemoveQuestionsFromBank(questionIdList, questionBankId);
        return ResultUtils.success(true);
    }


}

