package bragdoc.interfaces.api.report;

import bragdoc.application.report.*;
import bragdoc.application.report.dto.AIReportResponse;
import bragdoc.application.report.dto.GenerateReportRequest;
import bragdoc.application.report.dto.ReportResponse;
import bragdoc.interfaces.api.common.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Relatórios", description = "Geração de relatórios de conquistas")
public class ReportController {

    private final GenerateSummaryReportUseCase generateSummaryUseCase;
    private final GenerateCategoryReportUseCase generateCategoryUseCase;
    private final GeneratePeriodReportUseCase generatePeriodUseCase;
    private final GenerateAIReportUseCase generateAIReportUseCase;
    private final GenerateGitHubAnalysisUseCase generateGitHubAnalysisUseCase;

    public ReportController(
            GenerateSummaryReportUseCase generateSummaryUseCase,
            GenerateCategoryReportUseCase generateCategoryUseCase,
            GeneratePeriodReportUseCase generatePeriodUseCase,
            GenerateAIReportUseCase generateAIReportUseCase,
            GenerateGitHubAnalysisUseCase generateGitHubAnalysisUseCase) {
        this.generateSummaryUseCase = generateSummaryUseCase;
        this.generateCategoryUseCase = generateCategoryUseCase;
        this.generatePeriodUseCase = generatePeriodUseCase;
        this.generateAIReportUseCase = generateAIReportUseCase;
        this.generateGitHubAnalysisUseCase = generateGitHubAnalysisUseCase;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportResponse> getSummary(@CurrentUser String userLogin) {
        var response = generateSummaryUseCase.execute(userLogin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-category")
    public ResponseEntity<ReportResponse> getByCategory(
            @RequestParam(required = false) String category,
            @CurrentUser String userLogin) {

        var response = generateCategoryUseCase.execute(category, userLogin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-period")
    public ResponseEntity<ReportResponse> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CurrentUser String userLogin) {

        var response = generatePeriodUseCase.execute(startDate, endDate, userLogin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai-summary")
    public ResponseEntity<AIReportResponse> getAISummary(
            @RequestParam(required = false, defaultValue = "executive") String reportType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @CurrentUser String userLogin) {

        var request = new GenerateReportRequest(reportType, category, startDate, endDate);
        var response = generateAIReportUseCase.execute(request, userLogin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai-github-analysis")
    public ResponseEntity<AIReportResponse> getAIGitHubAnalysis(@CurrentUser String userLogin) {
        var response = generateGitHubAnalysisUseCase.execute(userLogin);
        return ResponseEntity.ok(response);
    }
}
