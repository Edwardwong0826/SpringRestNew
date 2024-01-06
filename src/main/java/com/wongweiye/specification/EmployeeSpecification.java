package com.wongweiye.specification;

import com.wongweiye.model.Email;
import com.wongweiye.model.Employee;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

// https://stackoverflow.com/questions/47469861/what-is-the-difference-between-a-criteria-a-predicate-and-a-specification
// https://medium.com/@bubu.tripathy/dynamic-query-with-specification-interface-in-spring-data-jpa-ae8764e32162
// Root - object represents the entity being queried and allows access to its attributes
// Criteria API - The Java Persistence Criteria API is used to define dynamic queries through the construction of object-based query definition objects, rather than use of the string-based approach of JPQL.
// CriteriaQuery - use to write queries without doing raw sql as well as gives us some object-oriented control over the queries
// CriteriaBuilder - is used to build a CriteriaQuery objects which is then used to perform a query
// Predicate - is class from the Criteria API used to construct where clauses
// Specification is an interface come by Spring Data JPA for define reusable Predicates derived concepts from Domain Driven Design
public class EmployeeSpecification {

    // root.get("xxx") this one refer to entity field name, not the actual table column name
    public static Specification<Employee> hasNameLike(String name){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Employee> idGreaterThan(long id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }

    // Criteria Query API allow us to join two table using Join
    public static Specification<Employee> hasEmailWithType(String type){
        return (root, query, criteriaBuilder) ->  {
          Join<Email, Employee> employeeEmail = root.join("emails");
          return criteriaBuilder.equal(employeeEmail.get("type"), type);
        };
    }
}
