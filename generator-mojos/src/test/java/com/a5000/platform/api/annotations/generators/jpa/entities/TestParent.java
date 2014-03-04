package com.a5000.platform.api.annotations.generators.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by cyril on 8/28/13.
 */
@Entity
public class TestParent implements ITest {

    @Id
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
