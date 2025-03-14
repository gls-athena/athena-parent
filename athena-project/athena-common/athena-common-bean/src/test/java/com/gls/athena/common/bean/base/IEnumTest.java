package com.gls.athena.common.bean.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IEnum接口的单元测试类
 */
public class IEnumTest {

    /**
     * 初始化方法
     */
    @BeforeEach
    public void setUp() throws Exception {

    }

    /**
     * 测试：传入null枚举类时抛出IllegalArgumentException
     */
    @Test
    public void of_NullEnumClass_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> IEnum.of(null, "test"));
        assertEquals("枚举类Class对象不能为null", exception.getMessage());
    }

    /**
     * 测试：传入null code时返回null
     */
    @Test
    public void of_NullCode_ReturnsNull() {
        TestEnum result = IEnum.of(TestEnum.class, null);
        assertNull(result);
    }

    /**
     * 测试：传入匹配的code时返回对应的枚举实例
     */
    @Test
    public void of_MatchingCode_ReturnsEnumInstance() {
        TestEnum result = IEnum.of(TestEnum.class, "1");
        assertNotNull(result);
        assertEquals("1", result.getCode());
    }

    /**
     * 测试：传入不匹配的code时返回null
     */
    @Test
    public void of_NonMatchingCode_ReturnsNull() {
        TestEnum result = IEnum.of(TestEnum.class, "nonexistent");
        assertNull(result);
    }

    /**
     * 测试：传入null枚举类时抛出IllegalArgumentException
     */
    @Test
    public void fromName_NullEnumClass_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> IEnum.fromName(null, "test", true));
        assertEquals("枚举类Class对象不能为null", exception.getMessage());
    }

    /**
     * 测试：传入null name时返回null
     */
    @Test
    public void fromName_NullName_ReturnsNull() {
        TestEnum result = IEnum.fromName(TestEnum.class, null, true);
        assertNull(result);
    }

    /**
     * 测试：大小写敏感匹配时返回对应的枚举实例
     */
    @Test
    public void fromName_CaseSensitiveMatch_ReturnsEnumInstance() {
        TestEnum result = IEnum.fromName(TestEnum.class, "one", true);
        assertEquals(TestEnum.ONE, result);
    }

    /**
     * 测试：大小写敏感不匹配时返回null
     */
    @Test
    public void fromName_CaseSensitiveNoMatch_ReturnsNull() {
        TestEnum result = IEnum.fromName(TestEnum.class, "ONE", true);
        assertNull(result);
    }

    /**
     * 测试：大小写不敏感匹配时返回对应的枚举实例
     */
    @Test
    public void fromName_CaseInsensitiveMatch_ReturnsEnumInstance() {
        TestEnum result = IEnum.fromName(TestEnum.class, "one", false);
        assertEquals(TestEnum.ONE, result);
    }

    /**
     * 测试：大小写不敏感不匹配时返回null
     */
    @Test
    public void fromName_CaseInsensitiveNoMatch_ReturnsNull() {
        TestEnum result = IEnum.fromName(TestEnum.class, "VALUE3", false);
        assertNull(result);
    }

    /**
     * 测试用枚举类
     */
    private enum TestEnum implements IEnum<String> {

        ONE("1", "one"),
        TWO("2", "two"),
        THREE("3", "three");

        private final String code;
        private final String name;

        TestEnum(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}