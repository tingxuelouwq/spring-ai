package com.kevin.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Autowired
    private TicketService ticketService;

    @Tool(description = "退票")
    public String cancel(@ToolParam(description = "预定号") String ticketNumber,
                         @ToolParam(description = "真实人名（必填，必须为人的真实姓名，严禁用其他信息代替，如缺失请传null）") String name) {
        // 先根据name从数据库中校验用户是否存在
        ticketService.cancel(ticketNumber, name);
        return "退票成功";
    }

    @Tool(description = "获取指定位置天气，根据位置自动推算经纬度")
    public String getAirQuality(@ToolParam(description = "纬度") double latitude,
                                @ToolParam(description = "经度") double longitude) {
        return "天晴";
    }
}
