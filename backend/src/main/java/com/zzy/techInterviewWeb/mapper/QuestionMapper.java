package com.zzy.techInterviewWeb.mapper;

import com.zzy.techInterviewWeb.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Select;

/**
* @author zhiyuan
* @description 针对表【question(Question Table)】的数据库操作Mapper
* @createDate 2024-12-05 13:07:10
* @Entity com.zzy.techInterviewWeb.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * check the quesiton list include deleted
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);
}





