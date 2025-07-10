package com.zzy.techInterviewWeb.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *  create questionBank request
 *
 *
 *
 */
@Data
public class QuestionBankAddRequest implements Serializable {

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


    private static final long serialVersionUID = 1L;
}