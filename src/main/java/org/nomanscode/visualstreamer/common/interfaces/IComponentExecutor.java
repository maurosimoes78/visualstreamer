package org.nomanscode.visualstreamer.common.interfaces;

import org.nomanscode.visualstreamer.common.Profile;

public interface IComponentExecutor {

    Profile getProfile();
    IInputPin getInputPin(String name);
    IOutputPin getOutputPin(String name);

    long getXCoord();
    long getYCoord();
}
