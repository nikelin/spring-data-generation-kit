package com.a5000.platform.api.annotations.paging;

/**
 * Copyright 2016 Cyril A. Karpenko <self@nikelin.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
