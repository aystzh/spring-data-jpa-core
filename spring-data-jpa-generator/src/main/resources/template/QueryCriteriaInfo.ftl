package ${package}.service.dto;

/**
* Created by ${author} on ${date}.
**/
public final class Q${className}{
<#if columns??>
    <#list columns as column>

    public static String ${column.changeColumnName} = "${column.changeColumnName}";

    </#list>
</#if>


}