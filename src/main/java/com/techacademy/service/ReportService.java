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

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, Employee loginEmployee) {

        // ===== 入力チェック（仕様書）=====
        // 日付：必須
        if (report.getReportDate() == null) {
            return ErrorKinds.REPORTDATE_BLANK_ERROR;
        }

        // タイトル：必須
        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            return ErrorKinds.TITLE_BLANK_ERROR;
        }

        // タイトル：100文字超
        if (report.getTitle().length() > 100) {
            return ErrorKinds.TITLE_LENGTH_ERROR;
        }

        // 内容：必須
        if (report.getContent() == null || report.getContent().trim().isEmpty()) {
            return ErrorKinds.CONTENT_BLANK_ERROR;
        }

        // 内容：600文字超
        if (report.getContent().length() > 600) {
            return ErrorKinds.CONTENT_LENGTH_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
}
