package com.zzy.techInterviewWeb.model.dto.questionBankQuestion;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * create question Bank request
 *
 *
 *
 */
@Data
public class QuestionBankQuestionAddRequest implements Serializable {


    /**
     * Question Bank ID
     */
    private Long questionBankId;

    /**
     * Question ID
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}