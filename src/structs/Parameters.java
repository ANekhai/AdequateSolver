package structs;

import java.io.File;

public class Parameters {
    private int threadNumber = 1;
    private int breakNumber = -1;
    private String rootFolder = "tmp";
    private int threshhold = 2000; //from params file they provided

    public Parameters() {
        rootFolder = null;
    }

    public Parameters(String[] args) {
        //TODO: Create parser for arguments
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public long getBreakNumber() {
        return breakNumber;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public int getThreshhold() {
        return threshhold;
    }
}
