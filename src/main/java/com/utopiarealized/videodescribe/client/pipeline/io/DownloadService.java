package com.utopiarealized.videodescribe.client.pipeline.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.utopiarealized.videodescribe.utils.Utils;

import org.springframework.beans.factory.annotation.Value;
import com.utopiarealized.videodescribe.model.ProcessModel;

import java.nio.file.Path;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import com.utopiarealized.videodescribe.client.pipeline.model.VideoStatus;
import com.utopiarealized.videodescribe.client.service.ProcessCreationService;
import com.utopiarealized.videodescribe.client.service.io.*;
import com.utopiarealized.videodescribe.client.annotation.Produce;
import com.utopiarealized.videodescribe.client.pipeline.PipelineOrchestrator;
import com.utopiarealized.videodescribe.client.pipeline.model.DownloadTask;
import com.utopiarealized.videodescribe.client.pipeline.model.ProcessLog;
import com.utopiarealized.videodescribe.model.dto.VideoStatusDTO;
import com.utopiarealized.videodescribe.client.pipeline.model.DownloadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DownloadService {
    private static final String[] FAILURE_STRINGS = {
            ".*ERROR: \\[twitter\\] \\d+: No video could be found in this tweet$",
            ".*ERROR: \\[twitter\\] \\d+: NSFW tweet requires authentication.$",
            ".*ERROR: \\[twitter\\] \\d+: Video .. is unavailable$"

    };

    @Autowired
    private ProcessCreationService processCreationService;

    private static final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private VideoIOService videoIOService;

    @Autowired
    private PipelineOrchestrator pipelineOrchestrator;
    private long STATUS_CHECK_INTERVAL_MS = 2500;

    private static final int DOWNLOAD_TIMEOUT_SECONDS = 300;

    @Value("${download.dir}")
    private String downloadDir = "/home/m/vid/";

    @Produce(threads = 2)
    public void downloadNextVideo() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                VideoStatusDTO videoDTO = videoIOService.getNextVideo();
                logger.info("[" + Thread.currentThread().getName() + "] Downloading video " + videoDTO);
                if (videoDTO == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    downloadVideo(new DownloadTask(videoDTO.getUrl(), videoDTO.getId()));
                }
                // Catch um all so the thread doesn't die.
            } catch (Exception e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

    }

    
    public void downloadVideo(DownloadTask downloadTask) throws IOException, InterruptedException {
        String url = downloadTask.getUrl();
        int videoId = downloadTask.getVideoId();
        Path videoDir = Utils.createDirectoryFromUrl(url, downloadDir, String.valueOf(videoId));
        String filePath = videoDir.resolve(videoId + ".mp4").toString();
        
        pipelineOrchestrator.submitData(new VideoStatus(videoId, VideoStatusDTO.STATUS_PROCESSING, VideoStatusDTO.SUBSTATUS_DOWNLOADING));
        pipelineOrchestrator.submitData(new ProcessLog(videoId, "Downloading video from " + url));

        ProcessModel processModel = new ProcessModel(new String[] { "yt-dlp",
                "-o", filePath, "--no-playlist", url }, DOWNLOAD_TIMEOUT_SECONDS);
        processCreationService.createProcess(processModel);
        String prevLine = null;
        while (processModel.isRunning(STATUS_CHECK_INTERVAL_MS)) {

            int intervalsFromPing = 0;
            String lastDownloadLine = Utils.findLastDownloadLine(processModel.getStdOutCapture());
            if (lastDownloadLine != null) {
                if (!lastDownloadLine.equals(prevLine) || intervalsFromPing > 4) {
                    pipelineOrchestrator.submitData(new ProcessLog(videoId, lastDownloadLine));
                    prevLine = lastDownloadLine;
                }
            }
        }
        int exitCode = processModel.getExitCode();
        Path videoFile = Paths.get(filePath);
        if (exitCode != 0 || !Files.exists(videoFile)) {
            boolean permanentFailure = false;
            for (String log : processModel.getStdErrCapture()) {
                for (String failureString : FAILURE_STRINGS) {
                    if (log.matches(failureString)) {
                        pipelineOrchestrator.submitData(new VideoStatus(videoId, VideoStatusDTO.STATUS_FAILED,
                                VideoStatusDTO.SUBSTATUS_DOWNLOADING));
                        permanentFailure = true;
                    }
                }
            }
            if (permanentFailure) {
                pipelineOrchestrator.submitData(new ProcessLog(videoId, "Download failed - permanent failure"));
            }
            pipelineOrchestrator.submitData(
                    new VideoStatus(videoId, VideoStatusDTO.STATUS_FAILED, VideoStatusDTO.SUBSTATUS_DOWNLOADING));
            pipelineOrchestrator.submitData(new ProcessLog(videoId, "Download failed for unknown reason"));
            logger.error("Download failed for video " + videoId);
        }
        pipelineOrchestrator.submitData(new DownloadResult(videoId, filePath, (int) Files.size(videoFile)));
        pipelineOrchestrator.submitData(new ProcessLog(videoId, "Download successful"));
    }

  
}