package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * remove question bank question relation request
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {
    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * quetion id
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
