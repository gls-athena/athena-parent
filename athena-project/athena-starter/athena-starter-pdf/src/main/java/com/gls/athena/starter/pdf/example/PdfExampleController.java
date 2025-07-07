package com.gls.athena.starter.pdf.example;

import com.gls.athena.starter.pdf.annotation.PdfResponse;
import com.gls.athena.starter.pdf.config.TemplateType;
import com.gls.athena.starter.pdf.generator.ReportStylePdfGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * PDF文档导出示例Controller
 *
 * @author athena
 */
@RestController
@RequestMapping("/example/pdf")
public class PdfExampleController {

    /**
     * 使用默认生成器导出PDF文档（无模板）
     */
    @GetMapping("/export/default")
    @PdfResponse(fileName = "用户报告")
    public Map<String, Object> exportDefault() {
        return Map.of(
                "用户名", "张三",
                "年龄", 25,
                "邮箱", "zhangsan@example.com",
                "部门", "技术部",
                "入职时间", "2023-01-15",
                "薪资", 12000.50
        );
    }

    /**
     * 使用HTML模板导出PDF文档
     */
    @GetMapping("/export/html")
    @PdfResponse(
            fileName = "员工报告",
            template = "employee-report.html",
            templateType = TemplateType.HTML
    )
    public EmployeeData exportWithHtmlTemplate() {
        return new EmployeeData(
                "李四",
                28,
                "高级开发工程师",
                "技术部",
                BigDecimal.valueOf(15000),
                LocalDate.of(2022, 3, 10),
                List.of("Java", "Spring Boot", "MySQL", "Redis")
        );
    }

    /**
     * 使用自定义生成器 - 报表风格
     */
    @GetMapping("/export/report-style")
    @PdfResponse(
            fileName = "销售报表",
            generator = ReportStylePdfGenerator.class
    )
    public Map<String, Object> exportWithCustomGenerator() {
        return Map.of(
                "总销售额", 2580000.00,
                "订单数量", 1250,
                "客户数量", 350,
                "平均订单金额", 2064.00,
                "退款率", 2.3,
                "客户满意度", 98.5,
                "本月增长率", 15.8
        );
    }

    /**
     * 内联显示PDF（浏览器内查看）
     */
    @GetMapping("/export/inline")
    @PdfResponse(
            fileName = "产品目录",
            inline = true
    )
    public List<ProductData> exportInline() {
        return List.of(
                new ProductData("笔记本电脑", "DELL XPS 13", BigDecimal.valueOf(8999.00), 50),
                new ProductData("智能手机", "iPhone 15", BigDecimal.valueOf(7999.00), 120),
                new ProductData("平板电脑", "iPad Air", BigDecimal.valueOf(4999.00), 80)
        );
    }

    /**
     * 使用类级别注解
     */
    @GetMapping("/export/class-level")
    public SimpleReportData exportWithClassLevel() {
        return new SimpleReportData("季度总结", "2024年第四季度表现良好", 95.5);
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
        private BigDecimal salary;
        private LocalDate hireDate;
        private List<String> skills;
    }

    /**
     * 产品数据DTO
     */
    @Data
    @AllArgsConstructor
    public static class ProductData {
        private String category;
        private String name;
        private BigDecimal price;
        private Integer stock;
    }

    /**
     * 简单报告数据DTO
     */
    @Data
    @AllArgsConstructor
    @PdfResponse(fileName = "简单报告", templateType = TemplateType.DATA)
    public static class SimpleReportData {
        private String title;
        private String description;
        private Double score;
    }
}
