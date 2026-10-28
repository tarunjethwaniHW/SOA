package com.unified.mortgage.modification.spring;

/**
 * Document Classifier Interface - Exposed via Spring SCA
 * Referenced from: composite.xml → spring/hardship-doc-classifier-context.xml
 *
 * Call Chain:
 *   SOA_COMPONENT (HardshipDocClassifierComponent)
 *       → COMPONENT_TO_SPRING → hardship-doc-classifier-context.xml
 *       → SPRING_BEAN (hardshipDocClassifierBean)
 *       → SPRING_BEAN_TO_CLASS → HardshipDocClassifierImpl.java
 */
public interface IHardshipDocClassifier {

    // Document type constants
    String DOC_TYPE_W2 = "W2";
    String DOC_TYPE_PAYSTUB = "PAYSTUB";
    String DOC_TYPE_TAX_RETURN = "TAX_RETURN";
    String DOC_TYPE_BANK_STATEMENT = "BANK_STATEMENT";
    String DOC_TYPE_ID_DOCUMENT = "ID_DOCUMENT";
    String DOC_TYPE_UNKNOWN = "UNKNOWN";

    /**
     * Classify a document based on its content.
     * @param documentContent The document content as text
     * @return Document type classification
     */
    String classifyHardshipDocument(String documentContent);

    /**
     * Get confidence score for the classification.
     * @param documentContent The document content
     * @param classifiedType The classified type
     * @return Confidence score 0-100
     */
    int getHardshipConfidenceScore(String documentContent, String classifiedType);

    /**
     * Check if document is a valid income document.
     * @param documentType The classified document type
     * @return true if W2, paystub, or tax return
     */
    boolean isHardshipIncomeDocument(String documentType);
}
