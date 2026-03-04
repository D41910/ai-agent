package com.dsj.aiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author dongsijun
 * @date 2026/3/4  14:59
 */
public class WeatherTools {
    @Tool(description = "获取指定城市天气情况")
    String getWeather(@ToolParam(description = "城市名称") String city) {
        return city + "今天晴朗,气温25ºC";
    }
}
