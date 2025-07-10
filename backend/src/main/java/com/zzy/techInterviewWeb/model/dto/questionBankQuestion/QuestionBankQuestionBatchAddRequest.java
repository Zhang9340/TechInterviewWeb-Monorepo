package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * create question Bank Batch request
 *
 *
 *
 */
@Data
public class QuestionBankQuestionBatchAddRequest implements Serializable {


    /**
     * Question Bank ID
     */
    private Long questionBankId;

    /**
     * Question ID List
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}