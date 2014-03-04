
package com.a5000.platform.api.annotations.generators.jpa.entities;

import java.io.Serializable;

public class TestParentDTO
    implements Serializable
{

    private Long id;

    public void setId(Long value) {
        this.id = value;
    }

    public Long getId() {
        return this.id;
    }

}
