package com.zzy.techInterviewWeb.job.once;

import cn.hutool.core.collection.CollUtil;
import com.zzy.techInterviewWeb.esdao.QuestionEsDao;
import com.zzy.techInterviewWeb.model.dto.question.QuestionEsDTO;
import com.zzy.techInterviewWeb.model.entity.Question;
import com.zzy.techInterviewWeb.service.QuestionService;
import java.util.List;
import java.util.stream.Collectors;
 
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// todo Uncomment to enable the task, One time task to transfer
//  data from databse to ES
@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Autowired(required = false)
    private QuestionService questionService;

    @Autowired(required = false)
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) {
        // Retrieve all questions (use when data volume is not large)
        List<Question> questionList = questionService.list();
        if (CollUtil.isEmpty(questionList)) {
            return;
        }
        // Convert to ES entity class
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        // Paginated batch insertion to ES
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("FullSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            // Note: the index of the synced data should not exceed the total data size
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("FullSyncQuestionToEs end, total {}", total);
    }
}
