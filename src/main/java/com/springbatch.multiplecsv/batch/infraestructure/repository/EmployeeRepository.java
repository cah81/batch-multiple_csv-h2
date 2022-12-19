package com.springbatch.multiplecsv.batch.infraestructure.repository;

import com.springbatch.multiplecsv.batch.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
