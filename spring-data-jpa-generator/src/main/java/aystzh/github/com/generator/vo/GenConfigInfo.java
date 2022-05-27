package aystzh.github.com.generator.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 代码生成配置
 * Created by zhanghuan on 2022/5/28.
 */
@Getter
@Setter
@NoArgsConstructor
public class GenConfigInfo implements Serializable {

    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 接口名称 用于swagger接口文档声明
     */
    private String apiAlias;

    /**
     * 包路径
     */
    private String pack;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 作者
     */
    private String author;

    /**
     * 是否去除表前缀
     */
    private String prefix;

    /**
     * 是否覆盖
     */
    private Boolean cover = false;

    /**
     * 生成文件绝对路径  不填默认取系统相对路径
     */
    private String absolutePath;
}
