package com.gls.athena.common.core.base;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 转换器接口，用于在源类型 S 和目标类型 T 之间进行相互转换。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * @author george
 */
public interface IConverter<S, T> {
    /**
     * 将源对象转换为目标对象。
     *
     * @param source 源对象
     * @return 转换后的目标对象
     */
    T convert(S source);

    /**
     * 将源对象的属性拷贝到已存在的目标对象中。
     *
     * @param source 源对象
     * @param target 已存在的目标对象
     */
    @InheritConfiguration(name = "convert")
    void convertCopy(S source, @MappingTarget T target);

    /**
     * 将源集合中的每个元素转换为目标类型，并生成一个新的目标列表。
     * 该方法使用流式操作对源集合中的每个元素进行转换，并将结果收集到一个新的列表中。
     *
     * @param sources 包含源类型元素的集合，不能为null
     * @return 包含目标类型元素的列表，如果源集合为空，则返回空列表
     */
    default List<T> convertList(Collection<S> sources) {
        if (sources == null) {
            return Collections.emptyList();
        }
        return sources.stream().map(this::convert).toList();
    }

    /**
     * 将源集合转换为目标集合。
     * 该函数接收一个源集合，通过流式处理将每个元素转换为目标类型，并最终收集为一个Set集合。
     *
     * @param sources 源集合，包含需要转换的元素
     * @return 转换后的目标集合，类型为Set<T>
     */
    default Set<T> convertSet(Collection<S> sources) {
        if (sources == null) {
            return Collections.emptySet();
        }
        return sources.stream().map(this::convert).collect(Collectors.toSet());
    }

    /**
     * 将目标对象反向转换为源对象。
     *
     * @param target 目标对象
     * @return 转换后的源对象
     */
    @InheritInverseConfiguration(name = "convert")
    S reverse(T target);

    /**
     * 将目标对象的属性拷贝到已存在的源对象中。
     *
     * @param target 目标对象
     * @param source 已存在的源对象
     */
    @InheritConfiguration(name = "reverse")
    void reverseCopy(T target, @MappingTarget S source);

    /**
     * 将目标列表转换为源列表。该方法通过遍历目标列表中的每个元素，并调用 {@code reverse} 方法将其转换为源类型，最终返回转换后的源列表。
     *
     * @param targets 目标列表，包含需要转换的元素
     * @return 转换后的源列表，包含转换后的元素
     */
    default List<S> reverseList(Collection<T> targets) {
        if (targets == null) {
            return Collections.emptyList();
        }
        return targets.stream().map(this::reverse).toList();
    }

    /**
     * 将目标列表转换为源列表的集合。
     * 该函数通过遍历目标列表中的每个元素，并使用 {@code reverse} 方法将其转换为源类型，
     * 最终将所有转换后的元素收集到一个集合中返回。
     *
     * @param targets 目标列表，包含需要转换的元素
     * @return 转换后的源列表集合
     */
    default Set<S> reverseSet(Collection<T> targets) {
        if (targets == null) {
            return Collections.emptySet();
        }
        return targets.stream().map(this::reverse).collect(Collectors.toSet());
    }

}
