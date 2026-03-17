package com.dsj.aiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dongsijun
 * @date 2026/3/18  04:06
 */
@SpringBootTest
class DsjManusTest {

    @Resource
    private DsjManus dsjManus;

    @Test
    void run(){
        String userPrompt = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，并结合一些网络图片，制定一份详细的约会计划，并以 PDF 格式输出,中文输出";
        String answer = dsjManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
