package com.kevin.springai.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class ToolService {

    @Autowired
    private TicketService ticketService;

    @Tool(description = "退票")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancel(@ToolParam(description = "预定号") String ticketNumber,
                         @ToolParam(description = "真实人名（必填，必须为人的真实姓名，严禁用其他信息代替，如缺失请传null）") String name) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 先根据name从数据库中校验用户是否存在
        ticketService.cancel(ticketNumber, name);
        return username + "退票成功";
    }

    @Tool(description = "获取指定位置天气，根据位置自动推算经纬度")
    public String getAirQuality(@ToolParam(description = "纬度") double latitude,
                                @ToolParam(description = "经度") double longitude) {
        return "天晴";
    }

    /**
     * 模拟从数据库中动态根据当前用户角色读取Tools
     */
    public List<ToolCallback> getToolCallList(ToolService toolService) {
        // 从数据库中读取的代码，略
        // 以获取到一个Tool为例

        // 1. 获取Tools处理的方法
        Method method = ReflectionUtils.findMethod(ToolService.class, "cancel", String.class, String.class);
        // 2. 构建ToolDefinition对象
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("cancel")
                .description("退票")
                .inputSchema("""
                        {
                            "type": "object",
                            "properties": {
                                "ticketNumber": {
                                    "type": "string",
                                    "description": "预定号"
                                },
                                "name": {
                                    "type": "string",
                                    "description": "姓名"
                                }
                            },
                            "required": ["ticketNumber", "name"]
                        }
                        """)
                .build();
        // 3. 构建ToolCallback
        return List.of(MethodToolCallback.builder()
                .toolDefinition(toolDefinition)
                .toolMethod(method)
                .toolObject(toolService)    // 使用Spring管理的Bean
                .build());
    }
}
