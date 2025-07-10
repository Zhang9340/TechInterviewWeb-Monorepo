package com.zzy.techInterviewWeb.model.dto.question;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Add new Question Request
 */
@Data
public class QuestionAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
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
     * Recommended Answer
     */
    private String answer;
}