
package com.a5000.platform.api.annotations.generators.jpa.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("iTestParentDAO")
public interface ITestParentDAO<T extends TestParent >
    extends JpaRepository<T, Long>
{


}
