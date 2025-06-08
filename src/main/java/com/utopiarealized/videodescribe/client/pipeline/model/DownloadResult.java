package com.utopiarealized.videodescribe.client.pipeline.model;

public class DownloadResult extends PipelineData {
    private int videoId;
    private String filePath;
    private int size;

    public DownloadResult(int videoId, String filePath, int size) {
        this.videoId = videoId;
        this.filePath = filePath;
        this.size = size;
    }


    public int getVideoId() {
        return videoId;
    }


    public String getFilePath() {
        return filePath;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "DownloadResult{" +
                "videoId=" + videoId +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}