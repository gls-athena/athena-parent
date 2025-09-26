package com.gls.athena.starter.word.support;

import com.gls.athena.starter.file.base.FileResponseHandler;
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
public class WordResponseHandler extends FileResponseHandler<WordGenerator, WordResponse> {

    /**
     * 构造函数
     * 初始化Word响应处理器，传入Word生成器列表
     *
     * @param wordGenerators Word生成器列表，用于处理Word文档生成
     */
    public WordResponseHandler(List<WordGenerator> wordGenerators) {
        super(wordGenerators);
    }

}

