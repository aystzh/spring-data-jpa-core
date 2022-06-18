package aystzh.github.com.jpa.common.service.impl;

import aystzh.github.com.jpa.common.annotations.RequestApiFieldUpdatable;
import aystzh.github.com.jpa.common.bean.BaseApiBean;
import aystzh.github.com.jpa.common.bean.BaseEntity;
import aystzh.github.com.jpa.common.enums.PageOrderType;
import aystzh.github.com.jpa.common.request.ApiRequest;
import aystzh.github.com.jpa.common.request.ApiRequestFilter;
import aystzh.github.com.jpa.common.request.ApiRequestOrder;
import aystzh.github.com.jpa.common.request.ApiRequestPage;
import aystzh.github.com.jpa.common.response.ApiResponse;
import aystzh.github.com.jpa.common.service.BaseApiService;
import aystzh.github.com.jpa.common.utils.BeanMapping;
import aystzh.github.com.jpa.common.utils.ReflectionUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 预留基础服务实现, 为所有服务实现的父类
 * Created by zhanghuan on 2022/5/8.
 */
public abstract class AbstractBaseApiServiceImpl implements BaseApiService {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Sort convertSort(ApiRequestPage requestPage) {
        if (requestPage.getOrderList() != null && !requestPage.getOrderList().isEmpty()) {
            List<Sort.Order> orderList = new ArrayList<>();
            for (ApiRequestOrder requestOrder : requestPage.getOrderList()) {
                orderList.add(this.convertSortOrder(requestOrder));
            }
            return Sort.by(orderList);
        }
        return null;
    }

    private Sort.Order convertSortOrder(ApiRequestOrder requestOrder) {
        Sort.Direction direction;
        if (requestOrder.getOrderType().equals(PageOrderType.DESC)) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }
        return new Sort.Order(direction, requestOrder.getField());
    }

    protected Pageable convertPageable(ApiRequestPage requestPage) {
        return PageRequest.of(requestPage.getPage(), requestPage.getPageSize(), this.convertSort(requestPage));
    }

    protected <T> Specification<T> convertSpecification(ApiRequest request) {
        if (request == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (request.getFilterList() != null && !request.getFilterList().isEmpty()) {
                List<Predicate> predicateList = new ArrayList<>();
                for (ApiRequestFilter filter : request.getFilterList()) {
                    switch (filter.getOperatorType()) {
                        case EQ:
                            predicateList.add(cb.equal(root.get(filter.getField()), filter.getValue()));
                            break;
                        case NOEQ:
                            predicateList.add(cb.notEqual(root.get(filter.getField()), filter.getValue()));
                            break;
                        case GE:
                            if (filter.getValue() instanceof Comparable) {
                                predicateList.add(cb.greaterThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue()));
                            } else {
                                logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                            }
                            break;
                        case LE:
                            if (filter.getValue() instanceof Comparable) {
                                predicateList.add(cb.lessThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue()));
                            } else {
                                logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                            }
                            break;
                        case GT:
                            if (filter.getValue() instanceof Comparable) {
                                predicateList.add(cb.greaterThan(root.get(filter.getField()), (Comparable) filter.getValue()));
                            } else {
                                logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                            }
                            break;
                        case LT:
                            if (filter.getValue() instanceof Comparable) {
                                predicateList.add(cb.lessThan(root.get(filter.getField()), (Comparable) filter.getValue()));
                            } else {
                                logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                            }
                            break;
                        case BETWEEN:
                            Object val1 = filter.getValueList().get(0);
                            Object val2 = filter.getValueList().get(1);
                            if (val1 instanceof Comparable && val2 instanceof Comparable) {
                                predicateList.add(cb.between(root.get(filter.getField()), (Comparable) val1, (Comparable) val2));
                            } else {
                                logger.error("字段({})不是可比较对象, value1={}, value2={}", filter.getField(), val1, val2);
                            }
                            break;
                        case IN:
                            predicateList.add(root.get(filter.getField()).in(filter.getValueList()));
                            break;
                        case NOTIN:
                            Iterator iterator = filter.getValueList().iterator();
                            In in = cb.in(root.get(filter.getField()));
                            while (iterator.hasNext()) {
                                in.value(iterator.next());
                            }
                            predicateList.add(cb.not(in));
                            break;
                        case LIKE:
                            predicateList.add(cb.like(root.get(filter.getField()), "%" + filter.getValue() + "%"));
                            break;
                        case LIKE_PREFIX:
                            predicateList.add(cb.like(root.get(filter.getField()), filter.getValue() + "%"));
                            break;
                        case LIKE_SUBFIX:
                            predicateList.add(cb.like(root.get(filter.getField()), "%" + filter.getValue()));
                            break;
                        case NOTNULL:
                            predicateList.add(cb.isNotEmpty(root.get(filter.getField())));
                            break;
                        case OR:
                            List<Predicate> orPredicate = Lists.newArrayList();
                            for (Object filterObject : filter.getValueList()) {
                                ApiRequestFilter requestFilter = (ApiRequestFilter) filterObject;
                                Predicate predicate = createPredicate(root, cb, requestFilter);
                                if (predicate != null) {
                                    orPredicate.add(predicate);
                                }
                            }
                            if (orPredicate.size() != 0) {
                                predicateList.add(cb.or(orPredicate.toArray(new Predicate[orPredicate.size()])));
                            }
                            break;
                        default:
                            logger.error("不支持的运算符, op={}", filter.getOperatorType());
                    }
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
            return null;
        };
    }

    private <T> Predicate createPredicate(Root<T> root, CriteriaBuilder cb, ApiRequestFilter filter) {
        switch (filter.getOperatorType()) {
            case EQ:
                return cb.equal(root.get(filter.getField()), filter.getValue());
            case NOEQ:
                return cb.notEqual(root.get(filter.getField()), filter.getValue());
            case GE:
                if (filter.getValue() instanceof Comparable) {
                    return cb.greaterThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
                break;
            case LE:
                if (filter.getValue() instanceof Comparable) {
                    return cb.lessThanOrEqualTo(root.get(filter.getField()), (Comparable) filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
                break;
            case GT:
                if (filter.getValue() instanceof Comparable) {
                    return cb.greaterThan(root.get(filter.getField()), (Comparable) filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
                break;
            case LT:
                if (filter.getValue() instanceof Comparable) {
                    return cb.lessThan(root.get(filter.getField()), (Comparable) filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
                break;
            case BETWEEN:
                Object val1 = filter.getValueList().get(0);
                Object val2 = filter.getValueList().get(1);
                if (val1 instanceof Comparable && val2 instanceof Comparable) {
                    return cb.between(root.get(filter.getField()), (Comparable) val1, (Comparable) val2);
                } else {
                    logger.error("字段({})不是可比较对象, value1={}, value2={}", filter.getField(), val1, val2);
                }
                break;
            case IN:
                return root.get(filter.getField()).in(filter.getValueList());
            case NOTIN:
                Iterator iterator = filter.getValueList().iterator();
                In in = cb.in(root.get(filter.getField()));
                while (iterator.hasNext()) {
                    in.value(iterator.next());
                }
                return cb.not(in);
            case LIKE:
                return cb.like(root.get(filter.getField()), "%" + filter.getValue() + "%");
            case NOTNULL:
                return cb.isNotEmpty(root.get(filter.getField()));
            default:
                logger.error("不支持的运算符, op={}", filter.getOperatorType());
        }
        return null;
    }

    protected <T, E> ApiResponse<E> convertApiResponse(Page<T> page, Class<E> c) {
        ApiResponse<E> apiResponse = new ApiResponse<>();
        apiResponse.setCurrentPage(page.getNumber());
        apiResponse.setPageSize(page.getSize());
        apiResponse.setTotal(page.getTotalElements());
        apiResponse.setResults(BeanMapping.mapList(page.getContent(), c));

        return apiResponse;
    }

    private static LoadingCache<Class<? extends BaseApiBean>, Set<String>> apiBeanUpdatableFieldCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends BaseApiBean>, Set<String>>() {
        @Override
        public Set<String> load(Class<? extends BaseApiBean> c) throws Exception {
            return ReflectionUtils.getFieldsAnnotatedWith(c, RequestApiFieldUpdatable.class).keySet().stream().map(Field::getName).collect(Collectors.toSet());
        }
    });

    protected <T extends BaseApiBean> boolean isUpdatableField(Class<T> c, String fieldName) {
        try {
            return apiBeanUpdatableFieldCache.get(c).contains(fieldName);
        } catch (Exception e) {
            logger.error("从缓存中载入可更新字段集合出错", e);
            logger.error("要判断的类为: {}, 字段名为: {}", c.getName(), fieldName);
            return false;
        }
    }

    protected <E extends BaseEntity, S extends BaseApiBean> E copyUpdatableField(E entity, S source) {
        Class<? extends BaseApiBean> sourceClass = source.getClass();
        try {
            for (Field field : sourceClass.getDeclaredFields()) {
                String fieldName = field.getName();
                if (!isUpdatableField(sourceClass, fieldName)) {
                    logger.info("字段({})不允许更新, 直接跳过, class={}", fieldName, sourceClass.getName());
                    continue;
                }

                Object fieldValue = PropertyUtils.getProperty(source, fieldName);
                if (fieldValue == null) {
                    continue;
                }

                BeanUtils.setProperty(entity, fieldName, fieldValue);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(String.format("类属性拷贝错误, message=%s, class=%s", e.getMessage(), sourceClass));
        }
        return entity;
    }


}
