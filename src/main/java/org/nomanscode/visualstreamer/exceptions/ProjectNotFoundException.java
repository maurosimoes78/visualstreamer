package org.nomanscode.visualstreamer.exceptions;

//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public void setResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("{\"@class\":\"" + ProjectNotFoundException.class.getTypeName() + "\", \"message\":\"" + this.getMessage() + "\"}");
        response.getWriter().flush();
        response.getWriter().close();
    }
}