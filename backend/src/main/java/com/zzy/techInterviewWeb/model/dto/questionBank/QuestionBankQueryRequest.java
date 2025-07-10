package com.zzy.techInterviewWeb.model.dto.questionBank;

import com.zzy.techInterviewWeb.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询questionBank请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * id
     */
    private Long notId;
    /**
     * search text
     */
    private String searchText;
    /**
     * title
     */
    private String title;
    /**
     * Description
     */
    private String description;
    /**
     * Picture
     */
    private String picture;
    /**
     * creator id
     */
    private Long userId;
    /**
     * need for the quesionlist when do the query
     */
    private boolean needQueryQuestionList;
}