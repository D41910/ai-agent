package com.dsj.aiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void doChatWithResponse() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好,我是dongsijun,我想让我的另一半(liaa)更爱我,但我不知道怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithResponse(message, chatId);
        Assertions.assertNotNull(loveReport);

    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithRagQueryExpander() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        loveApp.doChatWithRagQueryExpander(message, chatId);
    }

    @Test
    void doChatWithTools(){
        String chatId = UUID.randomUUID().toString();
        String message = "武汉天气怎么样";
        String answer = loveApp.doChatWithTools(message, chatId);
    }

}