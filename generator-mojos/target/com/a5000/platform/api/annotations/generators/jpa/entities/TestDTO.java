
package com.a5000.platform.api.annotations.generators.jpa.entities;

import java.util.ArrayList;
import java.util.List;

public class TestDTO
    extends TestParentDTO
{

    private String name;
    private com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO relatedTest;
    private Long relatedTestAId;
    private List<com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO> relatedTests = new ArrayList();
    private com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO relatedTestByInterface;
    private Long testParentId;

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }

    public void setRelatedTest(com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO value) {
        this.relatedTest = value;
    }

    public com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO getRelatedTest() {
        return this.relatedTest;
    }

    public void setRelatedTestAId(Long value) {
        this.relatedTestAId = value;
    }

    public Long getRelatedTestAId() {
        return this.relatedTestAId;
    }

    public void setRelatedTests(List<com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO> value) {
        this.relatedTests = value;
    }

    public List<com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO> getRelatedTests() {
        return this.relatedTests;
    }

    public void setRelatedTestByInterface(com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO value) {
        this.relatedTestByInterface = value;
    }

    public com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO getRelatedTestByInterface() {
        return this.relatedTestByInterface;
    }

    public void setTestParentId(Long value) {
        this.testParentId = value;
    }

    public Long getTestParentId() {
        return this.testParentId;
    }

    public com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO getRelatedTestX() {
        
        return relatedTest;
    
    }

    public static int add(int z, int y) {
        
        return y + z;
    
    }

}
