package com.learningmachine.android.app.ui.share;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardRequestBody {

    @SerializedName("blockcert")
    @Expose
    private String blockCert;

    @SerializedName("clientToken")
    @Expose
    private String clientToken;

    public DashboardRequestBody(String blockCert, String clientToken) {
        this.blockCert = blockCert;
        this.clientToken = clientToken;
    }

    public String getBlockCert() {
        return blockCert;
    }

    public void setBlockCert(String blockCert) {
        this.blockCert = blockCert;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }
}
