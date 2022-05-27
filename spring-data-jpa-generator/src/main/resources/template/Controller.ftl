package ${package}.controller;

import me.zhengjie.annotation.Log;
import ${package}.domain.${className};
import ${package}.service.${className}Service;
import ${package}.service.dto.${className}QueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
* Created by ${author} on ${date}.
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "${apiAlias}管理")
@RequestMapping("/api/${changeClassName}")
public class ${className}Controller {

    @Autowired
    private ${className}Service ${changeClassName}Service;

    @PostMapping("/list")
    @ApiOperation("查询${apiAlias}")
    public JsonResult<List<${className}Info>> list(@RequestBody ${className}Conditions conditions){
        List<${className}Info> list = ${changeClassName}Service.findAll(conditions);
        return JsonResult.<List<${className}Info>>builder()
            .status(ResponseCode.SUCCESS.getCode())
            .message(ResponseCode.SUCCESS.getStatus())
            .data(list)
            .build();
    }

    @PostMapping("/page")
    @ApiOperation("查询${apiAlias}")
    public JsonResult<ApiResponse<${className}Info>> page(@RequestBody ${className}Conditions conditions){
        ApiResponse<${className}Info> pageResponse = ${changeClassName}Service.page(conditions);
        return JsonResult.<ApiResponse<${className}Info>>builder()
            .status(ResponseCode.SUCCESS.getCode())
            .message(ResponseCode.SUCCESS.getStatus())
            .data(pageResponse)
            .build();
    }

    @PostMapping
    @ApiOperation("新增${apiAlias}")
    public JsonResult create(@Validated @RequestBody ${className}Info resources){
        ${className}Info info = ${changeClassName}Service.save(resources);
        return JsonResult.builder()
            .status(ResponseCode.SUCCESS.getCode())
            .message(ResponseCode.SUCCESS.getStatus())
            .data(info)
            .build();
    }

    @PutMapping
    @ApiOperation("修改${apiAlias}")
    public JsonResult update(@Validated @RequestBody ${className}Info resources){
        ${changeClassName}Service.update(resources);
        return JsonResult.builder()
            .status(ResponseCode.SUCCESS.getCode())
            .message(ResponseCode.SUCCESS.getStatus())
            .build();
    }

    @ApiOperation("删除${apiAlias}")
    @DeleteMapping
    public JsonResult<Object> delete(@RequestBody ${pkColumnType}[] ids) {
        ${changeClassName}Service.deleteAll(ids);
        return JsonResult.builder()
            .status(ResponseCode.SUCCESS.getCode())
            .message(ResponseCode.SUCCESS.getStatus())
            .build();
    }
}