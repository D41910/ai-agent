package com.dsj.aiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 阿里云灵积 AI Http 调用
 */
public class HttpAiInvoke {

    public static void main(String[] args) {
        // 替换为你的API密钥
        String apiKey = TestApiKey.API_KEY;
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        
        // 构建请求体JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");
        
        // 构建input对象
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        
        // 添加system消息
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");
        messages.add(systemMsg);
        
        // 添加user消息
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", "你是谁？");
        messages.add(userMsg);
        
        input.put("messages", messages);
        requestBody.put("input", input);
        
        // 构建parameters
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        requestBody.put("parameters", parameters);
        
        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();
        
        // 处理响应
        if (response.isOk()) {
            System.out.println("请求成功，响应结果：");
            System.out.println(response.body());
        } else {
            System.err.println("请求失败，状态码：" + response.getStatus());
            System.err.println("错误信息：" + response.body());
        }
    }
}
