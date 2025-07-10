package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import java.io.Serializable;
import lombok.Data;

/**
 * remove question bank question relation request
 */
@Data
public class QuestionBankQuestionRemoveRequest implements Serializable {
    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * quetion id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
