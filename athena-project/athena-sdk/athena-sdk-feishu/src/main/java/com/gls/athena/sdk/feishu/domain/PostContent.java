package com.gls.athena.sdk.feishu.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 富文本内容
 *
 * @author george
 */
@Data
public class PostContent implements Serializable {
    /**
     * 中文
     */
    @JsonProperty("zh_cn")
    private Content zhCn;
    /**
     * 英文
     */
    @JsonProperty("en_us")
    private Content enUs;

    /**
     * 标签
     */
    public interface Tag extends Serializable {
        /**
         * 获取标签
         *
         * @return 标签
         */
        String getTag();
    }

    /**
     * 内容
     */
    @Data
    public static class Content implements Serializable {
        /**
         * 标题
         */
        private String title;
        /**
         * 内容
         */
        private List<List<Tag>> content;
    }

    /**
     * text：文本标签
     */
    @Data
    public static class TextTag implements Tag {
        /**
         * 文本
         */
        private String text;
        /**
         * 是否转义
         */
        @JsonProperty("un_escape")
        private boolean unEscape = false;
        /**
         * 样式
         */
        private List<String> style = new ArrayList<>();

        @Override
        public String getTag() {
            return "text";
        }
    }

    /**
     * a：超链接标签
     */
    @Data
    public static class ATag implements Tag {
        /**
         * 文本
         */
        private String text;
        /**
         * 超链接
         */
        private String href;
        /**
         * 样式
         */
        private List<String> style = new ArrayList<>();

        @Override
        public String getTag() {
            return "a";
        }
    }

    /**
     * at：@标签
     */
    @Data
    public static class AtTag implements Tag {
        /**
         * 用户id
         */
        @JsonProperty("user_id")
        private String userId;
        /**
         * 样式
         */
        private List<String> style = new ArrayList<>();

        @Override
        public String getTag() {
            return "at";
        }
    }

    /**
     * img：图片标签
     */
    @Data
    public static class ImgTag implements Tag {
        /**
         * 图片地址
         */
        @JsonProperty("image_key")
        private String imageKey;

        @Override
        public String getTag() {
            return "img";
        }
    }

    /**
     * media：视频标签
     */
    @Data
    public static class MediaTag implements Tag {
        /**
         * 视频地址
         */
        @JsonProperty("file_key")
        private String fileKey;
        /**
         * 视频封面
         */
        @JsonProperty("image_key")
        private String imageKey;

        @Override
        public String getTag() {
            return "media";
        }
    }

    /**
     * emotion：表情标签
     */
    @Data
    public static class EmotionTag implements Tag {
        /**
         * 表情id
         */
        @JsonProperty("emoji_type")
        private String emojiType;

        @Override
        public String getTag() {
            return "emotion";
        }
    }

    /**
     * code_block：代码块标签
     */
    @Data
    public static class CodeTag implements Tag {
        /**
         * 代码语言
         */
        private String language;
        /**
         * 代码内容
         */
        private String text;

        @Override
        public String getTag() {
            return "code";
        }
    }

    /**
     * hr：分割线标签
     */
    @Data
    public static class HrTag implements Tag {
        @Override
        public String getTag() {
            return "hr";
        }
    }

    /**
     * md：Markdown 标签
     */
    @Data
    public static class MdTag implements Tag {
        /**
         * Markdown 内容
         */
        private String text;

        @Override
        public String getTag() {
            return "md";
        }
    }

}
