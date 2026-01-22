package bragdoc.infrastructure.integration.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import bragdoc.domain.report.AIReportGenerator;
import bragdoc.domain.report.ReportPromptBuilder;
import bragdoc.domain.report.ReportType;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do gerador de relatórios com IA usando Google Gemini.
 */
@Service
@Slf4j
public class GeminiAIReportGenerator implements AIReportGenerator {

    private final RestTemplate restTemplate;
    private final ReportPromptBuilder promptBuilder;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:}")
    private String apiUrl;

    @Value("${gemini.api.model:}")
    private String modelName;

    public GeminiAIReportGenerator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.promptBuilder = new ReportPromptBuilder();
    }

    @Override
    public String generateReport(String enrichedData, ReportType reportType) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                log.error("Gemini API Key não configurada");
                return "Erro: API Key não configurada.";
            }

            // 1. Construir prompt
            String prompt = promptBuilder.buildPrompt(enrichedData, reportType);

            // 2. Preparar requisição
            String url = String.format("%s/models/%s:generateContent?key=%s",
                    apiUrl, modelName, apiKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            GeminiRequest request = GeminiRequest.create(prompt);
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            log.info("Gerando relatório do tipo: {} com modelo: {}", reportType, modelName);

            // 3. Chamar API
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class);

            // 4. Extrair texto gerado
            if (response != null && response.getBody() != null) {
                String generatedText = response.getBody().getGeneratedText();

                if (generatedText == null || generatedText.trim().length() < 100) {
                    log.warn("Resposta muito curta ou vazia da API");
                    return "Erro: Resposta inválida da API. Tente novamente.";
                }

                return generatedText.trim();
            }

            return "Erro: Resposta vazia da API";

        } catch (Exception e) {
            log.error("Erro ao gerar relatório com Gemini: {}", e.getMessage());
            return "Erro ao gerar relatório: " + e.getMessage();
        }
    }
}
