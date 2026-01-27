package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public Optional<Report> findById(Integer id) {
        return reportRepository.findById(id);
    }

    // 日報保存（新規）
    @Transactional
    public ErrorKinds save(Report report, Employee loginEmployee) {

        // 入力チェック
        ErrorKinds check = validate(report);
        if (check != ErrorKinds.CHECK_OK) {
            return check;
        }

        // 同一日付チェック（新規）
        if (reportRepository.existsByEmployeeAndReportDate(loginEmployee, report.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        // 登録（所有者をログイン中従業員に固定）
        report.setEmployee(loginEmployee);
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report input, Employee loginEmployee) {

        // 入力チェック
        ErrorKinds check = validate(input);
        if (check != ErrorKinds.CHECK_OK) {
            return check;
        }

        // 更新対象を取得
        Report current = reportRepository.findById(input.getId()).orElse(null);
        if (current == null) {
            return ErrorKinds.CHECK_ERROR;
        }

        // 同一日付チェック（更新：自分以外）
//        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(
//                loginEmployee, input.getReportDate(), input.getId())) {
//            return ErrorKinds.DATECHECK_ERROR;
//        }

        // 更新（所有者はログイン中従業員に固定）
        current.setEmployee(loginEmployee);
        current.setReportDate(input.getReportDate());
        current.setTitle(input.getTitle());
        current.setContent(input.getContent());
        current.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(current);
        return ErrorKinds.SUCCESS;
    }

    // 入力チェック共通化（仕様書：必須 + 文字数）
    private ErrorKinds validate(Report report) {

        if (report.getReportDate() == null) {
            return ErrorKinds.REPORTDATE_BLANK_ERROR;
        }

        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            return ErrorKinds.TITLE_BLANK_ERROR;
        }

        if (report.getTitle().length() > 100) {
            return ErrorKinds.TITLE_LENGTH_ERROR;
        }

        if (report.getContent() == null || report.getContent().trim().isEmpty()) {
            return ErrorKinds.CONTENT_BLANK_ERROR;
        }

        if (report.getContent().length() > 600) {
            return ErrorKinds.CONTENT_LENGTH_ERROR;
        }

        return ErrorKinds.CHECK_OK;
    }
}
