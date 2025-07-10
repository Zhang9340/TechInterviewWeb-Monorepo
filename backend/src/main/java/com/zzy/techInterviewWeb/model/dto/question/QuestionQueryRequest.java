package com.zzy.techInterviewWeb.model.dto.question;

import com.zzy.techInterviewWeb.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询question请求
 *
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

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
     * content
     */
    private String content;

    /**
     * tag list
     */
    private List<String> tags;

    /**
     * user id
     */
    private Long userId;

    /**
     * Recommended Answer
     */
    private String answer;

    /**
     * questionBank Id
     */
    private Long questionBankId;

    private static final long serialVersionUID = 1L;
}