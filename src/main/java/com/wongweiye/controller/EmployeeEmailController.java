package com.wongweiye.controller;

import com.wongweiye.model.Address;
import com.wongweiye.model.Email;
import com.wongweiye.model.Employee;
import com.wongweiye.service.EmployeeEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/v1/configurations/EmployeeEmail" , produces = "application/json")
public class EmployeeEmailController {

    @Autowired
    EmployeeEmailService employeeEmailService;

    @PostMapping(path="/create")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee){


        Employee newEmployee = employeeEmailService.createEmployee(employee);

        if(newEmployee != null) {
            return ResponseEntity.ok().body(newEmployee);
        }
        else
        {
            return ResponseEntity.notFound().build();

        }
    }

    @PutMapping(path = "/update/{employeeID}", consumes = "application/json" )
    public ResponseEntity<Employee> updateEmployee(@PathVariable long employeeID, @RequestBody Employee employee) {


        Optional<Employee> newEmployee = employeeEmailService.updateEmployee(employeeID, employee);

        if(newEmployee.isPresent()) {
            return ResponseEntity.ok().body(newEmployee.get());
        }
        else
        {
            return ResponseEntity.notFound().build();

        }
    }

    @PutMapping(path = "/update/employee/{employeeID}/address/{addressID}", consumes = "application/json" )
    public ResponseEntity<Address> updateAddress(@PathVariable long addressID, @PathVariable long employeeID, @RequestBody Address address) {


        Optional<Address> newAddress = employeeEmailService.updateAddress(addressID, employeeID, address);

        if(newAddress.isPresent()) {
            return ResponseEntity.ok().body(newAddress.get());
        }
        else
        {
            return ResponseEntity.notFound().build();

        }
    }

    // @RequestBody can only use for POST/PUT not Get
    // else will not able to get json data value
    @PutMapping(path = "/update/email/{emailId}/address/{addressID}", consumes = "application/json" )
    public ResponseEntity<Email> updateEmailAndAddress(@PathVariable long addressID, @PathVariable long emailId, @RequestBody Email email) {


        Optional<Email> newEmail = employeeEmailService.updateEmailAndAddress(addressID, emailId, email);

        if(newEmail.isPresent()) {
            return ResponseEntity.ok().body(newEmail.get());
        }
        else
        {
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping(path = "/check/{employeeID}")
    public ResponseEntity<List<Email>> checkEmailAndUpdate(@PathVariable long employeeID) {


        List<Email> emails = employeeEmailService.checkEmail(employeeID);

        return ResponseEntity.ok().body(emails);

    }

    @GetMapping(path="/find/employee/{name}")
    public ResponseEntity<List<Employee>> checkEmployeeByName(@PathVariable String name){
        List<Employee> employees = employeeEmailService.findEmployees(name);

        return ResponseEntity.ok().body(employees);
    }

    @GetMapping(path="/check/by/email/type")
    public ResponseEntity<List<Employee>> checkEmployeeByEmailType(@RequestParam String emailType){
        List<Employee> employees = employeeEmailService.checkEmailType(emailType);
        return ResponseEntity.ok().body(employees);
    }

    @GetMapping(path="/check/by/email/idAndType")
    public ResponseEntity<List<Email>> checkEmployeeByEmailIdAndType(@RequestParam long emailId , @RequestParam String emailType){
        List<Email> emails = employeeEmailService.checkEmailWithIdAndType(emailId, emailType);
        return ResponseEntity.ok().body(emails);
    }

    @GetMapping(path="/search/employee")
    public ResponseEntity<List<Employee>> searchEmployeeName(@RequestParam String employeeName, @RequestParam long id){
        Pageable page = PageRequest.of(0,10);
        Page<Employee> employees = employeeEmailService.searchEmployee(employeeName, id, page);

        return ResponseEntity.ok().body(employees.toList());
    }

    @GetMapping(path="/search/employeeWithEmail")
    public ResponseEntity<List<Employee>> searchEmployeeWithEmailType(@RequestParam String employeeName, @RequestParam String emailType){

        Pageable page = PageRequest.of(0,10);
        Page<Employee> employees = employeeEmailService.searchEmployeeWithEmailType(employeeName, emailType, page);

        return ResponseEntity.ok().body(employees.toList());
    }

}
