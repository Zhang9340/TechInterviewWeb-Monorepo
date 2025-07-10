package com.zzy.techInterviewWeb.manager;

 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cos 操作测试
 *
 * 
 * 
 */
@SpringBootTest
class CosManagerTest {

    @Autowired(required = false)
    private CosManager cosManager;

    @Test
    void putObject() {
        cosManager.putObject("test", "test.json");
    }
}