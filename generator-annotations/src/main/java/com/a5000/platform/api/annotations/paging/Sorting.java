package com.a5000.platform.api.annotations.paging;

/**
 * @author nikelin
 * @date 22:52
 */
public class Sorting {
    private final SortDirection direction;
    private final String field;

    public Sorting(String direction) {
        this(SortDirection.valueOf(direction));
    }

    public Sorting(SortDirection direction) {
        this(direction, "id");
    }

    public Sorting(String direction, String field) {
        this(SortDirection.valueOf(direction), field);
    }

    public Sorting(SortDirection direction, String field) {
        this.direction = direction;
        this.field = field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public String getField() {
        return field;
    }
}
