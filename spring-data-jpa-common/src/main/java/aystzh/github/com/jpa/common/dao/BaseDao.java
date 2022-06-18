package aystzh.github.com.jpa.common.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 通用的JPA方法声明
 * Created by zhanghuan on 2022/5/9.
 */
@NoRepositoryBean
public interface BaseDao<T, I extends Serializable> extends BaseInternalDao<T, I>, JpaSpecificationExecutor<T> {
    <S extends T> S save(S s);

    void flush();

    <S extends T> S saveAndFlush(S s);

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort
     * @return all entities sorted by the given options
     */
    Iterable<T> findAll(Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    Page<T> findAll(Pageable pageable);
}
