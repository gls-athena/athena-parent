package com.gls.athena.starter.word.support;

import com.gls.athena.starter.file.base.BaseFileResponseHandler;
import com.gls.athena.starter.file.base.BaseFileResponseWrapper;
import com.gls.athena.starter.word.annotation.WordResponse;
import com.gls.athena.starter.word.generator.WordGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Word响应处理器
 * 用于处理Word文档生成的响应处理器，继承自基础文件响应处理器
 *
 * @author george
 */
@Slf4j
public class WordResponseHandler extends BaseFileResponseHandler<WordGenerator, WordResponse> {

    /**
     * 构造函数
     * 初始化Word响应处理器，传入Word生成器列表
     *
     * @param wordGenerators Word生成器列表，用于处理Word文档生成
     */
    public WordResponseHandler(List<WordGenerator> wordGenerators) {
        super(wordGenerators);
    }

    /**
     * 获取响应注解类型
     * 返回WordResponse类的Class对象，用于标识处理的响应类型
     *
     * @return WordResponse.class Word响应注解的Class对象
     */
    @Override
    protected Class<WordResponse> getResponseClass() {
        return WordResponse.class;
    }

    /**
     * 获取响应包装器
     * 根据Word响应注解创建对应的响应包装器实例
     *
     * @param wordResponse Word响应注解对象
     * @return BaseFileResponseWrapper<WordResponse> 基础文件响应包装器实例
     */
    @Override
    protected BaseFileResponseWrapper<WordResponse> getResponseWrapper(WordResponse wordResponse) {
        return new WordResponseWrapper(wordResponse);
    }
}

