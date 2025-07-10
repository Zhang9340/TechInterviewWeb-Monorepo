package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import com.zzy.techInterviewWeb.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * query question Bank request
 *
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * Question Bank ID
     */
    private Long questionBankId;

    /**
     * Question ID
     */
    private Long questionId;

    private Long userId;

    private static final long serialVersionUID = 1L;
}