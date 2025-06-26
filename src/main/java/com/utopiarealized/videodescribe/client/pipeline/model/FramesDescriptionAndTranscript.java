package com.utopiarealized.videodescribe.client.pipeline.model;

import java.util.List;

public class FramesDescriptionAndTranscript extends PipelineData {
   private int videoId;
   private String transcription;
   private List<String> frameDescriptions;

   public FramesDescriptionAndTranscript(int videoId, String transcription, List<String> frameDescriptions) {
    this.videoId = videoId;
    this.transcription = transcription;
    this.frameDescriptions = frameDescriptions;
   }

   public int getVideoId() {
    return videoId;
   }

   public String getTranscription() {
    return transcription;
   }

   public List<String> getFrameDescriptions() {
    return frameDescriptions;
   }
}
