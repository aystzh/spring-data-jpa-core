package aystzh.github.com.jpa.common.response;

import aystzh.github.com.jpa.common.request.ApiRequestPage;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 * 封装API响应对象供自定义查询使用
 * 支持分页
 * Created by zhanghuan on 2022/5/9.
 */
public class ApiResponse<E> implements Serializable {

    private static final long serialVersionUID = 2490364023692403771L;
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /* 回写请求时的分页设置 */
    private int currentPage;
    private int pageSize;

    /* 满足条件的记录总数 */
    private long total = 0;
    private long totalElements = 0;

    /* 满足条件的总页数 */
    private long totalPage ;

    private Collection<E> results;

    public ApiResponse(int currentPage, int pageSize, Collection<E> results, long total) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.results = results;
        this.total = total;
    }

    public ApiResponse(int currentPage, int pageSize, Collection<E> results) {
        this(currentPage, pageSize, results, 0);
    }

    public ApiResponse(int currentPage, int pageSize) {
        this(currentPage, pageSize, null);
    }

    public ApiResponse(ApiRequestPage requestPage) {
        this(requestPage.getPage(), requestPage.getPageSize());
    }

    public ApiResponse() {
    }

    public int getCount() {
        if (results == null) {
            return 0;
        }

        return results.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Collection<E> getResults() {
        if (results == null) {
            results = Lists.newArrayList();
        }
        return results;
    }

    public void setResults(Collection<E> results) {
        this.results = results;
    }

    public long getTotalPage() {
        long l = (total + pageSize - 1) / pageSize;
        return l;
    }

    public long getTotalElements() {
        return total;
    }
}
