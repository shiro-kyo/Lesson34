package com.techacademy.controller;

import java.util.ArrayList;
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
 // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userDetail, Model model) {

        List<Report> reports;new ArrayList<>();

        // 管理者なら全件、それ以外は自分の分だけ
        if (userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            reports = reportService.findAll();
        } else {
            reports = reportService.findByEmployee(userDetail.getEmployee());
        }

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
    public String create( Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        if (userDetail != null) {
            report = new Report();
            report.setEmployee(userDetail.getEmployee());   
        }
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report, null, model);
        }
        
        Employee reportEmployee = userDetail.getEmployee();
        ErrorKinds result = reportService.save(report, reportEmployee);


            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, userDetail, model);
            }
            return "redirect:/reports";
    }
    
 // 日報更新画面（表示）
    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                          @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        Report report = reportService.findById(id).orElse(null);
        if (report == null) {
            return "redirect:/reports";
        }

        // 氏名表示用（new.htmlで *{employee.name} を使ってるなら必要）
        report.setEmployee(userDetail.getEmployee());

        model.addAttribute("report", report);
        return "reports/update";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Integer id,
                         @Validated @ModelAttribute Report report,
                         BindingResult res,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        // 入力チェック（バリデーション）
        if (res.hasErrors()) {
            model.addAttribute("report", report);
            return "reports/update";
        }

        ErrorKinds result =  reportService.update(report, userDetail.getEmployee());

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", report);
            return "reports/update";
        }

        return "redirect:/reports";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Integer id,
                         @AuthenticationPrincipal UserDetail userDetail,
                         Model model) {

        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result),
                               ErrorMessage.getErrorValue(result));
            return "redirect:/reports/" + id + "/";
        }

        return "redirect:/reports";
    }

}
