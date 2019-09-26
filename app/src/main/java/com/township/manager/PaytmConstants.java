package com.township.manager;

import com.google.gson.annotations.SerializedName;

public class PaytmConstants {
    public static final String CHANNEL_ID="WAP";
    public static final String WEBSITE="WEBSTAGING";
    public static final String INDUSTRY_TYPE_ID="Retail";

    @SerializedName("CALLBACK_URL")
    static String CALLBACK_URL="https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    @SerializedName("MID")
    public static final String MID="NrgQec71041852491050";

    @SerializedName("ORDER_ID")
    String ORDER_ID;

    @SerializedName("CUST_ID")
    String CUST_ID;

    @SerializedName("TXN_AMOUNT")
    String TXN_AMOUNT;

    public String getCALLBACK_URL() {
        return CALLBACK_URL;
    }

    public void setCALLBACK_URL(String CALLBACK_URL) {
        this.CALLBACK_URL = CALLBACK_URL;
    }

    public String getMID() {
        return MID;
    }



    public String getORDER_ID() {
        return ORDER_ID;
    }

    public void setORDER_ID(String ORDER_ID) {
        this.ORDER_ID = ORDER_ID;
    }

    public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getTXN_AMOUNT() {
        return TXN_AMOUNT;
    }

    public void setTXN_AMOUNT(String TXN_AMOUNT) {
        this.TXN_AMOUNT = TXN_AMOUNT;
    }

    public String getCHECKSUMHASH() {
        return CHECKSUMHASH;
    }

    public void setCHECKSUMHASH(String CHECKSUMHASH) {
        this.CHECKSUMHASH = CHECKSUMHASH;
    }

    @SerializedName("CHECKSUMHASH")
    String CHECKSUMHASH;

}
