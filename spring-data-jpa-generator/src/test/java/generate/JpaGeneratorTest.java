package generate;

import aystzh.github.com.jpa.generator.GeneratorApplication;
import aystzh.github.com.jpa.generator.service.GeneratorService;
import aystzh.github.com.jpa.generator.vo.ColumnInfo;
import aystzh.github.com.jpa.generator.vo.GenConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Created by zhanghuan on 2022/5/28.
 */
@SpringBootTest(classes = GeneratorApplication.class)
@Slf4j
public class JpaGeneratorTest {

    @Autowired
    private GeneratorService generatorService;

    /**
     * 代码生成测试  目前仅仅适配了mysql数据库 其他数据库需要开发
     */
    @Test
    public void generateCode() {
        String tableName = "mnt_app";
        String absolutePath = "/Users/zhanghuan/bmsmart/smart-components/component-search";
        String packageName = "cn.com.bmsmart";
        //modelName是某个业务模块的简化名称 如 system | search | content | ...
        String modelName = "";
        String packName = String.format("%s%s", packageName, modelName);
        List<ColumnInfo> columns = generatorService.getColumns(tableName);
        GenConfigInfo genConfigInfo = new GenConfigInfo();
        genConfigInfo.setTableName(tableName);
        genConfigInfo.setApiAlias("手机端表信息");
        genConfigInfo.setAuthor("aystzh");
        genConfigInfo.setCover(Boolean.TRUE);
        genConfigInfo.setModuleName("component-search-manage");
        genConfigInfo.setPack(packName);
        genConfigInfo.setAbsolutePath(absolutePath);
        //genConfigInfo.setPrefix("");是否去除表前缀
        generatorService.generator(genConfigInfo, columns);
        //文件下载
       /* HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        generatorService.download(genConfigInfo, columns, request, response);*/
    }

}
