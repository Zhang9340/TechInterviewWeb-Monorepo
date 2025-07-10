package com.zzy.techInterviewWeb.esdao;

import com.zzy.techInterviewWeb.model.dto.question.QuestionEsDTO;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 *  Question ES
 */
public interface QuestionEsDao
        extends ElasticsearchRepository<QuestionEsDTO, Long> {

    /**
     *  search base on user ID
     * @param userId
     * @return
     */
    List<QuestionEsDTO> findByUserId(Long userId);


}

