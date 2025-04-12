package com.ai.chatbot.admin.controller;

import com.ai.chatbot.admin.dto.AdminActivityResponse;
import com.ai.chatbot.admin.service.AdminActivityService;
import com.ai.chatbot.admin.service.ReportService;
import com.ai.chatbot.auth.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class AdminController {

    private final AdminActivityService adminActivityService;
    private final ReportService reportService;

    /**
     * 관리자 활동 요약 조회
     */
    @GetMapping("/activity")
    public ResponseEntity<AdminActivityResponse> getActivity(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(adminActivityService.getActivity(principal.getRole()));
    }

    /**
     * 하루치 CSV 보고서 다운로드
     */
    @GetMapping("/daily")
    public ResponseEntity<InputStreamResource> downloadReport(@AuthenticationPrincipal JwtUserPrincipal principal) {
        ByteArrayInputStream stream = reportService.generateDailyReport(principal.getRole());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(stream));
    }
}
