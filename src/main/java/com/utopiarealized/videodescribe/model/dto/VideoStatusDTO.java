package com.utopiarealized.videodescribe.model.dto;

public class VideoStatusDTO {

    public static String STATUS_PENDING = "pending";
    public static String STATUS_PROCESSING = "processing";
    public static String STATUS_COMPLETED = "completed";
    public static String STATUS_FAILED = "failed";

    public static String SUBSTATUS_NONE = "none";
    public static String SUBSTATUS_DOWNLOADING = "downloading";
    public static String SUBSTATUS_META_DATA = "meta-data";
    public static String SUBSTATUS_FRAMES = "frames";
    public static String SUBSTATUS_COMPLETE = "complete";

    private long id;
    private Integer totalFrames;
    private String status;
    private String substatus;
    private String url;

    public VideoStatusDTO() {}

    public VideoStatusDTO(long id, Integer totalFrames, String status, String substatus) {
        this(id, totalFrames, status, substatus, null);
    }
    
    public VideoStatusDTO(long id, String status, String substatus) {
        this(id, null, status, substatus, null);
    }

    public VideoStatusDTO(long id, Integer totalFrames, String status, String substatus, String url) {
        this.id = id;
        this.totalFrames = totalFrames;
        this.status = status;
        this.substatus = substatus;
        this.url = url;
    }
    
    public long getId() {
        return id;
    }

    public Integer getTotalFrames() {
        return totalFrames;
    }

    public String getStatus() {
        return status;
    }

    public String getSubstatus() {
        return substatus;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }       

    public void setStatus(String status) {
        this.status = status;
    }   

    public void setSubstatus(String substatus) {
        this.substatus = substatus;
    }
    
    @Override
    public String toString() {
        return "VideoDTO{" +
                "id=" + id +
                ", totalFrames=" + totalFrames +
                ", status='" + status + '\'' +  
                ", substatus='" + substatus + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
