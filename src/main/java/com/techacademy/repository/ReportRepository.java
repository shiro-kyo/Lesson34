package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    // 新規：同一日付チェック（従業員×日付）
    boolean existsByEmployeeAndReportDate(Employee employee, LocalDate reportDate);

    // 更新：同一日付チェック（従業員×日付、ただし自分(id)は除外）
    boolean existsByEmployeeAndReportDateAndIdNot(Employee employee, LocalDate reportDate, Integer id);
   

        List<Report> findByEmployee(Employee employee);
    }


