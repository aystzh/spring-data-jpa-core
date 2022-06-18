package aystzh.github.com.jpa.common.request;

import aystzh.github.com.jpa.common.enums.OperatorType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装API请求对象供自定义查询使用
 * 支持自定义字段的各种运算比较
 * Created by zhanghuan on 2022/5/8.
 */
public class ApiRequest implements Serializable {

    private static final long serialVersionUID = -5782205017337866984L;

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<ApiRequestFilter> filterList;

    private ApiRequest() {

    }

    public static ApiRequest newInstance() {
        return new ApiRequest();
    }

    public ApiRequest cascadeParent(String parentName, ApiRequest apiRequest) {

        cascade(true, parentName, apiRequest);
        return this;
    }

    public ApiRequest cascadeChild(String childName, ApiRequest apiRequest) {
        cascade(false, childName, apiRequest);
        return this;
    }

    private ApiRequest cascade(boolean cascadeParent, String parentName, ApiRequest apiRequest) {
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        filterList.add(new ApiRequestFilter(OperatorType.CASCADE, cascadeParent, parentName, apiRequest));
        return this;
    }


    public ApiRequest filter(OperatorType operator, String field, Object... values) {
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        try {
            if (values.length == 0) {
                // TODO 未来可能会支持不需要参数的运算符
                throw new IllegalArgumentException("参数个数为0");
            }
            if (OperatorType.isUnary(operator)) {
                // 单目运算符
                if (values.length > 1) {
                    throw new IllegalArgumentException("单目运算符参数个数超过1个");
                }
                filterList.add(new ApiRequestFilter(operator, field, values[0]));
            } else if (OperatorType.isBinary(operator)) {
                if (values.length != 2) {
                    throw new IllegalArgumentException("双目运算符参数个数不为1个");
                }
                filterList.add(new ApiRequestFilter(operator, field, Lists.newArrayList(values)));
            } else if (OperatorType.isCollection(operator)) {
                filterList.add(new ApiRequestFilter(operator, field, Lists.newArrayList(values)));
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return this;
    }

    public ApiRequest filterEqual(String field, Object value) {
        return this.filter(OperatorType.EQ, field, value);
    }

    public ApiRequest filterNotEqual(String field, Object value) {
        return this.filter(OperatorType.NOEQ, field, value);
    }

    public ApiRequest filterNotNull(String field) {
        return this.filter(OperatorType.NOTNULL, field, "");
    }


    public ApiRequest filterLessEqual(String field, Object value) {
        return this.filter(OperatorType.LE, field, value);
    }

    public ApiRequest filterGreaterEqual(String field, Object value) {
        return this.filter(OperatorType.GE, field, value);
    }

    public ApiRequest filterLessThan(String field, Object value) {
        return this.filter(OperatorType.LT, field, value);
    }

    public ApiRequest filterGreaterThan(String field, Object value) {
        return this.filter(OperatorType.GT, field, value);
    }

    public ApiRequest filterBetween(String field, Object low, Object high) {
        if (low != null && high != null) {
            return this.filter(OperatorType.BETWEEN, field, low, high);
        }
        if (low == null && high == null) {
            return this;
        } else if (low == null) {
            return this.filterLessEqual(field, high);
        } else {
            return this.filterGreaterEqual(field, low);
        }
    }

    public ApiRequest filterIn(String field, Iterable<? extends Object> valueList) {
        return this.filter(OperatorType.IN, field, Iterables.toArray(valueList, Object.class));
    }

    public ApiRequest filterLikes(String field, Iterable<? extends String> valueList) {
        return this.filter(OperatorType.LIKES, field, Iterables.toArray(valueList, Object.class));
    }

    public ApiRequest filterNotIn(String field, Iterable<? extends Object> valueList) {
        return this.filter(OperatorType.NOTIN, field, Iterables.toArray(valueList, Object.class));
    }


    public ApiRequest filterLike(String field, Object value) {
        return this.filter(OperatorType.LIKE, field, value);
    }

    public ApiRequest filterLikePrefix(String field, Object value) {
        return this.filter(OperatorType.LIKE_PREFIX, field, value);
    }

    public ApiRequest filterLikeSubfix(String field, Object value) {
        return this.filter(OperatorType.LIKE_SUBFIX, field, value);
    }

    public ApiRequest filterOr(ApiRequestFilter... orConditions) {
        ApiRequestFilter filter = new ApiRequestFilter(OperatorType.OR, "", Lists.newArrayList(orConditions));
        if (filterList == null) {
            filterList = Lists.newArrayList();
        }
        this.filterList.add(filter);
        return this;
    }

    public ApiRequest filterMultiMatch(List<String> fields, Object value) {
        ApiRequestFilter filter = new ApiRequestFilter(OperatorType.MULTI_MATCH, fields, value);
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        this.filterList.add(filter);
        return this;
    }

    public ApiRequestFilter condition(OperatorType operator, String field, Object... values) {
        try {
            if (values.length == 0) {
                // TODO 未来可能会支持不需要参数的运算符
                throw new IllegalArgumentException("参数个数为0");
            }
            if (OperatorType.isUnary(operator)) {
                // 单目运算符
                if (values.length > 1) {
                    throw new IllegalArgumentException("单目运算符参数个数超过1个");
                }
                return new ApiRequestFilter(operator, field, values[0]);
            } else if (OperatorType.isBinary(operator)) {
                if (values.length != 2) {
                    throw new IllegalArgumentException("双目运算符参数个数不为1个");
                }
                return new ApiRequestFilter(operator, field, Lists.newArrayList(values));
            } else if (OperatorType.isCollection(operator)) {
                return new ApiRequestFilter(operator, field, Lists.newArrayList(values));
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return null;
    }

    public ApiRequestFilter conditionEqual(String field, Object value) {
        return this.condition(OperatorType.EQ, field, value);
    }

    public ApiRequestFilter conditionNotEqual(String field, Object value) {
        return this.condition(OperatorType.NOEQ, field, value);
    }

    public ApiRequestFilter conditionNotNull(String field) {
        return this.condition(OperatorType.NOTNULL, field, "");
    }

    public ApiRequestFilter conditionLessEqual(String field, Object value) {
        return this.condition(OperatorType.LE, field, value);
    }

    public ApiRequestFilter conditionGreaterEqual(String field, Object value) {
        return this.condition(OperatorType.GE, field, value);
    }

    public ApiRequestFilter conditionLessThan(String field, Object value) {
        return this.condition(OperatorType.LT, field, value);
    }

    public ApiRequestFilter conditionGreaterThan(String field, Object value) {
        return this.condition(OperatorType.GT, field, value);
    }

    public ApiRequestFilter conditionBetween(String field, Object low, Object high) {
        return this.condition(OperatorType.BETWEEN, field, low, high);
    }

    public ApiRequestFilter conditionIn(String field, Iterable<? extends Object> valueList) {
        return this.condition(OperatorType.IN, field, Iterables.toArray(valueList, Object.class));
    }

    public ApiRequestFilter conditionNotIn(String field, Iterable<? extends Object> valueList) {
        return this.condition(OperatorType.NOTIN, field, Iterables.toArray(valueList, Object.class));
    }

    public ApiRequestFilter conditionLike(String field, Object value) {
        return this.condition(OperatorType.LIKE, field, value);
    }

    public ApiRequestFilter conditionIsNull(String field) {
        return this.condition(OperatorType.ISNULL, field, "");
    }

    public ApiRequestFilter conditionAnd(ApiRequestFilter... conditions) {
        return this.condition(OperatorType.AND, "", (Object[]) conditions);
    }

    public ApiRequestFilter conditionOr(ApiRequestFilter... orConditions) {
        return new ApiRequestFilter(OperatorType.OR, "", orConditions);
    }

    public List<ApiRequestFilter> getFilterList() {
        if (filterList == null) {
            return null;
        }
        return ImmutableList.copyOf(filterList);
    }

}