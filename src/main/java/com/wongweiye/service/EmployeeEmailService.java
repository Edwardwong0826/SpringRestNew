package com.wongweiye.service;

import com.wongweiye.model.Address;
import com.wongweiye.model.Email;
import com.wongweiye.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeEmailService {

    public Employee createEmployee(Employee employee);
    public Optional<Employee> updateEmployee(long employeeId, Employee employee);

    public List<Employee> findEmployees(String name);

    public Page<Employee> searchEmployee(String name, long id, Pageable pageable);

    public Optional<Address> updateAddress(long addressId, long employeeId, Address address);

    public Optional<Email> updateEmailAndAddress(long addressId, long emailId, Email address);

    public List<Email> checkEmail(long employeeId);

    public List<Employee> checkEmailType(String type);

    public List<Email> checkEmailWithIdAndType(long id, String type);

    public Page<Employee> searchEmployeeWithEmailType(String name, String type, Pageable pageable);

}
