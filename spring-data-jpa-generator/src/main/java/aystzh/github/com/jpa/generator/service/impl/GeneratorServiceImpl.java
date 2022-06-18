package aystzh.github.com.jpa.generator.service.impl;

import aystzh.github.com.jpa.generator.service.GeneratorService;
import aystzh.github.com.jpa.generator.tools.FileUtil;
import aystzh.github.com.jpa.generator.utils.GenUtil;
import aystzh.github.com.jpa.generator.vo.ColumnInfo;
import aystzh.github.com.jpa.generator.vo.GenConfigInfo;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghuan on 2022/5/28.
 */
@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {
    private static final Logger log = LoggerFactory.getLogger(GeneratorServiceImpl.class);
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        // 使用预编译防止sql注入
        String sql = "select column_name, is_nullable, data_type, column_comment, column_key, extra from information_schema.columns " +
                "where table_name = ? and table_schema = (select database()) order by ordinal_position";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, tableName);
        List result = query.getResultList();
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            columnInfos.add(
                    new ColumnInfo(
                            tableName,
                            arr[0].toString(),
                            "NO".equals(arr[1]),
                            arr[2].toString(),
                            ObjectUtil.isNotNull(arr[3]) ? arr[3].toString() : null,
                            ObjectUtil.isNotNull(arr[4]) ? arr[4].toString() : null,
                            ObjectUtil.isNotNull(arr[5]) ? arr[5].toString() : null)
            );
        }
        return columnInfos;
    }


    @Override
    public void generator(GenConfigInfo genConfig, List<ColumnInfo> columns) {
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("生成失败，请手动处理已生成的文件");
        }
    }

    @Override
    public void download(GenConfigInfo genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response) {
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath() + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new RuntimeException("打包失败");
        }
    }
}
