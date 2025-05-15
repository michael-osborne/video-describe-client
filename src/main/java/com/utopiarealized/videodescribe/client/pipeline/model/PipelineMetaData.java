package com.utopiarealized.videodescribe.client.pipeline.model;

public class PipelineMetaData {
    private int retries = 0;
    private long createdAt = System.currentTimeMillis();

    public int getRetries() {
        return retries;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
