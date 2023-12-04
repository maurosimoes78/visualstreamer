package org.nomanscode.visualstreamer.process;

import reactor.core.Disposable;

public class Executer extends Thread implements Disposable {

    private String path;
    private String arguments;

    private ProcessBuilder builder;

    private Process currentProcess;

    public Executer(String path, String arguments) {
        this.path = path;
        this.arguments = arguments;
    }

    public void run() {
        try {
            //gst-launch-1.0 -v videotestsrc ! video/x-raw, height=360, width=640 ! videoconvert ! x264enc tune=zerolatency ! video/x-h264, profile=high ! mpegtsmux ! srtsink uri=srt://:8888
            String path = this.path + " \"" + arguments + "\"";
            this.builder = new ProcessBuilder(path);
            this.currentProcess = this.builder.start();
        } catch (Exception e) {

        }
    }

    public Boolean runMe () {
        try {
            this.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void dispose() {
        try {
            if (this.currentProcess.isAlive()) {
                this.currentProcess.exitValue();
            }
        }
        catch (IllegalThreadStateException e) {

        }
    }
}
