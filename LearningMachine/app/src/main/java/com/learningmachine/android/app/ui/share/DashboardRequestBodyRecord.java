//package com.learningmachine.android.app.ui.share;
//
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//import com.learningmachine.android.app.data.cert.BlockCert;
//import com.learningmachine.android.app.data.store.pda.PDAIndexRecord;
//
//import org.immutables.gson.Gson;
//import org.immutables.value.Value;
//
//@Gson.TypeAdapters
//@Value.Immutable
//public abstract class DashboardRequestBodyRecord {
//    public static DashboardRequestBodyRecord.Builder builder() {
//        return ImmutableDashboardRequestBodyRecord.builder();
//    }
//
//    @SerializedName("blockCert")
//    private BlockCert blockCert;
//
//    @SerializedName("clientToken")
//    private String clientToken;
//
//    public abstract static class Builder {
//
//        public abstract DashboardRequestBodyRecord build();
//
//        public abstract DashboardRequestBodyRecord.Builder blockCert(BlockCert blockCert);
//
//        public abstract DashboardRequestBodyRecord.Builder clientToken(String clientToken);
//    }
//}
