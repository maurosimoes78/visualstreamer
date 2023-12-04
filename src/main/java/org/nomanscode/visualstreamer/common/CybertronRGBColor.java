package org.nomanscode.visualstreamer.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CybertronRGBColor {

    public Integer red;
    public Integer green;
    public Integer blue;
    public Double alpha;

    public CybertronRGBColor(@JsonProperty("red") final Integer red,
                             @JsonProperty("green") final Integer green,
                             @JsonProperty("blue") final Integer blue,
                             @JsonProperty("alpha") final Double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @JsonProperty("red")
    public Integer getRed() {
        return this.red;
    }

    @JsonProperty("green")
    public Integer getGreen() {
        return this.green;
    }

    @JsonProperty("blue")
    public Integer getBlue() {
        return this.blue;
    }

    @JsonProperty("alpha")
    public Double getAlpha() {
        return this.alpha;
    }

    public static CybertronRGBColor GRAY() {
        return new CybertronRGBColor(128,128,128,255.0);
    }
}
