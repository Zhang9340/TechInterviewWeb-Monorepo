package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * create question Bank request
 *
 *
 *
 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {

    /**
     *  Question Id List
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
