package com.zzy.techInterviewWeb.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.techInterviewWeb.common.ErrorCode;
import com.zzy.techInterviewWeb.constant.CommonConstant;
import com.zzy.techInterviewWeb.exception.ThrowUtils;
import com.zzy.techInterviewWeb.mapper.QuestionBankMapper;
import com.zzy.techInterviewWeb.model.dto.questionBank.QuestionBankQueryRequest;
import com.zzy.techInterviewWeb.model.entity.QuestionBank;
import com.zzy.techInterviewWeb.model.entity.User;
import com.zzy.techInterviewWeb.model.vo.QuestionBankVO;
import com.zzy.techInterviewWeb.model.vo.UserVO;
import com.zzy.techInterviewWeb.service.QuestionBankService;
import com.zzy.techInterviewWeb.service.UserService;
import com.zzy.techInterviewWeb.utils.SqlUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
 
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the QuestionBank service
 *
 */
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Autowired(required = false)
    private UserService userService;

    /**
     * Validates data.
     *
     * @param questionBank the QuestionBank entity
     * @param add          flag indicating whether this is a creation operation
     */
    @Override
    public void validQuestionBank(QuestionBank questionBank, boolean add) {
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
        // Retrieve values from the entity
        String title = questionBank.getTitle();

        // For creation, parameters cannot be null
        if (add) {
            // Additional validation rules
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }

        // For updates, validate fields if they are provided
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "Title is too long");
        }
    }

    /**
     * Constructs query conditions.
     *
     * @param questionBankQueryRequest the request containing query parameters
     * @return the query wrapper
     */
    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (questionBankQueryRequest == null) {
            return queryWrapper;
        }

        // Extract values from the request
        Long id = questionBankQueryRequest.getId();
        Long notId = questionBankQueryRequest.getNotId();
        String title = questionBankQueryRequest.getTitle();
        String searchText = questionBankQueryRequest.getSearchText();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();
        Long userId = questionBankQueryRequest.getUserId();
        String description  = questionBankQueryRequest.getDescription();
        String picture = questionBankQueryRequest.getPicture();

        // Add query conditions
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(ObjectUtils.isNotEmpty(description),"description",description);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(picture), "picture", picture);


        // Sorting
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    /**
     * Retrieves a wrapped version of a QuestionBank.
     *
     * @param questionBank the QuestionBank entity
     * @param request      the HTTP request
     * @return the QuestionBank VO
     */
    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request) {
        // Convert entity to VO
        QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);

        // Optional: Add additional fields to the VO
        Long userId = questionBank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankVO.setUser(userVO);

        return questionBankVO;
    }

    /**
     * Retrieves a paginated list of wrapped QuestionBanks.
     *
     * @param questionBankPage the page of QuestionBanks
     * @param request          the HTTP request
     * @return the paginated VO page
     */
    @Override
    public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request) {
        List<QuestionBank> questionBankList = questionBankPage.getRecords();
        Page<QuestionBankVO> questionBankVOPage = new Page<>(questionBankPage.getCurrent(), questionBankPage.getSize(), questionBankPage.getTotal());

        if (CollUtil.isEmpty(questionBankList)) {
            return questionBankVOPage;
        }

        // Convert list of entities to list of VOs
        List<QuestionBankVO> questionBankVOList = questionBankList.stream()
                .map(QuestionBankVO::objToVo)
                .collect(Collectors.toList());

        // Optional: Add additional fields to the VO
        Set<Long> userIdSet = questionBankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        questionBankVOList.forEach(questionBankVO -> {
            Long userId = questionBankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionBankVO.setUser(userService.getUserVO(user));
        });

        questionBankVOPage.setRecords(questionBankVOList);
        return questionBankVOPage;
    }
}
