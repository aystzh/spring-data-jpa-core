package aystzh.github.com.jpa.generator.utils;

import aystzh.github.com.jpa.generator.vo.ColumnInfo;
import aystzh.github.com.jpa.generator.vo.GenConfigInfo;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.*;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成
 * <p>
 * Created by zhanghuan on 2022/5/28.
 */
@Slf4j
@SuppressWarnings({"unchecked", "all"})
public class GenUtil {

    private static final String TIMESTAMP = "Timestamp";

    private static final String BIGDECIMAL = "BigDecimal";

    public static final String PK = "PRI";

    public static final String EXTRA = "auto_increment";

    /**
     * 获取后端代码模板名称
     *
     * @return List
     */
    private static List<String> getAdminTemplateNames() {
        List<String> templateNames = new ArrayList<>();
        templateNames.add("Entity");
        templateNames.add("Dto");
        templateNames.add("Controller");
        templateNames.add("QueryCriteriaInfo");
        templateNames.add("Service");
        templateNames.add("ServiceImpl");
        templateNames.add("Repository");
        templateNames.add("Conditions");
        return templateNames;
    }

    public static List<Map<String, Object>> preview(List<ColumnInfo> columns, GenConfigInfo GenConfigInfo) {
        Map<String, Object> genMap = getGenMap(columns, GenConfigInfo);
        List<Map<String, Object>> genList = new ArrayList<>();
        // 获取后端模版
        List<String> templates = getAdminTemplateNames();
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        for (String templateName : templates) {
            Map<String, Object> map = new HashMap<>(1);
            Template template = engine.getTemplate(templateName + ".ftl");
            map.put("content", template.render(genMap));
            map.put("name", templateName);
            genList.add(map);
        }
        return genList;
    }

    public static String download(List<ColumnInfo> columns, GenConfigInfo GenConfigInfo) throws IOException {
        // 拼接的路径：/tmpeladmin-gen-temp/，这个路径在Linux下需要root用户才有权限创建,非root用户会权限错误而失败，更改为： /tmp/eladmin-gen-temp/
        // String tempPath =SYS_TEM_DIR + "eladmin-gen-temp" + File.separator + GenConfigInfo.getTableName() + File.separator;
        String tempPath = aystzh.github.com.jpa.generator.tools.FileUtil.SYS_TEM_DIR + "eladmin-gen-temp" + File.separator + GenConfigInfo.getTableName() + File.separator;
        Map<String, Object> genMap = getGenMap(columns, GenConfigInfo);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        // 生成后端代码
        List<String> templates = getAdminTemplateNames();
        for (String templateName : templates) {
            Template template = engine.getTemplate(templateName + ".ftl");
            String filePath = getAdminFilePath(templateName, GenConfigInfo, genMap.get("className").toString(), tempPath + "eladmin" + File.separator);
            assert filePath != null;
            File file = new File(filePath);
            // 如果非覆盖生成
            if (!GenConfigInfo.getCover() && FileUtil.exist(file)) {
                continue;
            }
            // 生成代码
            genFile(file, template, genMap);
        }
        return tempPath;
    }

    public static void generatorCode(List<ColumnInfo> columnInfos, GenConfigInfo genConfigInfo) throws IOException {
        Map<String, Object> genMap = getGenMap(columnInfos, genConfigInfo);
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        // 生成后端代码
        List<String> templates = getAdminTemplateNames();
        for (String templateName : templates) {
            Template template = engine.getTemplate(templateName + ".ftl");
            String path = "";
            if (StrUtil.isNotBlank(genConfigInfo.getAbsolutePath())) {
                path = genConfigInfo.getAbsolutePath();
            } else {
                path = System.getProperty("user.dir");
            }
            String filePath = getAdminFilePath(templateName, genConfigInfo, genMap.get("className").toString(), path);

            assert filePath != null;
            System.out.println("==========filepath====" + filePath);
            File file = new File(filePath);

            // 如果非覆盖生成
            if (!genConfigInfo.getCover() && FileUtil.exist(file)) {
                continue;
            }
            // 生成代码
            genFile(file, template, genMap);
        }
    }

    // 获取模版数据
    private static Map<String, Object> getGenMap(List<ColumnInfo> columnInfos, GenConfigInfo GenConfigInfo) {
        // 存储模版字段数据
        Map<String, Object> genMap = new HashMap<>(16);
        // 接口别名
        genMap.put("apiAlias", GenConfigInfo.getApiAlias());
        // 包名称
        genMap.put("package", GenConfigInfo.getPack());
        // 模块名称
        genMap.put("moduleName", GenConfigInfo.getModuleName());
        // 作者
        genMap.put("author", GenConfigInfo.getAuthor());
        // 创建日期
        genMap.put("date", LocalDate.now().toString());
        // 表名
        genMap.put("tableName", GenConfigInfo.getTableName());
        // 大写开头的类名
        // String className = StringUtils.toCapitalizeCamelCase(GenConfigInfo.getTableName());
        String className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, GenConfigInfo.getTableName());

        // 小写开头的类名
        //String changeClassName = StringUtils.toCamelCase(GenConfigInfo.getTableName());
        String changeClassName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, GenConfigInfo.getTableName());
        // 判断是否去除表前缀
        if (StrUtil.isNotEmpty(GenConfigInfo.getPrefix())) {
            //className = StringUtils.toCapitalizeCamelCase(StrUtil.removePrefix(GenConfigInfo.getTableName(), GenConfigInfo.getPrefix()));
            className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, GenConfigInfo.getTableName());
            changeClassName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, StrUtil.removePrefix(GenConfigInfo.getTableName(), GenConfigInfo.getPrefix()));
            // changeClassName = StringUtils.toCamelCase(StrUtil.removePrefix(GenConfigInfo.getTableName(), GenConfigInfo.getPrefix()));
        }
        // 保存类名
        genMap.put("className", className);
        // 保存小写开头的类名
        genMap.put("changeClassName", changeClassName);
        // 存在 Timestamp 字段
        genMap.put("hasTimestamp", false);
        // 查询类中存在 Timestamp 字段
        genMap.put("queryHasTimestamp", false);
        // 存在 BigDecimal 字段
        genMap.put("hasBigDecimal", false);
        // 查询类中存在 BigDecimal 字段
        genMap.put("queryHasBigDecimal", false);
        // 是否需要创建查询
        genMap.put("hasQuery", false);
        // 自增主键
        genMap.put("auto", false);
        // 存在字典
        genMap.put("hasDict", false);
        // 存在日期注解
        genMap.put("hasDateAnnotation", false);
        // 保存字段信息
        List<Map<String, Object>> columns = new ArrayList<>();
        // 保存查询字段的信息
        List<Map<String, Object>> queryColumns = new ArrayList<>();
        // 存储字典信息
        List<String> dicts = new ArrayList<>();
        // 存储 between 信息
        List<Map<String, Object>> betweens = new ArrayList<>();
        // 存储不为空的字段信息
        List<Map<String, Object>> isNotNullColumns = new ArrayList<>();

        for (ColumnInfo column : columnInfos) {
            Map<String, Object> listMap = new HashMap<>(16);
            // 字段描述
            listMap.put("remark", column.getRemark());
            // 字段类型
            listMap.put("columnKey", column.getKeyType());
            // 主键类型
            String colType = ColUtil.cloToJava(column.getColumnType());
            // 小写开头的字段名
            //String changeColumnName = StringUtils.toCamelCase(column.getColumnName());
            String changeColumnName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, column.getColumnName());

            // 大写开头的字段名
            //String capitalColumnName = StringUtils.toCapitalizeCamelCase(column.getColumnName());
            String capitalColumnName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, column.getColumnName());
            if (PK.equals(column.getKeyType())) {
                // 存储主键类型
                genMap.put("pkColumnType", colType);
                // 存储小写开头的字段名
                genMap.put("pkChangeColName", changeColumnName);
                // 存储大写开头的字段名
                genMap.put("pkCapitalColName", capitalColumnName);
            }
            // 是否存在 Timestamp 类型的字段
            if (TIMESTAMP.equals(colType)) {
                genMap.put("hasTimestamp", true);
            }
            // 是否存在 BigDecimal 类型的字段
            if (BIGDECIMAL.equals(colType)) {
                genMap.put("hasBigDecimal", true);
            }
            // 主键是否自增
            if (EXTRA.equals(column.getExtra())) {
                genMap.put("auto", true);
            }
            // 主键存在字典
            if (StrUtil.isNotBlank(column.getDictName())) {
                genMap.put("hasDict", true);
                dicts.add(column.getDictName());
            }

            // 存储字段类型
            listMap.put("columnType", colType);
            // 存储字原始段名称
            listMap.put("columnName", column.getColumnName());
            // 不为空
            listMap.put("istNotNull", column.getNotNull());
            // 字段列表显示
            listMap.put("columnShow", column.getListShow());
            // 表单显示
            listMap.put("formShow", column.getFormShow());
            // 表单组件类型
            listMap.put("formType", StrUtil.isNotBlank(column.getFormType()) ? column.getFormType() : "Input");
            // 小写开头的字段名称
            listMap.put("changeColumnName", changeColumnName);
            //大写开头的字段名称
            listMap.put("capitalColumnName", capitalColumnName);
            // 字典名称
            listMap.put("dictName", column.getDictName());
            // 日期注解
            listMap.put("dateAnnotation", column.getDateAnnotation());
            if (StrUtil.isNotBlank(column.getDateAnnotation())) {
                genMap.put("hasDateAnnotation", true);
            }
            // 添加非空字段信息
            if (column.getNotNull()) {
                isNotNullColumns.add(listMap);
            }
            // 判断是否有查询，如有则把查询的字段set进columnQuery
            if (!StrUtil.isBlank(column.getQueryType())) {
                // 查询类型
                listMap.put("queryType", column.getQueryType());
                // 是否存在查询
                genMap.put("hasQuery", true);
                if (TIMESTAMP.equals(colType)) {
                    // 查询中存储 Timestamp 类型
                    genMap.put("queryHasTimestamp", true);
                }
                if (BIGDECIMAL.equals(colType)) {
                    // 查询中存储 BigDecimal 类型
                    genMap.put("queryHasBigDecimal", true);
                }
                if ("between".equalsIgnoreCase(column.getQueryType())) {
                    betweens.add(listMap);
                } else {
                    // 添加到查询列表中
                    queryColumns.add(listMap);
                }
            }
            // 添加到字段列表中
            columns.add(listMap);
        }
        // 保存字段列表
        genMap.put("columns", columns);
        // 保存查询列表
        genMap.put("queryColumns", queryColumns);
        // 保存字段列表
        genMap.put("dicts", dicts);
        // 保存查询列表
        genMap.put("betweens", betweens);
        // 保存非空字段信息
        genMap.put("isNotNullColumns", isNotNullColumns);
        return genMap;
    }

    /**
     * 定义后端文件路径以及名称
     */
    private static String getAdminFilePath(String templateName, GenConfigInfo GenConfigInfo, String className, String rootPath) {
        String projectPath = rootPath + File.separator + GenConfigInfo.getModuleName();
        String packagePath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (!ObjectUtils.isEmpty(GenConfigInfo.getPack())) {
            packagePath += GenConfigInfo.getPack().replace(".", File.separator) + File.separator;
        }

        if ("Entity".equals(templateName)) {
            return packagePath + "entities" + File.separator + className + ".java";
        }

        if ("Controller".equals(templateName)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if ("Service".equals(templateName)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if ("ServiceImpl".equals(templateName)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if ("Dto".equals(templateName)) {
            return packagePath + "service" + File.separator + "dto" + File.separator + className + "Info.java";
        }

        if ("Repository".equals(templateName)) {
            return packagePath + "repository" + File.separator + className + "Repository.java";
        }
        if ("QueryCriteriaInfo".equals(templateName)) {
            return packagePath + "service" + File.separator + "dto" + File.separator + "Q" + className + ".java";
        }
        if ("Conditions".equals(templateName)) {
            return packagePath + "service" + File.separator + "dto" + File.separator + className + "Conditions.java";
        }

        return null;
    }


    private static void genFile(File file, Template template, Map<String, Object> map) throws IOException {
        // 生成目标文件
        Writer writer = null;
        try {
            FileUtil.touch(file);
            writer = new FileWriter(file);
            template.render(map, writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert writer != null;
            writer.close();
        }
    }
}
