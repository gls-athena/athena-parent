package com.gls.athena.common.bean.util;

import com.gls.athena.common.bean.result.ResultException;
import com.gls.athena.common.bean.result.ResultStatus;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * <p>
 * 提供常用的数据验证方法，支持链式调用和自定义错误消息。
 * 验证失败时抛出 ResultException 异常。
 * </p>
 *
 * @author george
 * @since 1.0.0
 */
@UtilityClass
public class ValidateUtil {

    /**
     * 邮箱格式正则表达式：支持常见的邮箱格式
     */
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 手机号格式正则表达式：支持中国大陆手机号（1开头，第二位为3-9）
     */
    private final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 身份证号格式正则表达式：支持18位身份证号（最后一位可为X）
     */
    private final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * 验证对象不为空
     *
     * @param <T>    泛型类型
     * @param object 待验证的对象
     * @return 返回原对象
     * @throws ResultException 如果对象为 null，则抛出异常
     */
    public <T> T notNull(T object) {
        return notNull(object, "参数不能为空");
    }

    /**
     * 验证对象不为空（自定义错误消息）
     *
     * @param <T>     泛型类型
     * @param object  待验证的对象
     * @param message 错误消息
     * @return 返回原对象
     * @throws ResultException 如果对象为 null，则抛出异常
     */
    public <T> T notNull(T object, String message) {
        if (object == null) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return object;
    }

    /**
     * 验证对象不为空（使用供应商提供错误消息）
     *
     * @param <T>             泛型类型
     * @param object          待验证的对象
     * @param messageSupplier 错误消息供应器
     * @return 返回原对象
     * @throws ResultException 如果对象为 null，则抛出异常
     */
    public <T> T notNull(T object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), messageSupplier.get());
        }
        return object;
    }

    /**
     * 验证字符串不为空且不为空白
     *
     * @param str 待验证的字符串
     * @return 返回原字符串
     * @throws ResultException 如果字符串为 null 或者去除空白后为空，则抛出异常
     */
    public String notBlank(String str) {
        return notBlank(str, "字符串不能为空");
    }

    /**
     * 验证字符串不为空且不为空白（自定义错误消息）
     *
     * @param str     待验证的字符串
     * @param message 错误消息
     * @return 返回原字符串
     * @throws ResultException 如果字符串为 null 或者去除空白后为空，则抛出异常
     */
    public String notBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return str;
    }

    /**
     * 验证集合不为空
     *
     * @param <T>        集合类型
     * @param collection 待验证的集合
     * @return 返回原集合
     * @throws ResultException 如果集合为 null 或者为空，则抛出异常
     */
    public <T extends Collection<?>> T notEmpty(T collection) {
        return notEmpty(collection, "集合不能为空");
    }

    /**
     * 验证集合不为空（自定义错误消息）
     *
     * @param <T>        集合类型
     * @param collection 待验证的集合
     * @param message    错误消息
     * @return 返回原集合
     * @throws ResultException 如果集合为 null 或者为空，则抛出异常
     */
    public <T extends Collection<?>> T notEmpty(T collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return collection;
    }

    /**
     * 验证数组不为空
     *
     * @param <T>   数组元素类型
     * @param array 待验证的数组
     * @return 返回原数组
     * @throws ResultException 如果数组为 null 或者长度为 0，则抛出异常
     */
    public <T> T[] notEmpty(T[] array) {
        return notEmpty(array, "数组不能为空");
    }

    /**
     * 验证数组不为空（自定义错误消息）
     *
     * @param <T>     数组元素类型
     * @param array   待验证的数组
     * @param message 错误消息
     * @return 返回原数组
     * @throws ResultException 如果数组为 null 或者长度为 0，则抛出异常
     */
    public <T> T[] notEmpty(T[] array, String message) {
        if (array == null || array.length == 0) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return array;
    }

    /**
     * 验证条件为真
     *
     * @param condition 条件表达式
     * @param message   错误消息
     * @throws ResultException 如果条件为假，则抛出异常
     */
    public void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 验证条件为假
     *
     * @param condition 条件表达式
     * @param message   错误消息
     * @throws ResultException 如果条件为真，则抛出异常
     */
    public void isFalse(boolean condition, String message) {
        if (condition) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 验证两个对象相等
     *
     * @param obj1    第一个对象
     * @param obj2    第二个对象
     * @param message 错误消息
     * @throws ResultException 如果两个对象不相等，则抛出异常
     */
    public void equals(Object obj1, Object obj2, String message) {
        if (!Objects.equals(obj1, obj2)) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 验证数字在指定范围内
     *
     * @param value   要验证的数值
     * @param min     最小值
     * @param max     最大值
     * @param message 错误消息
     * @throws ResultException 如果数值不在指定范围内，则抛出异常
     */
    public void inRange(Number value, Number min, Number max, String message) {
        notNull(value, "数值不能为空");
        if (value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
    }

    /**
     * 验证字符串长度在指定范围内
     *
     * @param str 待验证的字符串
     * @param min 最小长度
     * @param max 最大长度
     * @return 返回原字符串
     * @throws ResultException 如果字符串长度不在指定范围内，则抛出异常
     */
    public String lengthBetween(String str, int min, int max) {
        return lengthBetween(str, min, max,
                String.format("字符串长度必须在%d到%d之间", min, max));
    }

    /**
     * 验证字符串长度在指定范围内（自定义错误消息）
     *
     * @param str     待验证的字符串
     * @param min     最小长度
     * @param max     最大长度
     * @param message 错误消息
     * @return 返回原字符串
     * @throws ResultException 如果字符串长度不在指定范围内，则抛出异常
     */
    public String lengthBetween(String str, int min, int max, String message) {
        notNull(str, "字符串不能为空");
        int length = str.length();
        if (length < min || length > max) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return str;
    }

    /**
     * 验证邮箱格式
     *
     * @param email 待验证的邮箱地址
     * @return 返回原邮箱地址
     * @throws ResultException 如果邮箱格式不符合要求，则抛出异常
     */
    public String isEmail(String email) {
        return isEmail(email, "邮箱格式不正确");
    }

    /**
     * 验证邮箱格式（自定义错误消息）
     *
     * @param email   待验证的邮箱地址
     * @param message 错误消息
     * @return 返回原邮箱地址
     * @throws ResultException 如果邮箱格式不符合要求，则抛出异常
     */
    public String isEmail(String email, String message) {
        notBlank(email, "邮箱不能为空");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return email;
    }

    /**
     * 验证手机号格式
     *
     * @param mobile 待验证的手机号
     * @return 返回原手机号
     * @throws ResultException 如果手机号格式不符合要求，则抛出异常
     */
    public String isMobile(String mobile) {
        return isMobile(mobile, "手机号格式不正确");
    }

    /**
     * 验证手机号格式（自定义错误消息）
     *
     * @param mobile  待验证的手机号
     * @param message 错误消息
     * @return 返回原手机号
     * @throws ResultException 如果手机号格式不符合要求，则抛出异常
     */
    public String isMobile(String mobile, String message) {
        notBlank(mobile, "手机号不能为空");
        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return mobile;
    }

    /**
     * 验证身份证号格式
     *
     * @param idCard 待验证的身份证号
     * @return 返回原身份证号
     * @throws ResultException 如果身份证号格式不符合要求，则抛出异常
     */
    public String isIdCard(String idCard) {
        return isIdCard(idCard, "身份证号格式不正确");
    }

    /**
     * 验证身份证号格式（自定义错误消息）
     *
     * @param idCard  待验证的身份证号
     * @param message 错误消息
     * @return 返回原身份证号
     * @throws ResultException 如果身份证号格式不符合要求，则抛出异常
     */
    public String isIdCard(String idCard, String message) {
        notBlank(idCard, "身份证号不能为空");
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return idCard;
    }

    /**
     * 验证正则表达式匹配
     *
     * @param str     待验证的字符串
     * @param pattern 正则表达式模式
     * @param message 错误消息
     * @return 返回原字符串
     * @throws ResultException 如果字符串不匹配正则表达式，则抛出异常
     */
    public String matches(String str, Pattern pattern, String message) {
        notBlank(str, "字符串不能为空");
        notNull(pattern, "正则表达式不能为空");
        if (!pattern.matcher(str).matches()) {
            throw new ResultException(ResultStatus.PARAM_ERROR.getCode(), message);
        }
        return str;
    }

    /**
     * 验证正则表达式匹配
     *
     * @param str     待验证的字符串
     * @param regex   正则表达式字符串
     * @param message 错误消息
     * @return 返回原字符串
     * @throws ResultException 如果字符串不匹配正则表达式，则抛出异常
     */
    public String matches(String str, String regex, String message) {
        return matches(str, Pattern.compile(regex), message);
    }
}
