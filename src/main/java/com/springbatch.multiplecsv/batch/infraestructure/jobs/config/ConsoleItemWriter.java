package com.springbatch.multiplecsv.batch.infraestructure.jobs.config;

import com.springbatch.multiplecsv.batch.domain.Employee;
import com.springbatch.multiplecsv.batch.infraestructure.repository.EmployeeRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//public class ConsoleItemWriter<T>implements ItemWriter<T> {
//
//
//    @Override
//    public void write(List<? extends T> list) throws Exception {
//        for(T item: list){
//            System.out.println(item);
//
//        }
//
//    }
//
//}

 public class ConsoleItemWriter implements ItemWriter<Employee>{

     @Autowired
     EmployeeRepository repository;

     @Override
     public void write(List<? extends Employee> list) throws Exception {
         for(Employee item:list){
             System.out.println(item);
         }

        repository.saveAll(list);
     }
 }
