package com.oneonlybob.docker.network;

import org.slf4j.helpers.MessageFormatter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

public class Request extends HttpMessage{
    private final Method method;
    private final String path;

    private Request(Method method, String path, Headers headers) {
        super(headers);
        this.method=method;
        this.path=path;
    }

    public String getPath() {
        return this.path;
    }

    public Method getMethod() {
        return this.method;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Method method;
        private String path;
        private String query;
        private String body;
        //Trust me, I'm an engineer! I think we'll put this thing right here.
        private final Headers headers = new Headers();

        public Builder() {
            this.headers.add("Accept", "application/json");
            this.headers.add("User-Agent", "VelocityPlus-Agent");
            this.headers.add("Host", "localhost");
            this.headers.add("Connection", "close");
        }

        public Builder setMethod(Method method) {
            this.method=method;
            return this;
        }

        public Builder setPath(String path, Object... obj) {
            this.path= MessageFormatter.format(path, obj).getMessage();
            return this;
        }

        public Builder setQuery(String query, Object... obj) {
            Object[] objects = Arrays.stream(obj).map(arg -> URLEncoder.encode(String.valueOf(arg), StandardCharsets.UTF_8)).toArray();
            this.query = MessageFormatter.format(query, objects).getMessage();

            return this;
        }

        public Builder setBody(String body) {
            this.body=body;
            this.headers.replace("Content-Length", String.valueOf(this.body.length()));
            return this;
        }

        public Builder headers(Consumer<Headers> consumer) {
            consumer.accept(this.headers);
            return this;
        }

        public Request build() {
            if (this.method==null)
                throw new IllegalStateException("Method must be set");

            if (this.path==null)
                throw new IllegalStateException("Path must be set");

            if (this.query!=null)
                this.path+="?"+this.query;

            Request request = new Request(this.method, this.path, this.headers);

            if (this.body!=null)
                request.setBody(this.body);

            return request;
        }
    }
}