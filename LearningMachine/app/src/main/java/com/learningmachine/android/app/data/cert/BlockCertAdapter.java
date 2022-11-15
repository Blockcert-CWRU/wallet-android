package com.learningmachine.android.app.data.cert;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.learningmachine.android.app.data.cert.v11.BlockCertV11;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public class BlockCertAdapter implements JsonSerializer<BlockCert>, JsonDeserializer<BlockCert> {
    @Override
    public JsonElement serialize(BlockCert src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof BlockCertV12) {
            return context.serialize(src, BlockCertV12.class);
        } else if (src instanceof BlockCertV11) {
            return context.serialize(src, BlockCertV11.class);
        } else if (src instanceof BlockCertV20) {
            JsonElement jsonElement = context.serialize(src, BlockCertV20.class);
            // Fall 2022 xLab change: remove unnecessary fields to reduce size to avoid 413 response:
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            jsonObject.get("badge").getAsJsonObject().remove("image");
            jsonObject.remove("mDocumentNode");
            return jsonObject;
        }

        return null;
    }

    @Override
    public BlockCert deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BlockCert blockCert = null;
        JsonObject jsonObject = json.getAsJsonObject();
        if (isV20(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV20.class);
            jsonObject.remove("signature");
            blockCert.setDocumentNode(jsonObject);
        } else if (isV12(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV12.class);
            blockCert.setDocumentNode(jsonObject.getAsJsonObject("document"));
        } else if (isV11(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV11.class);
            blockCert.setDocumentNode(jsonObject);
        }
        return blockCert;
    }

    private boolean isV11(JsonObject json) {
        return allNotNull(
                json.get("certificate"),
                json.get("assertion"),
                json.get("verify"),
                json.get("recipient"),
                json.get("signature"),
                json.get("extension"));
    }

    private boolean isV12(JsonObject json) {
        return allNotNull(
                json.get("@context"),
                json.get("type"),
                json.get("document"),
                json.get("receipt"));
    }

    private boolean isV20(JsonObject json) {
        return allNotNull(
                json.get("type"),
                json.get("badge"),
                json.get("signature"),
                json.get("recipient"));
    }

    private boolean allNotNull(Object... objects) {
        return Arrays.stream(objects).allMatch(Objects::nonNull);
    }
}
