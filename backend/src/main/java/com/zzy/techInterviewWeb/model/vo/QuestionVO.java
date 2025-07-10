package com.zzy.techInterviewWeb.model.vo;

import cn.hutool.json.JSONUtil;
import com.zzy.techInterviewWeb.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * question vo
 *
 *
 *
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * Title
     */
    private String title;

    /**
     * Content
     */
    private String content;


    /**
     * Recommended Answer
     */
    private String answer;

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
     * tag list
     */
    private List<String> tagList;

    /**
     * User info
     */
    private UserVO user;

    /**
     * Transfer to Object from VO
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTagList();
        question.setTags(JSONUtil.toJsonStr(tagList));
        return question;
    }

    /**
     * Object ot VO
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTagList(JSONUtil.toList(question.getTags(), String.class));
        return questionVO;
    }
}
