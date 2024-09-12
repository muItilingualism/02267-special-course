package org.acme.util;

import java.io.IOException;

import io.quarkus.logging.Log;
import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) {

        final String method = context.getMethod();
        final String path = info.getPath();
        final String address = request.remoteAddress().toString();

        Log.infof("Request %s %s from IP %s", method, path, address);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        final String method = requestContext.getMethod();
        final String path = info.getPath();
        final int status = responseContext.getStatus();
        final Object entity = responseContext.getEntity();

        if (status >= 100 && status < 400) {
            Log.infof("Response %s %s returned status %d with body: %s", method, path, status, entity);
        } else if (status >= 400 && status < 500) {
            Log.warnf("Response %s %s returned status %d with error: %s", method, path, status, entity);
        } else if (status >= 500) {
            Log.errorf("Response %s %s returned status %d with error: %s", method, path, status, entity);
        }
    }
}