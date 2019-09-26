package com.township.manager;

public class Paytm {


    String mId;


    String orderId;


    String custId;


    String channelId;


    String txnAmount;


    String website;


    String callBackUrl;

    String industryTypeId;

    public Paytm(String channelId, String txnAmount, String website, String callBackUrl, String industryTypeId) {

        this.channelId = channelId;
        this.txnAmount = txnAmount;
        this.website = website;
        this.callBackUrl = callBackUrl;
        this.industryTypeId = industryTypeId;

    }

    public String getmId() {
        return mId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustId() {
        return custId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public String getWebsite() {
        return website;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }
}
