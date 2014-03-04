package com.redshape.generators.annotations.paging;

import java.util.Collection;

/**
 * @author nikelin
 * @date 22:51
 */
public class PageResponse<T> {
    private final Collection<T> result;
    private final int pages;
    private final long total;

    public PageResponse(int pages, long total, Collection<T> result ) {
        this.result = result;
        this.total = total;
        this.pages = pages;
    }

    public long getTotal() {
        return total;
    }

    public Collection<T> getResult() {
        return result;
    }

    public int getPages() {
        return pages;
    }

}
