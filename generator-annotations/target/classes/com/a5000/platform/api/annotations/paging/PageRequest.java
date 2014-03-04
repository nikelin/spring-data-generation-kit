package com.a5000.platform.api.annotations.paging;

/**
 * @author nikelin
 * @date 22:51
 */
public class PageRequest {
    private final Sorting sorting;
    private final PageSize pageSize;
    private final int page;

    public PageRequest(Sorting sorting, String pageSize, int page ) {
        this(sorting, PageSize.valueOf(pageSize), page );
    }

    public PageRequest(Sorting sorting, PageSize pageSize, int page) {
        this.sorting = sorting;
        this.pageSize = pageSize;
        this.page = page;
    }

    public Sorting getSorting() {
        return sorting;
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public int getPage() {
        return page;
    }
}
