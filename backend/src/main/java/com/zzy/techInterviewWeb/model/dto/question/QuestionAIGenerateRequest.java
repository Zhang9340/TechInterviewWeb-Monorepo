package com.zzy.techInterviewWeb.model.dto.question;

import lombok.Data;





import java.io.Serializable;





/**


 * AI generate question request


 *


 */


@Data


public class QuestionAIGenerateRequest implements Serializable {





    /**


     * Question Type

     */


    private String questionType;





    /**


     * Question Number


     */


    private int number = 10;





    private static final long serialVersionUID = 1L;


}