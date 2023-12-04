package org.nomanscode.visualstreamer.exceptions;

public class CybertronSDKException extends Exception {

    protected String description;
    protected String detail;

    public CybertronSDKException()
    {
        super();
    }

    public CybertronSDKException(Exception e) {
        super(e);
    }

    public CybertronSDKException(final String description) {
        super(description);
        this.description = "";
        this.detail = "";
    }

    public CybertronSDKException(final String description, final String detail)
    {
        super(description);
        this.description = description;
        this.detail = detail;
    }

    public CybertronSDKException(final String description, final String detail, Throwable cause)
    {
        super(description, cause);
        this.description = description;
        this.detail = detail;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String value)
    {
        this.detail = value;
    }
}