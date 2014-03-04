
package com.a5000.platform.api.annotations.generators.jpa.entities;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("iTestDAO")
public interface ITestDAO
    extends ITestParentDAO<Test>
{


    @org.springframework.data.jpa.repository.Query
    public Test findByName(
        @org.springframework.data.repository.query.Param("name")
        String name);

    @org.springframework.data.jpa.repository.Query
    public Page<Test> findByName(
        @org.springframework.data.repository.query.Param("name")
        String name, Pageable pageable);

    @org.springframework.data.jpa.repository.Query
    public List<Test> findByUserId(
        @org.springframework.data.repository.query.Param("userId")
        Long userId);

    @org.springframework.data.jpa.repository.Query("delete from Test where id = :id")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    public List<Test> deleteWhereUserIdIs(
        @org.springframework.data.repository.query.Param("id")
        Long id);

    @org.springframework.data.jpa.repository.Query("delete from Test where id in (:id)")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    public List<void> deleteWhereUserIdIn(
        @org.springframework.data.repository.query.Param("id")
        Long[] id);

    @org.springframework.data.jpa.repository.Query("select count(id) from Test where :id")
    public List<Long> countById(
        @org.springframework.data.repository.query.Param("id")
        Long id);

}
