package com.dsj.aiagent.tool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author dongsijun
 * @date 2026/3/5  10:06
 */
@SpringBootTest
class TerminalOperationToolTest {

    @Test
    void executeTerminalCommand() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        String s = terminalOperationTool.executeTerminalCommand("ls -l");
        System.out.println(s);
    }
}