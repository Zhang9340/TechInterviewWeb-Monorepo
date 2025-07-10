package com.zzy.techInterviewWeb.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Edit question request for user
 *
 *
 *
 */
@Data
public class QuestionEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * content
     */
    private String content;

    /**
     *
     */
    private List<String> tags;

    /**
     * Recommended Answer
     */
    private String answer;


    private static final long serialVersionUID = 1L;
}