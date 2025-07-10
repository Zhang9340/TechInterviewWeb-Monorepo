package com.zzy.techInterviewWeb.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * update question request for admin
 *
 *
 *
 */
@Data
public class QuestionUpdateRequest implements Serializable {

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