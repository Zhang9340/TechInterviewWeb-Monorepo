package com.zzy.techInterviewWeb.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.constant.CommonConstant;
import com.zzy.techInterviewWeb.exception.BusinessException;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.manager.AIManager;
import com.zzy.techInterviewWeb.mapper.QuestionMapper;
import com.zzy.techInterviewWeb.model.dto.question.QuestionEsDTO;
import com.zzy.techInterviewWeb.model.dto.question.QuestionQueryRequest;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.QuestionBankQuestion;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionVO;
import com.zzy.techInterviewWeb.model.vo.UserVO;
import com.zzy.techInterviewWeb.service.QuestionBankQuestionService;
import com.zzy.techInterviewWeb.service.QuestionService;
import com.zzy.techInterviewWeb.service.UserService;
import com.zzy.techInterviewWeb.utils.SqlUtils;
import java.util.Arrays;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Implementation of the Question service
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements
        QuestionService {

    @Autowired(required = false)
    private UserService userService;
    @Autowired(required = false)
    private QuestionBankQuestionService questionBankQuestionService;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired(required = false)
    private AIManager aiManager;

    /**
     * Validates data.
     *
     * @param question the question entity
     * @param add      flag indicating whether this is a creation operation
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // Retrieve values from the entity
        String title = question.getTitle();
        String content = question.getContent();

        // For creation, parameters cannot be null
        if (add) {
            // Additional validation rules
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }

        // For updates, validate fields if they are provided
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "Title is too long");
        }

        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 1024, ErrorCode.PARAMS_ERROR,
                    "Content is too long");
        }
    }

    /**
     * Constructs query conditions.
     *
     * @param questionQueryRequest the request containing query parameters
     * @return the query wrapper
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        // Extract values from the request
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        List<String> tagList = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String answer = questionQueryRequest.getAnswer();

        // Add query conditions
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // fuzzy search
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);

        // JSON array search
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        //
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // Sorting
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    /**
     * Retrieves a wrapped version of a question.
     *
     * @param question the question entity
     * @param request  the HTTP request
     * @return the question VO
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // Optional: Add additional fields to the VO
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);

        return questionVO;
    }

    /**
     * Retrieves a paginated list of wrapped questions.
     *
     * @param questionPage the page of questions
     * @param request      the HTTP request
     * @return the paginated VO page
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage,
            HttpServletRequest request) {

        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(),
                questionPage.getSize(), questionPage.getTotal());

        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }

        // Convert list of entities to list of VOs
        List<QuestionVO> questionVOList = questionList.stream()
                .map(QuestionVO::objToVo)
                .collect(Collectors.toList());

        // Optional: Add additional fields to the VO
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
        });

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * Get paginated list of questions (Admin only)
     *
     * @param questionQueryRequest the request containing pagination details
     * @return a paginated response of questions
     */

    public Page<Question> listQuestionByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        QueryWrapper<Question> queryWrapper = this.getQueryWrapper(questionQueryRequest);
        //get the question by question bank id
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (questionBankId != null) {
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(
                            QuestionBankQuestion.class).select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> questionList =
                    questionBankQuestionService.list(lambdaQueryWrapper);

            if (CollUtil.isNotEmpty(questionList)) {
                Set<Long> questionIdList = questionList.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
                queryWrapper.in("id", questionIdList);
            } else {
                //if bank is empty return empty list
                return new Page<>(current, size, 0);
            }

        }
        // Query database
        Page<Question> questionPage = this.page(new Page<>(current, size),
                queryWrapper);
        return questionPage;
    }

    @Override

    public Page<Question> searchFromEs(QuestionQueryRequest request) {
        int current = request.getCurrent() - 1;
        int pageSize = request.getPageSize();
        PageRequest pageRequest = PageRequest.of(current, pageSize);

        // Build ES 8.x style bool query using lambda builder
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.filter(fb -> fb.term(t -> t.field("isDelete").value("0")));

                    if (request.getId() != null) {
                        b.filter(fb -> fb.term(t -> t.field("id").value(request.getId())));
                    }
                    if (request.getNotId() != null) {
                        b.mustNot(mn -> mn.term(t -> t.field("id").value(request.getNotId())));
                    }
                    if (request.getUserId() != null) {
                        b.filter(fb -> fb.term(t -> t.field("userId").value(request.getUserId())));
                    }
                    if (request.getQuestionBankId() != null) {
                        b.filter(fb -> fb.term(
                                t -> t.field("questionBankId").value(request.getQuestionBankId())));
                    }
                    if (CollUtil.isNotEmpty(request.getTags())) {
                        for (String tag : request.getTags()) {
                            b.filter(fb -> fb.term(t -> t.field("tags").value(tag)));
                        }
                    }
                    if (StringUtils.isNotBlank(request.getSearchText())) {
                        b.should(sh -> sh.match(
                                m -> m.field("title").query(request.getSearchText())));
                        b.should(sh -> sh.match(
                                m -> m.field("content").query(request.getSearchText())));
                        b.should(sh -> sh.match(
                                m -> m.field("answer").query(request.getSearchText())));
                        b.minimumShouldMatch("1");
                    }
                    return b;
                }))
                .withPageable(pageRequest)
                .withSort(s -> {
                    if (StringUtils.isNotBlank(request.getSortField())) {
                        return s.field(f -> f
                                .field(request.getSortField())
                                .order(CommonConstant.SORT_ORDER_ASC.equals(request.getSortOrder())
                                        ? SortOrder.Asc : SortOrder.Desc));
                    } else {
                        return s.score(_s -> _s); // Default to score sort
                    }
                })
                .build();

        SearchHits<QuestionEsDTO> hits = elasticsearchOperations.search(query, QuestionEsDTO.class);

        Page<Question> page = new Page<>();
        page.setTotal(hits.getTotalHits());

        List<Question> resultList = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(QuestionEsDTO::dtoToObj)
                .collect(Collectors.toList());

        page.setRecords(resultList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteQuestions(List<Long> questionIdList) {
        // Parameter validation
        if (CollUtil.isEmpty(questionIdList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    "The list of questions to delete is empty");
        }
        // Iterate through the list of question IDs
        for (Long questionId : questionIdList) {
            // Delete the question by its ID
            boolean result = this.removeById(questionId);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Failed to delete question");
            }
            // Remove the association between the question and the question bank
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(
                            QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId);

            result = questionBankQuestionService.remove(lambdaQueryWrapper);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,
                        "Failed to delete the association between the question and the question bank");
            }
        }
    }

    @Override
    public boolean aiGenerateQuestions(String questionType, int number, User user) {
        if (ObjectUtil.hasEmpty(questionType, number, user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parameter Error");
        }

        // 1. Define system prompt

        String systemPrompt =
                "You are a professional programming interview coach. I need you to generate {number} interview questions about {topic}. The output format should be as follows:\n"
                        +
                        "\n" +
                        "1. What is reflection in Java?\n" +
                        "2. What is the purpose of the Stream API in Java 8?\n" +
                        "3. xxxxxx\n" +
                        "\n" +
                        "Do not output any additional content—no introductions, no conclusions. Only output the list above.\n"
                        +
                        "\n" +
                        "I will now provide you with the number of questions ({number}) and the topic ({topic}).\n";

        // 2. Concatenate user prompt

        String userPrompt = String.format("Number of questions: %s, Topic: %s", number,
                questionType);

        String answer = aiManager.ask(systemPrompt, userPrompt);

        List<String> lines = Arrays.asList(answer.split("\n"));

        List<String> questionList = lines.stream()
                .map(line -> StrUtil.removePrefix(line, StrUtil.subBefore(line, " ", false)))
                .toList();

        System.out.println(questionList.toString());
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                4,                         // Core thread count
                10,                        // Maximum thread count
                60L,                       // Thread idle time
                TimeUnit.SECONDS,          // Time unit for idle time
                new LinkedBlockingQueue<>(1000),  // Blocking queue capacity
                new ThreadPoolExecutor.CallerRunsPolicy()
                // Rejection policy: tasks are handled by the calling thread
        );
        List<CompletableFuture<Question>> futureList = questionList.stream()
                .map(q -> CompletableFuture.supplyAsync(() -> {
                    Question question = new Question();
                    question.setTitle(q);
                    question.setUserId(Integer.toUnsignedLong(123));
                    question.setTags("[\"Pending review\"]");
                    question.setAnswer(aiGenerateQuestionAnswer(q)); // Runs in thread pool
                    return question;
                }, customExecutor))
                .toList();
        List<Question> questionListTOSave = futureList.stream()
                .map(CompletableFuture::join)
                .toList();
        boolean result = this.saveBatch(questionListTOSave);
        if (!result) {


            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Fail to save the Quesitons");


        }

        customExecutor.shutdown();
        return true;
    }

    private String aiGenerateQuestionAnswer(String questionDescription) {
        // 1. Define the system prompt
        String systemPrompt =
                "You are a professional programming interview examiner. I will give you an interview question, and please help me generate a detailed solution. The requirements are as follows:\n"
                        +
                        "\n" +
                        "1. The explanation should be natural and fluent.\n" +
                        "2. You can first give a summary answer, then explain in detail.\n" +
                        "3. Use Markdown syntax for output.\n" +
                        "\n" +
                        "Apart from this, please do not output any extra content. Do not include an introduction or a conclusion — only output the solution.\n"
                        +
                        "\n" +
                        "I will now provide the interview question.";

// 2. Construct the user prompt
        String userPrompt = String.format("Interview Question: %s", questionDescription);

// 3. Call the AI to generate the solution
        return aiManager.ask(systemPrompt, userPrompt);


    }


}
