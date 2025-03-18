package com.gls.athena.common.core.base;

import com.gls.athena.common.bean.page.PageRequest;
import com.gls.athena.common.bean.page.PageResponse;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 转换器
 *
 * @param <S> 源
 * @param <T> 目标
 * @author george
 */
public interface IConverter<S, T> {
    /**
     * 转换 源 -> 目标
     *
     * @param source 源
     * @return 目标
     */
    T convert(S source);

    /**
     * 拷贝转换 源 -> 目标
     *
     * @param source 源
     * @param target 目标
     */
    @InheritConfiguration(name = "convert")
    void convertCopy(S source, @MappingTarget T target);

    /**
     * 将源列表中的每个元素转换为目标类型，并生成一个新的目标列表。
     * 该方法使用流式操作对源列表中的每个元素进行转换，并将结果收集到一个新的列表中。
     *
     * @param sources 包含源类型元素的集合，不能为null
     * @return 包含目标类型元素的列表，如果源列表为空，则返回空列表
     */
    default List<T> convertList(Collection<S> sources) {
        // 使用流式操作将源集合中的每个元素转换为目标类型，并收集到列表中
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
        // 使用流式处理将源集合中的每个元素转换为目标类型，并收集为Set集合
        return sources.stream().map(this::convert).collect(Collectors.toSet());
    }

    /**
     * 转换分页对象，将源分页对象转换为目标分页对象。
     * 该函数主要用于将一种类型的分页请求（源分页）转换为另一种类型的分页请求（目标分页）。
     * 转换过程中，源分页的页码、每页大小、排序字段、排序顺序等属性会被复制到目标分页中，
     * 同时源分页的参数会通过 `convert` 方法进行转换并设置到目标分页中。
     *
     * @param sourcePage 源分页对象，包含需要转换的分页信息。
     * @return 目标分页对象，包含转换后的分页信息。
     */
    default PageRequest<T> convertPage(PageRequest<S> sourcePage) {
        // 创建新的目标分页对象，并复制源分页的基本属性
        return new PageRequest<T>()
                .setPage(sourcePage.getPage())
                .setSize(sourcePage.getSize())
                .setSort(sourcePage.getSort())
                .setOrder(sourcePage.getOrder())
                .setParams(convert(sourcePage.getParams()));
    }

    /**
     * 转换 目标 -> 源
     *
     * @param target 目标
     * @return 源
     */
    @InheritInverseConfiguration(name = "convert")
    S reverse(T target);

    /**
     * 拷贝转换 目标 -> 源
     *
     * @param target 目标
     * @param source 源
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
        // 使用流处理目标列表，将每个元素通过 reverse 方法转换为源类型，并收集为列表
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
        // 使用流处理目标列表，将每个元素转换为源类型，并收集到集合中
        return targets.stream().map(this::reverse).collect(Collectors.toSet());
    }

    /**
     * 将目标分页对象转换为源分页对象。
     * 该函数主要用于将目标分页对象中的分页信息（如页码、每页大小、总记录数、总页数）以及数据列表转换为源分页对象。
     * 数据列表的转换通过调用 {@code reverseList} 方法实现。
     *
     * @param targetPage 目标分页对象，包含需要转换的分页信息和数据列表
     * @return 转换后的源分页对象，包含与目标分页相同的分页信息，但数据列表已通过 {@code reverseList} 方法转换
     */
    default PageResponse<S> reversePage(PageResponse<T> targetPage) {
        // 创建并返回一个新的源分页对象，设置分页信息和转换后的数据列表
        return new PageResponse<S>()
                .setPage(targetPage.getPage())
                .setSize(targetPage.getSize())
                .setTotal(targetPage.getTotal())
                .setPages(targetPage.getPages())
                .setData(reverseList(targetPage.getData()));
    }

}
