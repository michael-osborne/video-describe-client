package com.utopiarealized.videodescribe.client.service.io;

import okhttp3.RequestBody;
import okhttp3.MediaType;
import java.io.IOException;

public class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final ProgressListener progressListener;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(okio.BufferedSink sink) throws IOException {
        okio.ForwardingSink forwardingSink = new okio.ForwardingSink(sink) {
            long bytesWritten = 0L;

            @Override
            public void write(okio.Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                progressListener.onProgress(bytesWritten, contentLength());
            }
        };
        okio.BufferedSink bufferedSink = okio.Okio.buffer(forwardingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }


}
