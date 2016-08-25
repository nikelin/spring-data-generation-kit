package com.a5000.platform.api.annotations.paging;

import java.util.Collection;

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
