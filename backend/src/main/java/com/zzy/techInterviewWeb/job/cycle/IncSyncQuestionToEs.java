package com.zzy.techInterviewWeb.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.zzy.techInterviewWeb.esdao.QuestionEsDao;
import com.zzy.techInterviewWeb.mapper.QuestionMapper;
import com.zzy.techInterviewWeb.model.dto.question.QuestionEsDTO;
import com.zzy.techInterviewWeb.model.entity.Question;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// todo Uncomment to enable the task
@Component()
@Slf4j
public class IncSyncQuestionToEs {

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private QuestionEsDao questionEsDao;

    /**
     * Execute every minute
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // Query data from the last 5 minutes
        long FIVE_MINUTES = 5 * 60 * 1000L;
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionList)) {
            log.info("no inc question");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("IncSyncQuestionToEs end, total {}", total);
    }
}
