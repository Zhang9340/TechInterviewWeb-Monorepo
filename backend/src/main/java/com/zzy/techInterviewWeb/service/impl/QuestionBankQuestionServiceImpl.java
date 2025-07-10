package com.zzy.techInterviewWeb.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.constant.CommonConstant;
import com.zzy.techInterviewWeb.exception.BusinessException;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.mapper.QuestionBankQuestionMapper;
import com.zzy.techInterviewWeb.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.QuestionBank;
import com.zzy.techInterviewWeb.model.entity.QuestionBankQuestion;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionBankQuestionVO;
import com.zzy.techInterviewWeb.model.vo.UserVO;
import com.zzy.techInterviewWeb.service.QuestionBankQuestionService;
import com.zzy.techInterviewWeb.service.QuestionBankService;
import com.zzy.techInterviewWeb.service.QuestionService;
import com.zzy.techInterviewWeb.service.UserService;
import com.zzy.techInterviewWeb.utils.SqlUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the QuestionBankQuestion service
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends
        ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements
        QuestionBankQuestionService {

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    @Lazy
    private QuestionService questionService;

    @Autowired(required = false)
    private QuestionBankService questionBankService;


    /**
     * Validates data.
     *
     * @param questionBankQuestion the QuestionBankQuestion entity
     * @param add                  flag indicating whether this is a creation operation
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "Question not Found");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR,
                    "Question bank not Found");
        }
    }


    /**
     * Constructs query conditions.
     *
     * @param questionBankQuestionQueryRequest the request containing query parameters
     * @return the query wrapper
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(
            QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }

        // Extract values from the request
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();

        // Add query conditions
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);

        // Sorting
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    /**
     * Retrieves a wrapped version of a QuestionBankQuestion.
     *
     * @param questionBankQuestion the QuestionBankQuestion entity
     * @param request              the HTTP request
     * @return the QuestionBankQuestion VO
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(
            QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // Convert entity to VO
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(
                questionBankQuestion);

        // Optional: Add additional fields to the VO
        Long userId = questionBankQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankQuestionVO.setUser(userVO);

        return questionBankQuestionVO;
    }

    /**
     * Retrieves a paginated list of wrapped QuestionBankQuestions.
     *
     * @param questionBankQuestionPage the page of QuestionBankQuestions
     * @param request                  the HTTP request
     * @return the paginated VO page
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(
            Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(
                questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(),
                questionBankQuestionPage.getTotal());

        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }

        // Convert list of entities to list of VOs
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream()
                .map(QuestionBankQuestionVO::objToVo)
                .collect(Collectors.toList());

        // Optional: Add additional fields to the VO
        Set<Long> userIdSet = questionBankQuestionList.stream().map(QuestionBankQuestion::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        questionBankQuestionVOList.forEach(questionBankQuestionVO -> {
            Long userId = questionBankQuestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankQuestionVO.setUser(userService.getUserVO(user));
        });

        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions) {
        try {
            boolean result = this.saveBatch(questionBankQuestions);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,
                    "Failed to add questions to the bank");
        } catch (DataIntegrityViolationException e) {
            log.error(
                    "Database unique key conflict or violation of other integrity constraints, error message: {}",
                    e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "The question already exists in the bank and cannot be added again");
        } catch (DataAccessException e) {
            log.error(
                    "Operation failed due to database connection issues, transaction problems, etc., error message: {}",
                    e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Database operation failed");
        } catch (Exception e) {
            // Catch other exceptions and perform general handling
            log.error(
                    "An unknown error occurred while adding questions to the bank, error message: {}",
                    e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR,
                    "Failed to add questions to the bank");
        }
    }


    @Override
    public void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId,
            User loginUser) {
        // Parameter validation
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR,
                "The question list is empty");
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR,
                "Invalid question bank");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // Check if question IDs exist
        List<Question> questionList = questionService.listByIds(questionIdList);
        // Valid question IDs
        List<Long> validQuestionIdList = questionList.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR,
                "The valid question list is empty");

        // Check which questions already exist in the question bank to avoid duplicate insertions
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(
                        QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, validQuestionIdList);
        List<QuestionBankQuestion> existQuestionList = this.list(lambdaQueryWrapper);

// IDs of questions that already exist in the question bank
        Set<Long> existQuestionIdSet = existQuestionList.stream()
                .map(QuestionBankQuestion::getId)
                .collect(Collectors.toSet());

// Filter out IDs of questions that already exist in the question bank to avoid adding them again
        validQuestionIdList = validQuestionIdList.stream()
                .filter(questionId -> !existQuestionIdSet.contains(questionId))
                .collect(Collectors.toList());

// Throw an exception if all questions already exist in the question bank
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR,
                "All questions already exist in the question bank");

        // Check if the question bank ID exists
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR,
                "The question bank does not exist");

        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                4,                         // Core thread count
                10,                        // Maximum thread count
                60L,                       // Thread idle time
                TimeUnit.SECONDS,          // Time unit for idle time
                new LinkedBlockingQueue<>(1000),  // Blocking queue capacity
                new ThreadPoolExecutor.CallerRunsPolicy()
                // Rejection policy: tasks are handled by the calling thread
        );

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // Process in batches to avoid long transactions, assuming 1000 records per batch
        int batchSize = 1000;
        int totalQuestionListSize = validQuestionIdList.size();
        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // Generate data for each batch
            List<Long> subList = validQuestionIdList.subList(i,
                    Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream().map(questionId -> {
                QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                questionBankQuestion.setQuestionBankId(questionBankId);
                questionBankQuestion.setQuestionId(questionId);
                questionBankQuestion.setUserId(loginUser.getId());
                return questionBankQuestion;
            }).collect(Collectors.toList());

            // Process each batch using transactions with thread pool
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
            },customExecutor);
            futures.add(future);
        }
        // wait for all the future finish
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        customExecutor.shutdown();

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId) {
        // Parameter validation
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR,
                "The question list is empty");
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR,
                "Invalid question bank");

        // Execute deletion of associations
        for (Long questionId : questionIdList) {
            // Construct query
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(
                            QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean result = this.remove(lambdaQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,
                        "Failed to remove question from the question bank");
            }
        }
    }


}
