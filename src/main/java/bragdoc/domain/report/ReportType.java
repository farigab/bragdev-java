package bragdoc.domain.report;

/**
 * Enum representando os tipos de relatório disponíveis.
 */
public enum ReportType {
    EXECUTIVE("executive"),
    TECHNICAL("technical"),
    TIMELINE("timeline"),
    GITHUB("github");

    private final String value;

    ReportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReportType from(String value) {
        if (value == null) {
            return EXECUTIVE; // default
        }

        for (ReportType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }

        return EXECUTIVE;
    }
}
