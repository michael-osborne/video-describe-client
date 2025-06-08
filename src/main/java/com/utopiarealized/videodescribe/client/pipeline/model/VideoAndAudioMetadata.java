package com.utopiarealized.videodescribe.client.pipeline.model;

import com.utopiarealized.videodescribe.client.pipeline.model.child.AudioMetadata;
import com.utopiarealized.videodescribe.client.pipeline.model.child.VideoMetadata;

public class VideoAndAudioMetadata extends PipelineData {
    private DownloadResult downloadResult;
    private AudioMetadata audioMetadata;
    private VideoMetadata videoMetadata;
    

    public VideoAndAudioMetadata(DownloadResult downloadResult, AudioMetadata audioMetadata, VideoMetadata videoMetadata) {
        this.downloadResult = downloadResult;
        this.audioMetadata = audioMetadata;
        this.videoMetadata = videoMetadata;
    }

    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    public AudioMetadata getAudioMetadata() {
        return audioMetadata;
    }

    public VideoMetadata getVideoMetadata() {
        return videoMetadata;
    }
}
