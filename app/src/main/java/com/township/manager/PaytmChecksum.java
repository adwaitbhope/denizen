package com.township.manager;

public class PaytmChecksum {

    private String checksumHash;
    private String orderId;

    private String paytStatus;

    public String getChecksumHash() {
        return checksumHash;
    }

    public void setChecksumHash(String checksumHash) {
        this.checksumHash = checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }

    public void setPaytStatus(String paytStatus) {
        this.paytStatus = paytStatus;
    }

    public PaytmChecksum(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }
}
