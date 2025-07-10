package com.zzy.techInterviewWeb.manager;



import com.github.xiaoymin.knife4j.spring.configuration.Knife4jAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
//@SpringBootTest(properties = {
//        "debug=true",
//        "logging.level.org.springframework.beans.factory.support=DEBUG",
//        "logging.level.org.springframework.context.support=DEBUG"
//})
@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = Knife4jAutoConfiguration.class)
class AIManagerTest {





    @Autowired
    private AIManager aiManager;




    String userPrompt = String.format("Number of questions: %s, Topic: %s", 4, "SpringBoot dependency");

    @Test
    void testDoChat() {


        String systemPrompt = "You are a professional programming interview coach. I need you to generate {number} interview questions about {topic}. The output format should be as follows:\n" +
                "\n" +
                "1. What is reflection in Java?\n" +
                "2. What is the purpose of the Stream API in Java 8?\n" +
                "3. xxxxxx\n" +
                "\n" +
                "Do not output any additional content—no introductions, no conclusions. Only output the list above.\n" +
                "\n" +
                "I will now provide you with the number of questions ({number}) and the topic ({topic}).\n";


        String s = aiManager.ask(systemPrompt,userPrompt);


        System.out.println("Chatgpt output:" +s);


    }


}