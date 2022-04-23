package com.learningmachine.android.app.data.cert;

public class BlockcertClass {

    private String CertUid;
    private String CertName;
    private String CertDescription;
    private String IssuerId;
    private String IssueDate;
    private String Url;
    private String RecipientPublicKey;
    private String VerificationPublicKey;
    private String SourceId;
    private String MerkleRoot;
    private String Metadata;
    private String ReceiptHash;
    private String ExpirationDate;

    public BlockcertClass(String certUid, String certName, String certDescription, String issuerId, String issueDate, String url, String recipientPublicKey, String verificationPublicKey, String sourceId, String merkleRoot, String metadata, String receiptHash, String expirationDate) {
        CertUid = certUid;
        CertName = certName;
        CertDescription = certDescription;
        IssuerId = issuerId;
        IssueDate = issueDate;
        Url = url;
        RecipientPublicKey = recipientPublicKey;
        VerificationPublicKey = verificationPublicKey;
        SourceId = sourceId;
        MerkleRoot = merkleRoot;
        Metadata = metadata;
        ReceiptHash = receiptHash;
        ExpirationDate = expirationDate;
    }


    public String getCertUid() {
        return CertUid;
    }

    public void setCertUid(String certUid) {
        CertUid = certUid;
    }

    public String getCertName() {
        return CertName;
    }

    public void setCertName(String certName) {
        CertName = certName;
    }

    public String getCertDescription() {
        return CertDescription;
    }

    public void setCertDescription(String certDescription) {
        CertDescription = certDescription;
    }

    public String getIssuerId() {
        return IssuerId;
    }

    public void setIssuerId(String issuerId) {
        IssuerId = issuerId;
    }

    public String getIssueDate() {
        return IssueDate;
    }

    public void setIssueDate(String issueDate) {
        IssueDate = issueDate;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getRecipientPublicKey() {
        return RecipientPublicKey;
    }

    public void setRecipientPublicKey(String recipientPublicKey) {
        RecipientPublicKey = recipientPublicKey;
    }

    public String getVerificationPublicKey() {
        return VerificationPublicKey;
    }

    public void setVerificationPublicKey(String verificationPublicKey) {
        VerificationPublicKey = verificationPublicKey;
    }

    public String getSourceId() {
        return SourceId;
    }

    public void setSourceId(String sourceId) {
        SourceId = sourceId;
    }

    public String getMerkleRoot() {
        return MerkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        MerkleRoot = merkleRoot;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String metadata) {
        Metadata = metadata;
    }

    public String getReceiptHash() {
        return ReceiptHash;
    }

    public void setReceiptHash(String receiptHash) {
        ReceiptHash = receiptHash;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }
}
