package ${package}.repository;

import ${package}.entities.${className};
import org.springframework.data.jpa.repository.JpaRepository;

/**
* Created by ${author} on ${date}.
**/
public interface ${className}Repository extends BaseDao<${className}, ${pkColumnType}>,JpaRepository<${className}, ${pkColumnType}> {
<#if columns??>
    <#list columns as column>
        <#if column.columnKey = 'UNI'>
    /**
    * 根据 ${column.capitalColumnName} 查询
    * @param ${column.columnName} /
    * @return /
    */
    ${className} findBy${column.capitalColumnName}(${column.columnType} ${column.columnName});
        </#if>
    </#list>
</#if>
}