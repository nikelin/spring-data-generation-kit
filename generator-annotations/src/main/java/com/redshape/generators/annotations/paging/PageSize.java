package com.redshape.generators.annotations.paging;

/**
 * @author nikelin
 * @date 22:53
 */
public enum PageSize {
    S(10),
    M(25),
    L(50),
    XL(100);

    private int value;

    private PageSize( int value ) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
