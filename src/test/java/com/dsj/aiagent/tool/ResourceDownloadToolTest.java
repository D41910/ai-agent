package com.dsj.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dongsijun
 * @date 2026/3/5  10:16
 */
@SpringBootTest
class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        String s = resourceDownloadTool.downloadResource("https://picsum.photos/id/237/500/500", "logo.png");
        System.out.println(s);

    }
}