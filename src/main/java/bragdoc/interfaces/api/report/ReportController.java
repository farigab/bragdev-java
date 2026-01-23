package bragdoc.interfaces.api.report;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bragdoc.application.report.GenerateAIReportUseCase;
import bragdoc.application.report.GenerateCategoryReportUseCase;
import bragdoc.application.report.GenerateGitHubAnalysisUseCase;
import bragdoc.application.report.GeneratePeriodReportUseCase;
import bragdoc.application.report.GenerateSummaryReportUseCase;
import bragdoc.application.report.dto.AICustomSummaryRequest;
import bragdoc.application.report.dto.AIReportResponse;
import bragdoc.application.report.dto.GenerateReportRequest;
import bragdoc.application.report.dto.ReportResponse;
import bragdoc.interfaces.api.common.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

    @PostMapping("/ai-summary/custom")
    public ResponseEntity<AIReportResponse> getAICustomSummary(
            @RequestBody @Valid AICustomSummaryRequest requestDto,
            @CurrentUser String userLogin) {

        var request = new GenerateReportRequest(
                requestDto.reportType(),
                requestDto.category(),
                requestDto.startDate(),
                requestDto.endDate());

        var response = generateAIReportUseCase.executeCustom(
                request,
                userLogin,
                requestDto.userPrompt(),
                requestDto.repository());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai-github-analysis")
    public ResponseEntity<AIReportResponse> getAIGitHubAnalysis(@CurrentUser String userLogin) {
        var response = generateGitHubAnalysisUseCase.execute(userLogin);
        return ResponseEntity.ok(response);
    }
}
