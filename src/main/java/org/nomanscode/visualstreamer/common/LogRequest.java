package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.*;
import org.nomanscode.visualstreamer.common.*;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,  property = "@class")
@Data
public class LogRequest {

    private Date startDate = null;
    private Date endDate = null;
    private String subject = null;
    private List<ErrorLevel> levels = null;
    private String nodeName = null;

    public LogRequest( final String startDate,
                       final String endDate,
                       final String subject,
                       final String levels,
                       final String nodeName) {


        this.startDate = startDate.equals("null") || startDate.equals("undefined") ? null : this.convertStringToDate(startDate);
        this.endDate = endDate.equals("null") || endDate.equals("undefined") ? null : this.convertStringToDate(endDate);
        this.subject = subject.equals("null") || subject.equals("undefined") ? null : subject;
        this.nodeName = nodeName.equals("null") || nodeName.equals("undefined") ? null : nodeName;

        this.levels = levels.equals("null") || levels.equals("undefined") ? new ArrayList<>() : Arrays.stream(levels.split(";")).map(ErrorLevel::valueOf).collect(Collectors.toList());
    }

    @JsonCreator
    public LogRequest(  @JsonProperty("startdate") final Date startDate,
                        @JsonProperty("enddate") final Date endDate,
                        @JsonProperty("subject") final String subject,
                        @JsonProperty("levels") final List<ErrorLevel> levels,
                        @JsonProperty("nodeName") final String nodeName) {

        if (Objects.nonNull(startDate)) {
            this.startDate = startDate;
        }
        if (Objects.nonNull(endDate)) {
            this.endDate = endDate;
        }
        if (Objects.nonNull(subject)) {
            this.subject = subject;
        }
        if (Objects.nonNull(levels)) {
            this.levels = levels;
        }

        if (Objects.nonNull(nodeName)) {
            this.nodeName = nodeName;
        }
    }

    public Date convertStringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
