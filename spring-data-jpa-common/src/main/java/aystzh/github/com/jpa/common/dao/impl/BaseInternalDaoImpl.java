package aystzh.github.com.jpa.common.dao.impl;

import aystzh.github.com.jpa.common.dao.BaseInternalDao;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;

/**
 * 实现一些通用的自定义DAO操作方法
 * Created by zhanghuan on 2022/5/8.
 */
public class BaseInternalDaoImpl<T, I extends Serializable> extends SimpleJpaRepository<T, I> implements BaseInternalDao<T, I> {

    private final EntityManager entityManager;


    public BaseInternalDaoImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);

        this.entityManager = entityManager;
    }

    @Override
    public T findOneForUpdate(I id) {
        return entityManager.find(this.getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
    }
}
