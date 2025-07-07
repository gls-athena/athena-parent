package com.gls.athena.starter.word.example;

import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.ExcelStyleWordGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Word文档导出示例Controller
 *
 * @author athena
 */
@RestController
@RequestMapping("/example/word")
public class WordExampleController {

    /**
     * 使用默认生成器导出Word文档（无模板）
     */
    @GetMapping("/export/default")
    @WordResponse(fileName = "用户报告")
    public Map<String, Object> exportDefault() {
        return Map.of(
                "title", "用户数据报告",
                "userName", "张三",
                "userAge", 25,
                "userEmail", "zhangsan@example.com",
                "department", "技术部",
                "createTime", "2025-01-07"
        );
    }

    /**
     * 使用模板导出Word文档
     */
    @GetMapping("/export/template")
    @WordResponse(
            fileName = "员工报告",
            template = "employee-report.docx"
    )
    public EmployeeData exportWithTemplate() {
        return new EmployeeData(
                "李四",
                28,
                "开发工程师",
                "技术部",
                List.of("Java", "Spring Boot", "MySQL"),
                Map.of(
                        "2023", "优秀",
                        "2024", "良好"
                )
        );
    }

    /**
     * 使用自定义生成器 - Excel风格表格
     */
    @GetMapping("/export/excel-style")
    @WordResponse(
            fileName = "Excel风格报告",
            generator = ExcelStyleWordGenerator.class
    )
    public Map<String, Object> exportWithCustomGenerator() {
        return Map.of(
                "订单号", "ORD-2025-001",
                "客户名称", "阿里巴巴科技有限公司",
                "订单金额", 58999.99,
                "订单状态", "已完成",
                "创建时间", "2025-01-07 10:30:00",
                "支付方式", "银行转账",
                "备注", "重要客户，优先处理"
        );
    }

    /**
     * 使用类级别注解
     */
    @GetMapping("/export/class-level")
    public SimpleData exportWithClassLevel() {
        return new SimpleData("测试数据", "这是一个测试", 100);
    }

    /**
     * 员工数据DTO
     */
    @Data
    @AllArgsConstructor
    public static class EmployeeData {
        private String name;
        private Integer age;
        private String position;
        private String department;
        private List<String> skills;
        private Map<String, String> performance;
    }

    /**
     * 简单数据DTO
     */
    @Data
    @AllArgsConstructor
    @WordResponse(fileName = "简单数据报告")
    public static class SimpleData {
        private String title;
        private String description;
        private Integer value;
    }
}
