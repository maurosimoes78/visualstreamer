package org.nomanscode.visualstreamer.exceptions;

//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SSEException extends RuntimeException {

    public SSEException(String message) {
        super(message);
    }

    public void setResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("{\"@class\":\"" + SSEException.class.getTypeName() + "\", \"message\":\"" + this.getMessage() + "\"}");
        response.getWriter().flush();
        response.getWriter().close();
    }

}
