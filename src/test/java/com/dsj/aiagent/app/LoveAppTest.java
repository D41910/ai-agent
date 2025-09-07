package com.dsj.aiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dongsj
 * * @date 2025/9/7
 */
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好,我是dongsijun";
        String answer = loveApp.doChat(message, chatId);

        message = "我想让我的另一半(liaa)更爱我";
        answer = loveApp.doChat(message, chatId);

        message = "你好，我的另一半是谁";
        answer = loveApp.doChat(message, chatId);
    }
}