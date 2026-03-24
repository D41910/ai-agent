package com.dsj.aiagent.controller;

import com.dsj.aiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author dongsijun
 * @date 2026/3/24  09:02
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallbackProvider allTools;

    @Resource
    private ChatModel chatModel;

    @GetMapping("/love_app/char/sync")
    public Flux<String> doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChatBySystem(message, chatId);
    }

    @GetMapping(value = "/love_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatBySystem(message, chatId);
    }

//    @GetMapping(value = "/love_app/chat/sse")
//    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSE(String message, String chatId) {
//        return loveApp.doChatBySystem(message, chatId)
//                .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build());
//    }

    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        //创建一个超长时间的SseEmitter
        SseEmitter emitter = new SseEmitter(180000L);
        //获取Flux数据流并直接订阅
        loveApp.doChatBySystem(message,chatId)
                .subscribe(
                        chunk -> {
                            try{
                                emitter.send(chunk);
                            }catch (IOException e){
                                emitter.completeWithError(e);
                            }
                        },
                            //处理错误
                            emitter::completeWithError,
                            //处理完成
                            emitter::complete
                );
        return emitter;
    }

}
