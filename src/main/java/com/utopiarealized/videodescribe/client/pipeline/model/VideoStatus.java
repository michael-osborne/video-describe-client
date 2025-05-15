package com.utopiarealized.videodescribe.client.pipeline.model;

public class VideoStatus extends PipelineData {
    private long id;
    private String status;
    private String substatus;

    private Integer totalFrames;
    private String url;

    public VideoStatus() {}

    public VideoStatus(long id, int totalFrames, String status, String substatus, String url) {
        this.id = id;
        this.totalFrames = totalFrames;
        this.status = status;
        this.substatus = substatus;
        this.url = url;
    }

    public VideoStatus(long id, String status, String substatus) {
        this.id = id;
        this.status = status;
        this.substatus = substatus;
    }

    public long getId() {
        return id;
    }   

    public String getStatus() {
        return status;
    }

    public String getSubstatus() {
        return substatus;
    }

    public Integer getTotalFrames() {
        return totalFrames;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "VideoStatus{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", substatus='" + substatus + '\'' +
                '}';
    }
}
