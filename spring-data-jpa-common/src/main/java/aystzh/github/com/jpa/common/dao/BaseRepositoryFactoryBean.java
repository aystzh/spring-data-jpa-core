package aystzh.github.com.jpa.common.dao;

import aystzh.github.com.jpa.common.dao.impl.BaseInternalDaoImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 自定义Spring JPA的FactoryBean
 * Created by zhanghuan on 2022/5/9.
 */
public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T,
        I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {


    public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new BaseRepositoryFactory(em);
    }

    private static class BaseRepositoryFactory<T, I extends Serializable>
            extends JpaRepositoryFactory {

        private final EntityManager em;

        BaseRepositoryFactory(EntityManager em) {
            super(em);
            this.em = em;
        }

    /*
        protected Object getTargetRepository(RepositoryMetadata metadata) {
            return new BaseInternalDaoImpl<>((Class<T>) metadata.getDomainType(), em);
        }
*/
        @Override
        protected JpaRepositoryImplementation getTargetRepository(RepositoryInformation information, EntityManager em) {

            return new BaseInternalDaoImpl<>((Class<T>) information.getDomainType(), em);
        }

        @Override
         protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseInternalDaoImpl.class;
        }
    }

}
