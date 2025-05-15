package com.utopiarealized.videodescribe.client.pipeline.model;
import java.util.List;
import java.util.Map;

public class DescriptionResult extends PipelineData {
    private long videoId;
    private List<FrameDescription> descriptions;
    private Map<Integer, Byte> wordCounts;

    public DescriptionResult(long videoId, List<FrameDescription> descriptions, Map<Integer, Byte> wordCounts) {
        this.videoId = videoId;
        this.descriptions = descriptions;
        this.wordCounts = wordCounts;
        }


    public long getVideoId() {
        return videoId;
    }

    public List<FrameDescription> getDescriptions() {
        return descriptions;            
    }

    public Map<Integer, Byte> getWordCounts() {
        return wordCounts;
    }   

    @Override
    public String toString() {
        return "DescriptionResult{" +
                "videoId=" + videoId +
                ", descriptions=" + descriptions +  
                ", wordCounts=" + wordCounts +
                '}';
    }
}