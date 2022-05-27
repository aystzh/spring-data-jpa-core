package generate;

import aystzh.github.com.generator.GeneratorApplication;
import aystzh.github.com.generator.service.GeneratorService;
import aystzh.github.com.generator.vo.ColumnInfo;
import aystzh.github.com.generator.vo.GenConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by zhanghuan on 2022/5/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneratorApplication.class)
@Slf4j
public class JpaGeneratorTest {

    @Autowired
    private GeneratorService generatorService;

    @Test
    public void generateCode() {
        String tableName = "mnt_app";
        String absolutePath = "/Users/zhanghuan/bmsmart/smart-components/component-search";
        List<ColumnInfo> columns = generatorService.getColumns(tableName);
        GenConfigInfo genConfigInfo = new GenConfigInfo();
        genConfigInfo.setTableName(tableName);
        genConfigInfo.setApiAlias("手机端表信息");
        genConfigInfo.setAuthor("aystzh");
        genConfigInfo.setCover(Boolean.TRUE);
        genConfigInfo.setModuleName("component-search-manage");
        genConfigInfo.setPack("cn.com.bmsmart");
        genConfigInfo.setAbsolutePath(absolutePath);
        //genConfigInfo.setPrefix("");是否去除表前缀
        generatorService.generator(genConfigInfo, columns);
    }
}
