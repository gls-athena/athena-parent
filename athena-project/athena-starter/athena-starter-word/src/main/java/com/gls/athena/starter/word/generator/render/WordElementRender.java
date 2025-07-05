package com.gls.athena.starter.word.generator.render;

/**
 * Word文档元素渲染器接口
 *
 * @author athena
 */
public interface WordElementRender {

    /**
     * 渲染文档元素
     *
     * @param data    数据对象
     * @param context 渲染上下文
     */
    void render(Object data, WordRenderContext context);

    /**
     * 判断是否支持渲染该类型的数据
     *
     * @param data 数据对象
     * @param path 数据路径
     * @return 是否支持
     */
    boolean supports(Object data, String path);

    /**
     * 获取渲染器优先级，值越小优先级越高
     *
     * @return 优先级
     */
    default int getOrder() {
        return 100;
    }
}
