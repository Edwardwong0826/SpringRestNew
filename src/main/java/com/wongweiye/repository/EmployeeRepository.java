package com.wongweiye.repository;

import com.wongweiye.model.Email;
import com.wongweiye.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByName(String name);

    // when using JPQL/HQL, select x from y, the y need to be refers to entity class name
    @Query("select e from Employee e join e.emails m where m.type = :emailType")
    List<Employee> getEmployeeByType(String emailType);

    @Query("select m from Employee e join e.emails m where m.id = ?1 and  m.type = ?2")
    List<Email> getEmailByIdAndType(long emailId, String emailType);

}
