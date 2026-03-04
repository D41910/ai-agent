package com.dsj.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dongsijun
 * @date 2026/3/4  15:49
 */
@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "dsj.text";
        String content = fileOperationTool.readFile(fileName);
        assertNotNull(content);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "dsj.text";
        String content = "阿斯利康到哪里睡";
        String result = fileOperationTool.writeFile(fileName, content);
        assertNotNull(result);
    }
}