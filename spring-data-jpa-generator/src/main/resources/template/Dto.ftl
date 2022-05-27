package ${package}.service.dto;

import lombok.Data;
<#if hasTimestamp>
import java.sql.Timestamp;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
import java.io.Serializable;
<#if !auto && pkColumnType = 'Long'>
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
</#if>

/**
* Created by ${author} on ${date}.
**/
@Data
public class ${className}Info extends AbstractBaseApiBean {
<#if columns??>
    <#list columns as column>

    <#if column.remark != ''>
    /** ${column.remark} */
    </#if>
    <#if column.columnKey = 'PRI'>
    <#if !auto && pkColumnType = 'Long'>
    /** 防止精度丢失 */
    @JsonSerialize(using= ToStringSerializer.class)
    </#if>
    private ${column.columnType} ${column.changeColumnName};
    </#if>
    <#if column.columnKey != 'PRI'>
     @RequestApiFieldUpdatable
     private ${column.columnType} ${column.changeColumnName};
    </#if>
    </#list>
</#if>
}