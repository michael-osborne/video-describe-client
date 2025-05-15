package com.utopiarealized.videodescribe.model;

import java.util.List;
import java.util.Collections;

import java.util.ArrayList;
import java.util.Arrays;

public class ProcessModel {

    private String[] command;
    private Process process;
    private Integer waitForCompletionInSeconds;

    private List<String> stdOutCapture = Collections.synchronizedList(new ArrayList<>());
    private List<String> stdErrCapture = Collections.synchronizedList(new ArrayList<>());


    public ProcessModel(String[] command, Integer waitForCompletion) {
        this.command = command;
        this.waitForCompletionInSeconds = waitForCompletion;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public int getExitCode() throws InterruptedException {
            if (process.isAlive()) {
                process.waitFor();
            }
         
 
        return process.exitValue();
    }


    //Nonbloccking method to check if it's still running
    public  boolean isRunning(long timeout) throws InterruptedException {
        if (process.isAlive()) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
        return process.isAlive();
    }

    public void addStdOutCapture(String line) {
        stdOutCapture.add(line);
    }   

    public void addStdErrCapture(String line) {
        stdErrCapture.add(line);
    }   
    
    public String[] getCommand() {
        return command;
    }

    public Integer getWaitForCompletionInSeconds() {
        return waitForCompletionInSeconds;
    }   
    
    public List<String> getStdOutCapture() {
        return stdOutCapture;
    }

    public List<String> getStdErrCapture() {
        return stdErrCapture;
    }

    @Override
    public String toString() {
        return "ProcessModel{" +
                "command=" + Arrays.toString(command) +
                ", waitForCompletionInSeconds=" + waitForCompletionInSeconds +
                ", stdOutCapture=" + stdOutCapture +
                ", stdErrCapture=" + stdErrCapture +
                '}';
    }
}
