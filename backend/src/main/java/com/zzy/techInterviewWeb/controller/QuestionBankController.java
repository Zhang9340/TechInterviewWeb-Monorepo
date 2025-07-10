package com.zzy.techInterviewWeb.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.techInterviewWeb.annotation.AuthCheck;
import com.zzy.techInterviewWeb.common.BaseResponse;
import com.zzy.techInterviewWeb.common.DeleteRequest;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.common.ResultUtils;
import com.zzy.techInterviewWeb.constant.UserConstant;
import com.zzy.techInterviewWeb.exception.BusinessException;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.model.dto.question.QuestionQueryRequest;
import com.zzy.techInterviewWeb.model.dto.questionBank.QuestionBankAddRequest;
import com.zzy.techInterviewWeb.model.dto.questionBank.QuestionBankEditRequest;
import com.zzy.techInterviewWeb.model.dto.questionBank.QuestionBankQueryRequest;
import com.zzy.techInterviewWeb.model.dto.questionBank.QuestionBankUpdateRequest;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.QuestionBank;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionBankVO;
import com.zzy.techInterviewWeb.model.vo.QuestionVO;
import com.zzy.techInterviewWeb.service.QuestionBankService;
import com.zzy.techInterviewWeb.service.QuestionService;
import com.zzy.techInterviewWeb.service.UserService;

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
 * questionBank controller
 */
@RestController
@RequestMapping("/questionBank")
@Slf4j
public class QuestionBankController {

    @Autowired(required = false)
    private QuestionBankService questionBankService;

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private QuestionService questionService;

    // region CRUD operations

    /**
     * Create a question bank
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBank(
            @RequestBody QuestionBankAddRequest questionBankAddRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
        // Convert DTO to entity
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankAddRequest, questionBank);
        // Validate data
        questionBankService.validQuestionBank(questionBank, true);
        // Populate default values
        User loginUser = userService.getLoginUser(request);
        questionBank.setUserId(loginUser.getId());
        // Save to the database
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // Return the ID of the newly created record
        long newQuestionBankId = questionBank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * Delete a question bank
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest,
            HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // Check existence
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // Only the creator or an admin can delete
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // Perform database operation
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Update a question bank (Admin only)
     *
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(
            @RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // Convert DTO to entity
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankUpdateRequest, questionBank);
        // Validate data
        questionBankService.validQuestionBank(questionBank, false);
        // Check existence
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // Perform database operation
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Get a question bank by ID (wrapped object)
     *
     * @param quesitonBankQueryRequest
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(
            QuestionBankQueryRequest quesitonBankQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(quesitonBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = quesitonBankQueryRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // Query the database
        QuestionBank questionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
        QuestionBankVO questionBankVO = questionBankService.getQuestionBankVO(questionBank,
                request);

        // if need question list under this question bank
        boolean needQueryQuestionList = quesitonBankQueryRequest.isNeedQueryQuestionList();
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            questionQueryRequest.setPageSize(quesitonBankQueryRequest.getPageSize());
            questionQueryRequest.setCurrent(quesitonBankQueryRequest.getCurrent());
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage,request);
            questionBankVO.setQuestionPage(questionVOPage);
        }

        // Return wrapped object
        return ResultUtils.success(questionBankVO);
    }

    /**
     * Get a paginated list of question banks (Admin only)
     *
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // Query the database
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        return ResultUtils.success(questionBankPage);
    }

    /**
     * Get a paginated list of question banks (wrapped objects)
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request) {
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // Limit large queries
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        // Query the database
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));

        // Return wrapped objects
        return ResultUtils.success(
                questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    /**
     * Get a paginated list of question banks created by the logged-in user
     *
     * @param questionBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // Add filter to query only the data created by the current logged-in user
        User loginUser = userService.getLoginUser(request);
        questionBankQueryRequest.setUserId(loginUser.getId());
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // Limit large queries
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // Query the database
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        // Return wrapped objects
        return ResultUtils.success(
                questionBankService.getQuestionBankVOPage(questionBankPage, request));
    }

    /**
     * Edit a question bank (for users)
     *
     * @param questionBankEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editQuestionBank(
            @RequestBody QuestionBankEditRequest questionBankEditRequest,
            HttpServletRequest request) {
        if (questionBankEditRequest == null || questionBankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // Convert DTO to entity
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankEditRequest, questionBank);
        // Validate data
        questionBankService.validQuestionBank(questionBank, false);
        User loginUser = userService.getLoginUser(request);
        // Check existence
        long id = questionBankEditRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // Only the creator or an admin can edit
        if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(
                loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // Perform database operation
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
