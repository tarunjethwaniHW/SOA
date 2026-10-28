package com.unified.mortgage.modification.adapters;

/**
 * OCR Processor - Java Adapter for BPEL Java Embedding
 * Document OCR processing and classification.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="ProcessHardshipOcr" language="java">
 *     com.unified.mortgage.modification.adapters.HardshipOcrProcessor processor = new com.unified.mortgage.modification.adapters.HardshipOcrProcessor();
 *     HardshipOcrResult result = processor.processHardshipDocument(documentData);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/ocr-processor.jar
 */
public class HardshipOcrProcessor {

    public static final String DOC_TYPE_PAYSTUB = "PAYSTUB";
    public static final String DOC_TYPE_W2 = "W2";
    public static final String DOC_TYPE_TAX_RETURN = "TAX_RETURN";
    public static final String DOC_TYPE_BANK_STATEMENT = "BANK_STATEMENT";
    public static final String DOC_TYPE_ID_DOCUMENT = "ID_DOCUMENT";
    public static final String DOC_TYPE_UNKNOWN = "UNKNOWN";

    /**
     * Processes a document with OCR and returns classification result.
     */
    public HardshipOcrResult processHardshipDocument(byte[] documentData) {
        if (documentData == null || documentData.length == 0) {
            return new HardshipOcrResult(DOC_TYPE_UNKNOWN, "", 0);
        }

        String content = new String(documentData);
        String documentType = classifyHardshipDocument(content);
        String extractedText = extractHardshipText(content);
        int confidence = calculateHardshipConfidence(content, documentType);

        return new HardshipOcrResult(documentType, extractedText, confidence);
    }

    /**
     * Classifies document type based on content patterns.
     */
    private String classifyHardshipDocument(String content) {
        String lowerContent = content.toLowerCase();

        if (lowerContent.contains("w-2") || lowerContent.contains("wage and tax")) {
            return DOC_TYPE_W2;
        }
        if (lowerContent.contains("pay period") || lowerContent.contains("earnings")) {
            return DOC_TYPE_PAYSTUB;
        }
        if (lowerContent.contains("form 1040") || lowerContent.contains("tax return")) {
            return DOC_TYPE_TAX_RETURN;
        }
        if (lowerContent.contains("account balance") || lowerContent.contains("statement period")) {
            return DOC_TYPE_BANK_STATEMENT;
        }
        if (lowerContent.contains("driver") || lowerContent.contains("license") || lowerContent.contains("passport")) {
            return DOC_TYPE_ID_DOCUMENT;
        }

        return DOC_TYPE_UNKNOWN;
    }

    /**
     * Extracts text from document (simplified for testing).
     */
    private String extractHardshipText(String content) {
        return content.length() > 500 ? content.substring(0, 500) + "..." : content;
    }

    /**
     * Calculates confidence score for classification.
     */
    private int calculateHardshipConfidence(String content, String documentType) {
        if (DOC_TYPE_UNKNOWN.equals(documentType)) return 0;
        int keywordCount = countHardshipKeywords(content.toLowerCase(), documentType);
        return Math.min(100, keywordCount * 20);
    }

    private int countHardshipKeywords(String content, String documentType) {
        int count = 0;
        switch (documentType) {
            case DOC_TYPE_W2:
                if (content.contains("employer")) count++;
                if (content.contains("wages")) count++;
                if (content.contains("federal")) count++;
                break;
            case DOC_TYPE_PAYSTUB:
                if (content.contains("gross")) count++;
                if (content.contains("net")) count++;
                if (content.contains("deductions")) count++;
                break;
            case DOC_TYPE_BANK_STATEMENT:
                if (content.contains("balance")) count++;
                if (content.contains("deposits")) count++;
                if (content.contains("withdrawals")) count++;
                break;
            default:
                count = 1;
        }
        return count;
    }

    /**
     * OCR processing result containing document type and extracted text.
     */
    public static class HardshipOcrResult {
        private final String documentType;
        private final String extractedText;
        private final int confidence;

        public HardshipOcrResult(String documentType, String extractedText, int confidence) {
            this.documentType = documentType;
            this.extractedText = extractedText;
            this.confidence = confidence;
        }

        public String getDocumentType() { return documentType; }
        public String getExtractedText() { return extractedText; }
        public int getConfidence() { return confidence; }
    }
}
