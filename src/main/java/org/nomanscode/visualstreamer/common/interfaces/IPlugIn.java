package org.nomanscode.visualstreamer.common.interfaces;

//import org.nomanscode.visualstreamer.sdk.task.pin.InputPinInfo;
//import org.nomanscode.visualstreamer.sdk.task.pin.OutputPinInfo;

import java.util.UUID;

public interface IPlugIn {
    /*public List<ParameterX> getDefaultInputParameters();
    public List<ParameterX> getDefaultOutputParameters();*/
    //public String getOriginalName();
    public String getVersion();
    public String getVendor();
    public UUID getProductIdentificationId();
}
