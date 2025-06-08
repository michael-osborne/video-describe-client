package com.utopiarealized.videodescribe.client.pipeline.service;

import org.springframework.stereotype.Service;

import com.utopiarealized.videodescribe.client.pipeline.model.VideoAndAudioMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utopiarealized.videodescribe.model.ProcessModel;
import com.utopiarealized.videodescribe.client.service.ProcessCreationService;
import com.utopiarealized.videodescribe.client.service.io.VideoIOService;
import com.fasterxml.jackson.databind.JsonNode;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.model.dto.MetadataDTO;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;

import com.utopiarealized.videodescribe.client.annotation.Consume;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.DownloadResult;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.client.pipeline.model.child.AudioMetadata;
import com.utopiarealized.videodescribe.client.pipeline.model.child.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GetVideoMetadataService {
    @Autowired
    private ProcessCreationService processCreationService;

    @Autowired
    private VideoIOService videoIOService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;

    private static final Logger logger = LoggerFactory.getLogger(GetVideoMetadataService.class);

    @Consume(threads = 2)
    public void getVideoMetadata(DownloadResult downloadResult) {
        pipelineOrchestrator.submitData(new ProcessLog(downloadResult.getVideoId(), "Getting video metadata"));
        try {
            ProcessModel processModel = new ProcessModel(new String[] {
                    "ffprobe",
                    "-v", "error", // Suppress unnecessary logs
                    "-show_streams", // Show stream info (video/audio)
                    "-count_frames", // Count frames
                    "-print_format", "json", // Output as JSON
                    downloadResult.getFilePath()
            }, null);

            processCreationService.createProcess(processModel);

            int exitCode = processModel.getExitCode();
            if (exitCode != 0) {
                String error = "unknown error";
                if (processModel.getStdErrCapture().size() > 0) {
                    error = processModel.getStdErrCapture().get(processModel.getStdErrCapture().size() - 1);
                }
                pipelineOrchestrator.submitData(new ProcessLog(downloadResult.getVideoId(),
                        "ffprobe failed with exit code: " + exitCode + ". Error: " + error));
                pipelineOrchestrator
                        .submitData(new VideoStatus(downloadResult.getVideoId(), VideoStatusDTO.STATUS_FAILED,
                                VideoStatusDTO.SUBSTATUS_META_DATA));
                logger.error("ffprobe failed with exit code: " + exitCode + " on " + downloadResult.getFilePath());
                return;
            }

            String output = String.join("", processModel.getStdOutCapture());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(output);

            JsonNode videoStream = null;
            JsonNode audioStream = null;

            // Find video and audio streams
            for (JsonNode stream : json.get("streams")) {
                String codecType = stream.get("codec_type").asText();
                if ("video".equals(codecType) && videoStream == null) {
                    videoStream = stream;
                } else if ("audio".equals(codecType) && audioStream == null) {
                    audioStream = stream;
                }
            }

            if (videoStream == null) {
                logger.error("No video stream found in the MP4 file.");
                pipelineOrchestrator.submitData(new ProcessLog(downloadResult.getVideoId(),
                        "ffprobe failed. Error: No video stream found in the MP4 file."));
                pipelineOrchestrator
                        .submitData(new VideoStatus(downloadResult.getVideoId(), VideoStatusDTO.STATUS_FAILED,
                                VideoStatusDTO.SUBSTATUS_META_DATA));
                return;
            }

            // Extract video metadata with safety checks
            int width = videoStream.get("width").asInt();
            int height = videoStream.get("height").asInt();
            Double duration = videoStream.get("duration") == null ? null : videoStream.get("duration").asDouble();
            String fpsStr = videoStream.get("r_frame_rate").asText();
            long totalFrames = videoStream.get("nb_read_frames").asLong();
            double fps = evalFrameRate(fpsStr);

            // Extract audio metadata (if audio stream exists)
            String audioCodec = null;
            int sampleRate = 0;
            int channels = 0;
            int audioBitrate = 0;
            double audioDuration = 0;

            if (audioStream != null) {
                audioCodec = audioStream.get("codec_name").asText("");
                sampleRate = audioStream.get("sample_rate").asInt(0);
                channels = audioStream.get("channels").asInt(0);
                audioBitrate = audioStream.get("bit_rate").asInt(0);
                audioDuration = audioStream.get("duration") == null ? null : audioStream.get("duration").asDouble();
            } else {
                logger.warn("No audio stream found in the MP4 file for video ID: " + downloadResult.getVideoId());
                pipelineOrchestrator.submitData(new ProcessLog(downloadResult.getVideoId(),
                        "No audio stream found in the MP4 file"));
            }

            // File size
            String filePath = downloadResult.getFilePath();
            File file = new File(filePath);
            int size = (int) file.length();

            // Fallback duration calculation if video duration is missing
            if (duration == null) {
                duration = totalFrames / fps;
            }

            pipelineOrchestrator.submitData(new ProcessLog(downloadResult.getVideoId(), "Sending metadata to server"));


            VideoAndAudioMetadata videoAndAudioMetadata = new VideoAndAudioMetadata(downloadResult,
                   audioStream != null ? new AudioMetadata(audioCodec, sampleRate, channels, audioBitrate, audioDuration) : null,
                    new VideoMetadata(width, height, duration, fps, totalFrames));
            pipelineOrchestrator.submitData(videoAndAudioMetadata);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error getting video metadata: " + e.getMessage());
            logger.error("Stack trace: " + e.getStackTrace());
        }
    }

    private static double evalFrameRate(String frameRate) {
        try {
            String[] parts = frameRate.split("/");
            double numerator = Double.parseDouble(parts[0]);
            double denominator = Double.parseDouble(parts[1]);
            return numerator / denominator;
        } catch (Exception e) {
            return 0.0; // Default to 0 if parsing fails
        }
    }
}
