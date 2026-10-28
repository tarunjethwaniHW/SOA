package com.unified.mortgage.refinance.approval;

import javax.xml.ws.WebFault;
import com.unified.mortgage.refinance.approval.types.RefinanceFaultDetail;

@WebFault(name = "RefinanceFaultDetail", targetNamespace = "http://xmlns.oracle.com/RefinanceOrchestration/types")
public class SanctionListFaultException extends Exception {
    
    private final RefinanceFaultDetail faultInfo;

    public SanctionListFaultException(String message, RefinanceFaultDetail faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public SanctionListFaultException(String message, RefinanceFaultDetail faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public RefinanceFaultDetail getFaultInfo() {
        return faultInfo;
    }
}
