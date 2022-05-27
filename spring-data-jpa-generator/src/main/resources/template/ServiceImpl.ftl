package ${package}.service.impl;

import ${package}.entities.${className};
<#if columns??>
    <#list columns as column>
        <#if column.columnKey = 'UNI'>
            <#if column_index = 1>
import me.zhengjie.exception.EntityExistException;
            </#if>
        </#if>
    </#list>
</#if>
import ${package}.repository.${className}Repository;
import ${package}.service.${className}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<#if !auto && pkColumnType = 'Long'>
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
</#if>
<#if !auto && pkColumnType = 'String'>
import cn.hutool.core.util.IdUtil;
</#if>
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
* Created by ${author} on ${date}.
**/
@Service
@Transactional
public class ${className}ServiceImpl extends AbstractBaseApiServiceImpl implements ${className}Service {

    @Resource
    private ${className}Repository ${changeClassName}Repository;

    @Override
    public ApiResponse<${className}Info> page(${className}Conditions conditions){
        ApiRequest apiRequest = ApiRequest.newInstance();
        ApiRequestPage apiRequestPage = ApiRequestPage.newInstance().paging(conditions.getPage(), conditions.getSize()).addOrder(Q${className}.createDate);
        Page<${className}> all = ${changeClassName}Repository.findAll(convertSpecification(apiRequest), convertPageable(apiRequestPage));
        return convertApiResponse(all, ${className}Info.class);
    }

    @Override
    public List<${className}Info> findAll(${className}Conditions conditions){
        ApiRequest apiRequest = ApiRequest.newInstance();
        List<${className}> all = ${changeClassName}Repository.findAll(convertSpecification(apiRequest));
        return BeanMapping.mapList(all, ${className}Info.class);
    }

    private ${className} findOneForUpdate(String id){
        ${className} ${changeClassName} = ${changeClassName}Repository.findOneForUpdate(id);
        if (Objects.nonNull(${changeClassName})) {
            throw new BaseAppException(ResponseCode.FAIL.name());
        }
        return ${changeClassName};
    }
    @Override
    @Transactional
    public ${className}Info findById(${pkColumnType} ${pkChangeColName}) {
        ${className} entity = ${changeClassName}Repository.findById(id).orElseGet(${className}::new);
        return BeanMapping.map(entity, ${className}Info.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ${className}Info save(${className}Info resources) {
        ${className} entity = BeanMapping.map(resources, ${className}.class);
        return BeanMapping.map((${changeClassName}Repository.save(entity)), ${className}Info.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(${className}Info resources) {
        String id = resources.getId();
        ${className} entity = findOneForUpdate(id);
        copyUpdatableField(entity, resources);
    }

    @Override
    public void deleteAll(${pkColumnType}[] ids) {
        ${changeClassName}Repository.deleteAllById(Arrays.asList(ids));
    }
}