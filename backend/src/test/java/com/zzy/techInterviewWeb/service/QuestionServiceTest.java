package com.zzy.techInterviewWeb.service;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration;
import com.zzy.techInterviewWeb.model.entity.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = Knife4jAutoConfiguration.class)
public class QuestionServiceTest {
    @Autowired
    QuestionService questionService;
    @Test
     void aiGenerateTest(){
        boolean res = questionService.aiGenerateQuestions("Springboot Dependency Injection",2,new User());
        Assertions.assertEquals(true, res);
    }

}
