package com.dsj.aiagent.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * @author dongsijun
 * @date 2026/3/16  15:08
 */
@Service
public class WeatherService {
    @Tool(description = "获取指定城市天气情况")
    public String getWeather(@ToolParam(description = "城市名称") String cityName){
        return "城市" +   cityName + "的天气是晴天,温度22ºC";
    }
}
