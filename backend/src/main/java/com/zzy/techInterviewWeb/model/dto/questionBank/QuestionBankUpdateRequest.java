package com.zzy.techInterviewWeb.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * update questionBank request for Admin
 *
 *
 *
 */
@Data
public class QuestionBankUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;


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