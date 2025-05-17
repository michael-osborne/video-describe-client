# VIDEO DESCRIBE CLIENT

This is a java project that uses various open source tools (yt-dlp, ffmpeg, stable-diffusion-ui:interrogator) to download a video on the internet and create a set of descriptive keywords of that video. 


## Technical

Technically it uses Blocking Queues to create an asynchronious pipeline for processing videos posted to YouTube and other sites 
supported by yt-dlp. It queries a server (code private) for a URL of a video to process, then performs the following transformations on it:

- Downloads the video (yt-dlp)
    -  Retrieves metadata (ffprobe)
        - Uploads metdata to server
    - Creates a sped up thumbnail .webp (ffmpeg)
        - Uploads the .webp to the server
    - Grabs frames from the video (one every 6 seconds by default) (FFmpegFrameGrabber)
        - Uploads frames to server
        - Sends frames to image interrogator to convert image to description (stable-diffusion-ui: interrgator)
            - Uploads frames to server.

Each of the following lines is it's own pipeline. Indentations indicate dependecy on previous pipeline output.


### Pipelines

The pipelines are created using Java PriorityBlockingQueue(s). The consumers are must be Spring @Service objects and tagged with a custom annotation @consume(threads=#). Methods tagged with this annotation are void and take one parameter - an object that extends PipelineData. An example would be

@Consume(threads=1)
public void processFrame(FrameData frame)

For each consumer, a priority blocking queue is created for the parameter class type and stored in a List associated with the class type. Concretely, the objects look like:
    
    private Map<Class<?>, List<PriorityBlockingQueue<PipelineData>>> dataToQueues = new ConcurrentHashMap<>();

The class PipelineOrchestrator.submitData(PipelineData) will queue the object passed in to all consumers registered to consume the parameter type.

PipelineOrchestrator also allows an individual consumer to request requeuing of data using the resumbmitData(PipelineData) method. When used, it determines the consumer by walking backward in the current stack to find the @consume method that it was called from. Resubmit is used in the event of a transient error to allow reprocessing. Metadata associated with the object is then used to determine priority of re-queued objects.

This basic proof-of-concept pipeline is enough to get an extensible video description application running. Additional functionality would include:
- Using a strategy to determine # of threads/cusomization in task processors 
- More robust error processing (possibly offloading objects to disk in the event they cannot be processed or other forms of dead-letter processing)
- Possible named queues and/or self-documenting pipeline processing.

# Demos/Contact


A barebones demo is available here: https://qr-ify.com/video-srvr/login. Please contact me for login credentials