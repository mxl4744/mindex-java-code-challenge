package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompensationRepository compRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        int numberOfReports = countReports(employee);
        
        return new ReportingStructure(employee, numberOfReports);
    }

    private int countReports(Employee employee) {
        if (employee == null || employee.getDirectReports() == null) {
            return 0;
        }

        int count = employee.getDirectReports().size(); // count direct reports
        Set<Employee> allReports = new HashSet<>(employee.getDirectReports()); // HashSet to avoid duplicates

        for (Employee directReport : employee.getDirectReports()) {
            Employee report = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
            count += countReports(report); // count indirect reports recursively
            allReports.addAll(report.getDirectReports());
        }

        return count + allReports.size(); // Total reports
    }

	@Override
	public Compensation createComp(Compensation comp) {
		Employee employee = read(comp.getEmployee().getEmployeeId());
	    comp.setEmployee(employee);
		return compRepository.insert(comp);
	}
    
    @Override
	public Compensation readByEmployeeId(String employeeId) {
    	return compRepository.findByEmployeeId(employeeId);
	}

}
