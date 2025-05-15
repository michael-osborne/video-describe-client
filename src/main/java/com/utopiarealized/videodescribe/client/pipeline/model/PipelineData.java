package com.utopiarealized.videodescribe.client.pipeline.model;

public class PipelineData implements Comparable<PipelineData> {
    private PipelineMetaData metaData = new PipelineMetaData()  ;

    public PipelineData() {
    }

    public PipelineMetaData getMetaData() {
        return metaData;
    }

    @Override
    public int compareTo(PipelineData o) {
        if (metaData.getRetries() < 10 ) {
            return Long.compare(metaData.getCreatedAt(), o.metaData.getCreatedAt());
        }
        return Long.compare(0, o.metaData.getRetries());
    }
}
