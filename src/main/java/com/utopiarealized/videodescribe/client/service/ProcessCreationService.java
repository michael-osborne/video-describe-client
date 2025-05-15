package com.utopiarealized.videodescribe.client.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Value;
import java.io.BufferedWriter;
import com.utopiarealized.videodescribe.utils.Utils;
import java.io.FileWriter;
import java.util.List;
import jakarta.annotation.PostConstruct;
import com.utopiarealized.videodescribe.model.ProcessModel;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class ProcessCreationService {

    private static Map<String, BlockingQueue<String>> writingQueues = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ProcessCreationService.class);

    @Value("${log.location}")
    private String logLocation;

    @PostConstruct
    public void init() {
        Utils.createDirectoryIfNotExists(logLocation);
    }

    public void createProcess(ProcessModel processModel) throws IOException, InterruptedException {
        final String processName = processModel.getCommand()[0];
        createLoggingThreadsIfDNE(processName);

        ProcessBuilder pb = new ProcessBuilder(processModel.getCommand()); 
        Process process = pb.start();
        processModel.setProcess(process);

        // Thread for stdout
        Thread outThread = new Thread(() -> readStreamToQueueAndBuffer(  processModel.getStdOutCapture(),   process.getInputStream(), Thread.currentThread().getName(), 
        writingQueues.get(processName + "-out")));
        outThread.start();

        // Thread for stderr
        Thread errThread = new Thread(() -> readStreamToQueueAndBuffer(processModel.getStdErrCapture(), process.getErrorStream(), Thread.currentThread().getName(), 
        writingQueues.get(processName + "-err")    ));
        errThread.start();

    
    }


    private synchronized void createLoggingThreadsIfDNE(String processId) {
        if (!writingQueues.containsKey(processId +"-out")) {
            BlockingQueue<String> stdOutQueue = new LinkedBlockingQueue<>();
            BlockingQueue<String> stdErrQueue = new LinkedBlockingQueue<>();
            writingQueues.put(processId +"-out", stdOutQueue);
            writingQueues.put(processId +"-err", stdErrQueue);
            Thread outThread = new Thread(() -> writeToFile(logLocation + "/" + processId + "-out.log", stdOutQueue));
            Thread errThread = new Thread(() -> writeToFile(logLocation + "/" + processId + "-err.log", stdErrQueue));
            outThread.start();
            errThread.start();
        }
    }

    private void writeToFile(String filePath, BlockingQueue<String> queue) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            try {
                while (true) {
                    String line = queue.take();
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            logger.error("Error writing to file: " + e.getMessage());
        }
    }


    private void readStreamToQueueAndBuffer(List<String> buffer, InputStream stream, String prefix, BlockingQueue<String> queue) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            try {   
                while ((line = reader.readLine()) != null) {
                    queue.put(prefix + ": " + line);
                    buffer.add(line);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            logger.error("Error reading stream to queue and buffer: " + e.getMessage());
        }
    }
} 
