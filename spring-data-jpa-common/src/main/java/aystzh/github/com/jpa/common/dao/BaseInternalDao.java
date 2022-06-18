package aystzh.github.com.jpa.common.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * 用于实现自定义实现的预置方法
 * Created by zhanghuan on 2022/5/9.
 */
@NoRepositoryBean
public interface BaseInternalDao<T, I extends Serializable> extends Repository<T, I> {
    /**
     * 锁行读写的方式通过ID获取对象
     *
     * @param id 对象ID
     * @return 对应的对象
     */
    T findOneForUpdate(I id);
}
