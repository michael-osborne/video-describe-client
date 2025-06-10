package com.utopiarealized.videodescribe.client.pipeline.model;

import java.util.List;

public class FramesDescriptionAndTranscript extends PipelineData {
   private int videoId;
   private String description;
   private List<String> frameDescriptions;

   public FramesDescriptionAndTranscript(int videoId, String description, List<String> frameDescriptions) {
    this.videoId = videoId;
    this.description = description;
    this.frameDescriptions = frameDescriptions;
   }

   public int getVideoId() {
    return videoId;
   }

   public String getDescription() {
    return description;
   }

   public List<String> getFrameDescriptions() {
    return frameDescriptions;
   }
}
