package com.township.manager;

import com.google.gson.annotations.SerializedName;

public class Paytm {
    static final String MID = "NrgQec71041852491050";
    static final String CHANNEL_ID = "WAP";
    static final String WEBSITE = "WEBSTAGING";
    static final String INDUSTRY_TYPE_ID = "Retail";
    static final String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    static class PaytmOrder {
        private String ORDER_ID;
        private String CUST_ID;
        private String TXN_AMOUNT;
        private String CHECKSUMHASH;

        void setORDER_ID(String ORDER_ID) {
            this.ORDER_ID = ORDER_ID;
        }

        void setCUST_ID(String CUST_ID) {
            this.CUST_ID = CUST_ID;
        }

        void setTXN_AMOUNT(String TXN_AMOUNT) {
            this.TXN_AMOUNT = TXN_AMOUNT;
        }

        void setCHECKSUMHASH(String CHECKSUMHASH) {
            this.CHECKSUMHASH = CHECKSUMHASH;
        }

        String getORDER_ID() {
            return ORDER_ID;
        }

        String getCUST_ID() {
            return CUST_ID;
        }

        String getTXN_AMOUNT() {
            return TXN_AMOUNT;
        }

        String getCHECKSUMHASH() {
            return CHECKSUMHASH;
        }
    }

}
