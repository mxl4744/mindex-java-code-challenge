package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee create request for id [{}]", id);

        return employeeService.read(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
    
    @GetMapping("/employee/{id}")
    public ReportingStructure getReportingStructure(@PathVariable String employeeId) {
    	LOG.debug("Received employee reporting structure request for id [{}]", employeeId);
        
    	return employeeService.getReportingStructure(employeeId);
    }
    
    @PostMapping
    public Compensation createComp(@RequestBody Compensation comp) {
    	LOG.debug("Received employee compensation create request for [{}]", comp.getEmployee());

        return employeeService.createComp(comp);
    }

    @GetMapping("/employee/{employeeId}")
    public Compensation readByEmployeeId(@PathVariable String employeeId) {
    	LOG.debug("Received employee compensation read request for id [{}]", employeeId);
    	
        return employeeService.readByEmployeeId(employeeId);
    }
}
