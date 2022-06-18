package aystzh.github.com.jpa.common.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghuan on 2022/5/8.
 */
public enum PageOrderType {
    ALL(-1, "全部"),

    ASC(0, "升序"),
    DESC(1, "降序"),
    ;

    private static Logger logger = LoggerFactory.getLogger(PageOrderType.class);

    private static final Object LOCK = new Object();

    private static Map<Integer, PageOrderType> map;
    private static List<PageOrderType> list;
    private static List<PageOrderType> allList;

    static {
        synchronized (LOCK) {
            Map<Integer, PageOrderType> map = new HashMap<>();
            List<PageOrderType> list = new ArrayList<>();
            List<PageOrderType> listAll = new ArrayList<>();
            for (PageOrderType type : PageOrderType.values()) {
                map.put(type.getValue(), type);
                listAll.add(type);
                if (!type.equals(ALL)) {
                    list.add(type);
                }
            }

            map = ImmutableMap.copyOf(map);
            PageOrderType.list = ImmutableList.copyOf(list);
            allList = ImmutableList.copyOf(listAll);
        }
    }

    private int value;
    private String name;

    PageOrderType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static PageOrderType get(int value) {
        try {
            return map.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<PageOrderType> list() {
        return list;
    }

    public static List<PageOrderType> listAll() {
        return allList;
    }
}
