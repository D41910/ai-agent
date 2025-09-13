package com.dsj.aiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongsj
 * * @date 2025/9/13
 */
public class FileBasedChatMemory implements ChatMemory {

    private static final int DEFAULT_MAX_MESSAGES = 20;

    private final String BASE_DIR;

    private final int maxMessages;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    //构建对象时，指定文件保存目录
    public FileBasedChatMemory(String baseDir) {
        maxMessages = DEFAULT_MAX_MESSAGES;
        BASE_DIR = baseDir;
        File baseDirFile = new File(baseDir);
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs();
        }
    }

    //构建对象时，指定文件保存目录 指定历史记忆关联条数
    public FileBasedChatMemory(String baseDir , int maxMessages) {
        Assert.isTrue(maxMessages > 0, "maxMessages must be greater than 0");
        this.maxMessages = maxMessages;
        BASE_DIR = baseDir;
        File baseDirFile = new File(BASE_DIR);
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        return messageList.stream()
                .skip(Math.max(0,messageList.size()-maxMessages))
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 保存会话信息
     *
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取或创建会话消息的列表
     *
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = this.getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 每个会话文件单独保存
     *
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
