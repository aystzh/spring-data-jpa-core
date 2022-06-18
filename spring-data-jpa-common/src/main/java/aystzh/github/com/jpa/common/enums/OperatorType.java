package aystzh.github.com.jpa.common.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 运算符类型
 * Created by zhanghuan on 2022/5/8.
 */
public enum OperatorType {
    ALL(-1, "全部"),

    EQ(0, "等于"),

    LE(1, "小于等于"),
    GE(2, "大于等于"),

    LT(3, "小于"),
    GT(4, "大于"),

    BETWEEN(5, "BETWEEN"),

    IN(6, "IN"),

    LIKE(7, "LIKE"),
    NOTIN(8, "NOT IN"),
    NOEQ(9, "NOT EQ"),
    NOTNULL(10, "NOT NULL"),
    ISNULL(11, "IS NULL"),
    OR(12, "OR"),
    AND(13, "AND"),
    LIKE_PREFIX(14, "LIKE_PRE"),
    LIKE_SUBFIX(15, "LIKE_SUB"),
    LIKES(16, "LIKES"),

    MULTI_MATCH(17, "MULTI_MATCH"),
    CASCADE(18, "CASCADE");

    private static Logger logger = LoggerFactory.getLogger(OperatorType.class);

    private static final Object LOCK = new Object();

    private static Map<Integer, OperatorType> map;
    private static List<OperatorType> list;
    private static List<OperatorType> allList;

    // 单目运算符
    private static Set<OperatorType> unarySet;
    // 双目运算符
    private static Set<OperatorType> binarySet;
    // 集合运算符
    private static Set<OperatorType> collectionSet;
    // 多字段匹配
    private static Set<OperatorType> multiMatchSet;

    static {
        synchronized (LOCK) {
            Map<Integer, OperatorType> map = new HashMap<>();
            List<OperatorType> list = new ArrayList<>();
            List<OperatorType> listAll = new ArrayList<>();
            for (OperatorType type : OperatorType.values()) {
                map.put(type.getValue(), type);
                listAll.add(type);
                if (!type.equals(ALL)) {
                    list.add(type);
                }
            }

            OperatorType.map = ImmutableMap.copyOf(map);
            OperatorType.list = ImmutableList.copyOf(list);
            allList = ImmutableList.copyOf(listAll);

            unarySet = Sets.newHashSet(EQ, LE, GE, LT, GT, LIKE, NOEQ, NOTNULL, ISNULL, LIKE_PREFIX, LIKE_SUBFIX);
            binarySet = Sets.newHashSet(BETWEEN);
            collectionSet = Sets.newHashSet(IN, NOTIN, OR, AND, LIKES);
            multiMatchSet = Sets.newHashSet(MULTI_MATCH);
        }
    }

    private int value;
    private String name;

    OperatorType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static OperatorType get(int value) {
        try {
            return map.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<OperatorType> list() {
        return list;
    }

    public static List<OperatorType> listAll() {
        return allList;
    }

    public static boolean isUnary(OperatorType operatorType) {
        return unarySet.contains(operatorType);
    }

    public static boolean isMultiMatch(OperatorType operatorType) {
        return multiMatchSet.contains(operatorType);
    }

    public static boolean isBinary(OperatorType operatorType) {
        return binarySet.contains(operatorType);
    }

    public static boolean isCollection(OperatorType operatorType) {
        return collectionSet.contains(operatorType);
    }
}
