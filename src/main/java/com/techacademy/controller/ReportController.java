package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        List<Report> reports = reportService.findAll();
        model.addAttribute("reportList", reports);
        model.addAttribute("listSize", reports.size());
        return "reports/list";
    }
    
 // 日報詳細画面
    @GetMapping("/{id}/")
    public String detail(@PathVariable("id") Integer id, Model model) {

        Report report = reportService.findById(id).orElse(null);
        model.addAttribute("report", report);

        return "reports/detail";
    }

    
    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());
        return "reports/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, userDetail);
        }
        
        Employee reportEmployee = userDetail.getEmployee();
        ErrorKinds result = reportService.save(report, reportEmployee);


            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, userDetail);
            }

        return "redirect:/reports";
    }
}
