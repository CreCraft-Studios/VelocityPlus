package com.oneonlybob.docker.network;

public class Response extends HttpMessage{
    public int statusCode=StatusCodes.INTERNAL_SERVER_ERROR;
    public String reason="Exception";

    public boolean ok() {
        return this.statusCode==StatusCodes.OK;
    }

    public boolean noContent() {
        return this.statusCode==StatusCodes.NO_CONTENT;
    }

    public boolean notFound() {
        return this.statusCode==StatusCodes.NOT_FOUND;
    }

    public boolean serverError() {
        return this.statusCode==StatusCodes.INTERNAL_SERVER_ERROR;
    }
}