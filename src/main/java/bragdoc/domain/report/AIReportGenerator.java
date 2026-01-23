package bragdoc.domain.report;

/**
 * Interface de serviço de domínio para geração de relatórios com IA.
 * Implementação fica na infraestrutura.
 */
public interface AIReportGenerator {

    /**
     * Gera um relatório usando IA baseado nos dados fornecidos.
     */
    String generateReport(String enrichedData, ReportType reportType);

    /**
     * Gera um relatório usando IA permitindo um prompt customizado e um repositório
     * que podem influenciar o conteúdo do prompt enviado à API de IA.
     */
    String generateReport(String enrichedData, ReportType reportType, String userPrompt, String repository);
}
