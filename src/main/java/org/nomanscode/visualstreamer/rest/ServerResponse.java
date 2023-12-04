package org.nomanscode.visualstreamer.rest;

import com.fasterxml.jackson.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.nomanscode.visualstreamer.common.MyHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Slf4j
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class ServerResponse {

    public enum RESPONSE_ERROR_ACTION {
        SHOW_OK,
        SHOW_SIMPLE_QUESTION,
        SHOW_COMPLEX_QUESTION
    }

    @JsonIgnore
    private Object object_ = null;
    @JsonIgnore
    private boolean error_ = true;
    @JsonIgnore
    private String message_ = "Undefined Message";
    @JsonIgnore
    private String classPath_ = "";
    @JsonIgnore
    private String subClassPath_ = "";
    @JsonIgnore
    private RESPONSE_ERROR_ACTION errorAction_ = RESPONSE_ERROR_ACTION.SHOW_OK;
    @JsonIgnore
    private static String originalCaller_ = "Unknown";

    public ServerResponse()
    {

    }

    @JsonCreator
    public ServerResponse ( @JsonProperty("object") final Object object,
                            @JsonProperty("error") final boolean error,
                            @JsonProperty("message") final String message,
                            @JsonProperty("erroraction") final RESPONSE_ERROR_ACTION errorAction) {

        this.error_ = error;
        this.message_ = message;
        if ( object != null ) {
            this.classPath_ = object.getClass().getTypeName();

            try {
                if (object instanceof ArrayList) {
                    if (((ArrayList) object).size() > 0) {
                        this.subClassPath_ = ((ArrayList) object).get(0).getClass().getTypeName();
                    }
                } else if (object instanceof LinkedHashMap) {
                    if (((LinkedHashMap) object).size() > 0) {
                        this.subClassPath_ = ((LinkedHashMap) object).get(0).getClass().getTypeName();
                    }
                } else if (object instanceof HashMap) {
                    if (((HashMap) object).size() > 0) {
                        this.subClassPath_ = ((LinkedHashMap) object).get(0).getClass().getTypeName();
                        this.subClassPath_ = ((HashMap) object).get(0).getClass().getTypeName();
                    }
                }
            }
            catch(Exception e) {

            }

        }
        this.object_ = object;
        this.errorAction_ = errorAction;

        log.info("ServerResponse: is error: {}, message: {}", this.error_ ? "true" : "false", this.message_ );
    }

    private static String getCaller(int deep) {
        try {

            /*if ( !originalCaller_.equalsIgnoreCase("unknown")) {
                return originalCaller_;
            }*/

            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length <= deep) {
                return "Unknown";
            }

            String caller = trace[deep].getClassName() + ":" + trace[deep].getMethodName();

            log.info("ServerResponse: caller is {}", caller );

            return caller;
        }
        catch(Exception e) {
            return "Unknown (Exception:" + e.getMessage() + ")";
        }
    }

    @JsonProperty("error")
    public boolean getError()
    {
        return this.error_;
    }

    @JsonProperty("message")
    public String getMessage()
    {
        return this.message_;
    }

    @JsonProperty("object")
    public Object getObject()
    {
        return this.object_;
    }

    @JsonProperty("erroraction")
    public RESPONSE_ERROR_ACTION getErrorAction()
    {
        return this.errorAction_;
    }

    @JsonProperty("payloadclasspath")
    public String getClassPath() {
        return this.classPath_;
    }

    @JsonProperty("payloadsubclasspath")
    public String getSubClassPath() {
        return this.subClassPath_;
    }

    @JsonProperty("originalcaller")
    public String getOriginalCaller() {
        return this.originalCaller_;
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final MyHolder<String> hldrMessage)
    {
        String message = "Unknown error";

        if ( hldrMessage != null ) {
            message = hldrMessage.value;
        }

        originalCaller_ = getCaller(3);
        return new ServerResponse(null, true, message, RESPONSE_ERROR_ACTION.SHOW_OK );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final MyHolder<String> hldrMessage, Object obj)
    {
        String message = "Unknown error";

        if ( hldrMessage != null ) {
            message = hldrMessage.value;
        }

        originalCaller_ = getCaller(3);
        return new ServerResponse(obj, true, message, RESPONSE_ERROR_ACTION.SHOW_OK );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final String message)
    {
       originalCaller_ = getCaller(3);
       return new ServerResponse(null, true, message, RESPONSE_ERROR_ACTION.SHOW_OK );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final String message, RESPONSE_ERROR_ACTION action)
    {
        originalCaller_ = getCaller(3);
        return new ServerResponse(null, true, message, action );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final String message, Object obj, RESPONSE_ERROR_ACTION action)
    {
        originalCaller_ = getCaller(3);
        return new ServerResponse(obj, true, message, action );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseFailed(final String message, Object obj)
    {
        originalCaller_ = getCaller(3);
        return new ServerResponse(obj, true, message, RESPONSE_ERROR_ACTION.SHOW_OK );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseSuccess(Object obj)
    {
        originalCaller_ = getCaller(3);
        return new ServerResponse(obj, false, "success", RESPONSE_ERROR_ACTION.SHOW_OK );
    }

    @JsonIgnore
    public static ServerResponse getServerResponseSuccess()
    {
        originalCaller_ = getCaller(3);
        return new ServerResponse(null, false, "success", RESPONSE_ERROR_ACTION.SHOW_OK);
    }

    @JsonIgnore
    public static ServerResponse prepareResponse(Object obj, MyHolder<String> errorMessage)
    {
        originalCaller_ = getCaller(3);

        if ( obj == null ) {
            return ServerResponse.getServerResponseFailed(errorMessage);
        }

        return ServerResponse.getServerResponseSuccess(obj);
    }

    @JsonIgnore
    public static ServerResponse create() {
        return new ServerResponse();
    }
}
