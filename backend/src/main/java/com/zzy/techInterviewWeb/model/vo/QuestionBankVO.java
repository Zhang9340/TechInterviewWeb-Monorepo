package com.zzy.techInterviewWeb.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.model.entity.QuestionBank;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * questionBank vo
 *
 *
 *
 */
@Data
public class QuestionBankVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * Title
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

    /**
     * Creator User ID
     */
    private Long userId;


    /**
     * Creation Time
     */
    private Date createTime;

    /**
     * Update Time
     */
    private Date updateTime;


    /**
     * user created
     */
    private UserVO user;

    /**
     * questions  under the question bank
     */
    Page<QuestionVO> questionPage;

    /**
     *
     *
     * @param questionBankVO
     * @return
     */
    public static QuestionBank voToObj(QuestionBankVO questionBankVO) {
        if (questionBankVO == null) {
            return null;
        }
        QuestionBank questionBank = new QuestionBank();
        BeanUtils.copyProperties(questionBankVO, questionBank);
        return questionBank;
    }

    /**
     *
     *
     * @param questionBank
     * @return
     */
    public static QuestionBankVO objToVo(QuestionBank questionBank) {
        if (questionBank == null) {
            return null;
        }
        QuestionBankVO questionBankVO = new QuestionBankVO();
        BeanUtils.copyProperties(questionBank, questionBankVO);
        return questionBankVO;
    }
}
