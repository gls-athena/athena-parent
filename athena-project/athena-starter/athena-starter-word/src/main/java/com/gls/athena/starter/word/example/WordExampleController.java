package com.gls.athena.starter.word.example;

import com.gls.athena.starter.word.annotation.WordResponse;
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
     * 使用类级别注解
     */
    @GetMapping("/export/class-level")
    public SimpleData exportWithClassLevel() {
        return new SimpleData("测试数据", "这是一个测试", 100);
    }

    /**
     * 员工数据DTO
     */
    public static class EmployeeData {
        private String name;
        private Integer age;
        private String position;
        private String department;
        private List<String> skills;
        private Map<String, String> performance;

        public EmployeeData(String name, Integer age, String position, String department,
                            List<String> skills, Map<String, String> performance) {
            this.name = name;
            this.age = age;
            this.position = position;
            this.department = department;
            this.skills = skills;
            this.performance = performance;
        }

        // Getters
        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getPosition() {
            return position;
        }

        public String getDepartment() {
            return department;
        }

        public List<String> getSkills() {
            return skills;
        }

        public Map<String, String> getPerformance() {
            return performance;
        }
    }

    /**
     * 简单数据DTO
     */
    @WordResponse(fileName = "简单数据报告")
    public static class SimpleData {
        private String title;
        private String description;
        private Integer value;

        public SimpleData(String title, String description, Integer value) {
            this.title = title;
            this.description = description;
            this.value = value;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Integer getValue() {
            return value;
        }
    }
}
