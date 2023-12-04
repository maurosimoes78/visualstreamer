package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
public class ComplexTypes {

    @JsonIgnore
    private String html;

    @JsonIgnore
    private String script;

    @JsonIgnore
    private String classPath;

    @JsonIgnore
    private Object data;

    public ComplexTypes() {

    }

    @JsonCreator
    public ComplexTypes(@JsonProperty("html") final String html,
                        @JsonProperty("script") final String script,
                        @JsonProperty("classpath") final String classPath,
                        @JsonProperty("data") final String data) {
        this.html = html;
        this.script = script;
        this.classPath = classPath;

        if ( data != null && !data.isEmpty() ) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Class<?> cls = Class.forName(classPath);
                this.data = mapper.readValue(data, cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @JsonProperty("html")
    public String getHtml() {
        return this.html;
    }

    @JsonProperty("html")
    public void setHtml(String value) {
        this.html = value;
    }

    @JsonProperty("script")
    public String getScript() {
        return this.script;
    }

    @JsonProperty("html")
    public void setScript(String value) {
        this.script = value;
    }

    @JsonProperty("classpath")
    public String getClassPath() {
        return this.classPath;
    }

    @JsonProperty("classpath")
    public void setClassPath(String value) {
        this.classPath = value;
    }

    @JsonProperty("data")
    public Object getData() {
        return this.data;
    }

    @JsonProperty("data")
    public void setData(Object value) {
        this.data = value;
    }

    public static ComplexTypes create(String html, String script, String classPath, Object value) {
        try {
            if ( html == null || html.isEmpty()) {
                return null;
            }

            if ( value == null && classPath == null || classPath.isEmpty() ) {
                return null;
            }

            String classPath_ = classPath == null || classPath.isEmpty() ? value.getClass().getName(): classPath;

            ComplexTypes complex = new ComplexTypes();
            complex.setClassPath(classPath_);
            complex.setScript(script);
            complex.setHtml(html);
            complex.setData(value);

            return complex;
        }
        catch(Exception e) {
            return null;
        }
    }
}
