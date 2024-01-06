package com.wongweiye.serviceImpl;

import com.wongweiye.model.Address;
import com.wongweiye.model.Email;
import com.wongweiye.model.Employee;
import com.wongweiye.repository.AddressRepository;
import com.wongweiye.repository.EmailRepository;
import com.wongweiye.repository.EmployeeRepository;
import com.wongweiye.service.EmployeeEmailService;
import com.wongweiye.specification.EmployeeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeEmailServiceImpl implements EmployeeEmailService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    EmailRepository emailRepository;

    @Override
    public Employee createEmployee(Employee employee) {

//          // this is manually add and created the object
//          List<Email> emails = new ArrayList<>();
//
//
//          Email email = new Email("A",employee);
//          emails.add(email);
//
//
//          email.getEmailAddress().add(new Address("gmail.com",email)); //-- this is for one to many relationship
//          //emails.get(0).setAddress(new Address("gmail.com",email)); // -- this is for one to one relationship
//
//          employee.setName("test report");
//          employee.setEmails(emails);

        employee.getEmails().forEach(d -> d.setEmployee(employee));

        employee.getEmails().forEach(e-> e.getEmailAddress().forEach( s -> s.setEmail(e) ) );

       return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> updateEmployee(long employeeId, Employee employee) {

        return employeeRepository.findById(employeeId).map( emp ->{
            emp.setName(employee.getName());
//            emp.setEmails(employee.getEmails());
//            emp.getEmails().get(0).setEmployee(emp);
//            emp.getEmails().get(0).setEmailAddress( employee.getEmails().get(0).getEmailAddress() );
        return employeeRepository.save(emp);
        });
    }

    @Override
    public List<Employee> findEmployees(String name) {

        List<Employee> byName = employeeRepository.findByName(name);
        return byName;
    }

    @Override
    public Page<Employee> searchEmployee(String name, long id, Pageable pageable){

        Specification<Employee> spec = Specification.where(null);

        if (Objects.nonNull(name)) {
            spec = spec.and(EmployeeSpecification.hasNameLike(name)).and(EmployeeSpecification.idGreaterThan(id));
        }

        return employeeRepository.findAll(spec, pageable);

    }


    @Override
    public Optional<Address> updateAddress(long addressId, long employeeId, Address address) {

        return addressRepository.findById(addressId).map( addr ->{
            addr.setAddressName(address.getAddressName());
            return addressRepository.save(addr);
        });
    }

    @Override
    public Optional<Email> updateEmailAndAddress(long addressId, long emailId, Email inEmail) {
        List<Address> addressList = emailRepository.findById(emailId).get().getEmailAddress();
        return emailRepository.findById(emailId).map(email ->{


            email.getEmailAddress().clear();
            email.getEmailAddress().addAll(inEmail.getEmailAddress());
            email.getEmailAddress().forEach(s-> s.setEmail(email));
            return emailRepository.save(email);


        });
    }

    @Override
    public List<Email> checkEmail(long employeeId) {
        Optional<Employee> byId = employeeRepository.findById(employeeId);

        return byId.get().getEmails();
    }

    public List<Employee> checkEmailType(String emailType) {
        List<Employee> byEmailType = employeeRepository.getEmployeeByType(emailType);

        return byEmailType;
    }

    @Override
    public List<Email> checkEmailWithIdAndType(long id, String type) {
        List<Email> emailByIdAndType = employeeRepository.getEmailByIdAndType(id, type);
        return emailByIdAndType;
    }

    @Override
    public Page<Employee> searchEmployeeWithEmailType(String name, String type, Pageable pageable){
        Specification<Employee> spec = Specification.where(null);

        spec = spec.and(EmployeeSpecification.hasNameLike(name)).and(EmployeeSpecification.hasEmailWithType(type));

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

        return employeePage;
    }


}
