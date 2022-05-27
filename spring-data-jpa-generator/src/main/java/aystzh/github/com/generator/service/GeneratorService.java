package aystzh.github.com.generator.service;


import aystzh.github.com.generator.vo.ColumnInfo;
import aystzh.github.com.generator.vo.GenConfigInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zhanghuan on 2022/5/28.
 */
public interface GeneratorService {
    /**
     * 得到数据表的元数据
     *
     * @param name 表名
     * @return /
     */
    List<ColumnInfo> getColumns(String name);

    /**
     * 代码生成
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     */
    void generator(GenConfigInfo genConfig, List<ColumnInfo> columns);


    /**
     * 打包下载
     *
     * @param genConfig 配置信息
     * @param columns   字段信息
     * @param request   /
     * @param response  /
     */
    void download(GenConfigInfo genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response);


}
