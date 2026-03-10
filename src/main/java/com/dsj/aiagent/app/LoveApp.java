package com.dsj.aiagent.app;


import com.dsj.aiagent.advisor.MyLoggerAdvisor;
import com.dsj.aiagent.chatmemory.FileBasedChatMemory;
import com.dsj.aiagent.rag.LoveAppContextualQueryAugmenterFactory;
import com.dsj.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.dsj.aiagent.rag.QueryRewriter;
import com.dsj.aiagent.tool.WeatherTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @author dongsj
 * * @date 2025/9/7
 */
@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；" +
            "已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";


    // AI 恋爱知识库问答功能
    @Resource(name = "pgVectorVectorStore")
    private VectorStore loveReportVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    public LoveApp(ChatModel dashscopeChatModel) {
//        //给ChatModel绑定工具
//        // 先得到工具对象
//        ToolCallback[] weatherTools = ToolCallbacks.from(new WeatherTools());
//        // 绑定工具到对话
//        ChatOptions chatOptions = ToolCallingChatOptions.builder()
//                .toolCallbacks(weatherTools)
//                .build();
//        // 构造 Prompt 时指定对话选项
//        Prompt prompt = new Prompt("北京今天天气怎么样？", chatOptions);
//        dashscopeChatModel.call(prompt);


        //初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        //初始化基于内存的对话记忆
//        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
                //注册默认工具
                .defaultTools(new WeatherTools())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
//                        //自定义拦截器 Advisor，可按需开启
                        new MyLoggerAdvisor()
//                        //自定义推力增强器 Advisor，可按需开启
//                        new ReReadingAdvisor()
                ).build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: " + content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {

    }

    /**
     * AI 报告功能演示结构化输出
     *
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithResponse(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);

        log.info("loveReport: " + loveReport);
        return loveReport;
    }

    /**
     * 和RAG知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(loveReportVectorStore).build();
        //查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient.prompt()
                .user(rewrittenMessage)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                //开启日志便于观察效果
                .advisors(new MyLoggerAdvisor())
                //应用RAG知识库代码
//                .advisors(new QuestionAnswerAdvisor(loveReportVectorStore))
                //空上下文处理
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(loveReportVectorStore)
                                .filterExpression(new FilterExpressionBuilder().eq("status", "单身").build())
                                .similarityThreshold(0.99)
                                .topK(3)
                                .build())
                        .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                        .build())
                //文档过滤
//                .advisors(
//                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                                loveReportVectorStore,"已婚"))
                //应用增强RAG增强服务（基于云知识库服务）
//                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: " + content);
        return content;
    }

    /**
     * 用户问题扩展demo
     *
     * @param message
     * @param chatId
     * @return
     */
    public void doChatWithRagQueryExpander(String message, String chatId) {
        MultiQueryExpander multiQueryExpander = MultiQueryExpander
                .builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .numberOfQueries(3)
                .build();
        List<Query> expand = multiQueryExpander.expand(new Query(message));
        VectorStoreDocumentRetriever build = VectorStoreDocumentRetriever.builder().vectorStore(loveReportVectorStore).build();
        for (Query query : expand) {
            List<Document> retrieve = build.retrieve(query);
            System.out.println(retrieve.size());
        }
    }

    /**
     * 工具调用
     *
     * @param message
     * @param chatId
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatId))
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}" + content);
        return content;

    }


}
