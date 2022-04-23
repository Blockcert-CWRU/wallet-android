package com.learningmachine.android.app.ui.share;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.cert.BlockCert;

public class DashboardRequestBody {

    @SerializedName("blockcert")
    @Expose
    private BlockCert blockCert;

    @SerializedName("clientToken")
    @Expose
    private String clientToken;

    public DashboardRequestBody(BlockCert blockCert, String clientToken) {
        this.blockCert = blockCert;
        this.clientToken = clientToken;
    }

    public BlockCert getBlockCert() {
        return blockCert;
    }

    public void setBlockCert(BlockCert blockCert) {
        this.blockCert = blockCert;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }
}
