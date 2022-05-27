package ${package}.service;

import ${package}.service.dto.${className}Info;
import java.util.List;

/**
* Created by ${author} on ${date}.
**/
public interface ${className}Service {

    /**
    * 查询数据分页
    * @param conditions 条件
    * @return Map<String,Object>
    */
    ApiResponse<${className}Info> page(${className}Conditions conditions);

    /**
    * 查询所有数据不分页
    * @param conditions 条件参数
    * @return List<${className}Dto>
    */
    List<${className}Info> findAll(${className}Conditions conditions);

    /**
     * 根据ID查询
     * @param ${pkChangeColName} ID
     * @return ${className}Info
     */
    ${className}Info findById(${pkColumnType} ${pkChangeColName});

    /**
    * 创建
    * @param resources /
    * @return ${className}Info
    */
    ${className}Info save(${className}Info resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(${className}Info resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(${pkColumnType}[] ids);
}