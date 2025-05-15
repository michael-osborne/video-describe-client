package com.utopiarealized.videodescribe.client.pipeline;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

import com.utopiarealized.videodescribe.client.pipeline.model.PipelineData;

import jakarta.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PipelineOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(PipelineOrchestrator.class);

    private Map<Method, ExecutorService> executors = new ConcurrentHashMap<>();

    //key:"className.methodName"
    private Map<String, PriorityBlockingQueue<PipelineData>> methodToQueue = new ConcurrentHashMap<>();
    private Map<Class<?>, List<PriorityBlockingQueue<PipelineData>>> dataToQueues = new ConcurrentHashMap<>();

    public void buildPipelineConsumer(int threads, Object service, Method method, Class<?> parameterType) {
        PriorityBlockingQueue<PipelineData> queue = new PriorityBlockingQueue<>(1000);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        

        dataToQueues.computeIfAbsent(parameterType, k -> new ArrayList<>()).add(queue);
        methodToQueue.put(method.getDeclaringClass().getName() + "." + method.getName(), queue);
        executors.put(method, executor);

        // Create consumer function that will be executed for each item
        Consumer<PipelineData> processItem = data -> {
            try {
                logger.info("[" + Thread.currentThread().getName() + "] Processing item: " + data + " on method: " + method.getName());
                method.invoke(service, data);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Failed to invoke method: " + method.getName()
                        + " on service: " + service.getClass().getName(), e);
            } catch (Exception e) {
                logger.error("[" + Thread.currentThread().getName() + "] Error invoking method: " + method.getName()
                + " on service: " + service.getClass().getName(), e);
            }
        };
        

        // Start multiple consumers - one per thread
        for (int i = 0; i < threads; i++) {
            startProcessingConsumer(executor, queue, processItem);
        }
    }

    public void buildPipelineProducer(int threads, Object service, Method method) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Runnable producer = () -> {
                try {
                   method.invoke(service);
               } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to invoke method: " + method.getName()
                            + " on service: " + service.getClass().getName(), e);
               } catch (Exception e) {
                logger.error("[" + Thread.currentThread().getName() + "] Error invoking method: " + method.getName()
                + " on service: " + service.getClass().getName(), e);
               }
        };
        startProcessingProducer(executor, producer);
    }

    private <T> void startProcessingConsumer(ExecutorService executor, BlockingQueue<T> queue, Consumer<T> processor) {
        // Create a single consumer task
        CompletableFuture.runAsync(() -> {
            String threadName = Thread.currentThread().getName();
            logger.info("Starting consumer on thread: " + threadName);
            while (!Thread.currentThread().isInterrupted() && !executor.isShutdown()) {
                try {
                    T item = queue.take();
                    logger.info("[" + threadName + "] Processing item: " + item);
                    processor.accept(item);
                
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // Log error but continue processing
                    logger.error("[" + threadName + "] Error processing item: " + e.getMessage());
                    logger.error("[" + threadName + "] Stack trace: " + e.getStackTrace());
                }
            }
        }, executor);
    }

    private <T> void startProcessingProducer(ExecutorService executor,  Runnable producer) {
        // Create a single consumer task
        CompletableFuture.runAsync(producer, executor);
    }

    @PreDestroy
    public void shutdown() {
        executors.values().forEach(executor -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        });
    }

    public void submitData(PipelineData data) {
        List<PriorityBlockingQueue<PipelineData>> queues = dataToQueues.get(data.getClass());
        if (queues != null) {
            for (PriorityBlockingQueue<PipelineData> queue : queues) {
                if (!queue.add(data)) {
                    // TODO:Handle queue full scenario
                }
            }
        } else {
            throw new RuntimeException("No consumer for data class: " + data.getClass().getName());
        }
    }

    // Resubmit data to the same consumer only
    // Resubmit to the beginning of the queue
    public void resumbmitData(PipelineData data) {
        data.getMetaData().setRetries(data.getMetaData().getRetries() + 1);
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
         String classAndMethodName = stackTrace[2].getClassName() + "." + stackTrace[2].getMethodName();
         PriorityBlockingQueue<PipelineData> queue = methodToQueue.get(classAndMethodName);
         if (queue != null) {
            queue.offer(data);
         } else {
            throw new RuntimeException("No consumer for data class: " + data.getClass().getName());
         }
    }
}
