package structs;

public class Info {

    private int countIterations[];
    private int numberAS = 0;
    private int kernelSize = 0;
    private boolean kernel = false;
    // TODO: figure these out
    private int threadCount[];
    private int total[];
    private boolean isRoot = false;
    private String rootDirectory;
    private int maxUpper[];
    private int maxLower[];
    private boolean started = false;
    private int iterator = 0;
    private int originalGeneNumber;
    // private BufferedWriter traceWriter;
    private boolean parallel = false;
    private int result[];
    private int threadNumber = 0;
    // buncha time checkers
//    public long time[];
//    public long io_time[];
//    public long bound_time[];
//    public long vec_time[];
//    public long as_time[];
//    public long other_time[];
//    public long thread_time[];
    //parallel functionality checks
    private boolean active[];
    private volatile boolean isLocked[];
    private int fileCheck[][];
    private int fileBase;
    private long breakNumber;
    private int frequency = 0;
//    private long break_num;
//    private boolean isBuffered;
    private int maxElementSize;
    private int averageNodeNumber;
//    private boolean enableTrace = false;
    private boolean isParallel = false;
    private int threadTotal[][];
//    private float spaceUsage[][];
//    private float actualUsage[][];
//    private float actualBytes = 0;
//    private boolean isZero;
    private boolean globalFinished = false;


    public Info(Parameters params) {
        threadNumber = params.getThreadNumber();
        maxUpper = new int[threadNumber];
        maxLower = new int[threadNumber];
        // rootDirectory = params.getRootDirectory();
        rootDirectory = "temp";
        result = new int[threadNumber];
        countIterations = new int[threadNumber];
        threadCount = new int[threadNumber];
        total = new int[threadNumber];
//        time = new long[threadNumber];
//        bound_time = new long[p.th_num];
//        as_time = new long[p.th_num];
//        vec_time = new long[p.th_num];
//        other_time = new long[p.th_num];
//        thread_time = new long[p.th_num];
        active = new boolean[threadNumber];
        isLocked = new boolean[threadNumber];
        breakNumber = params.getBreakNumber();
//        this.freq = p.check_freq;
//        this.is_buffered = p.is_buffered;
        this.maxElementSize = params.getThreshhold();
//        this.avg_node_num = p.avg_node_num;
//        this.enable_trace = p.enable_trace;
//        space_usage = new float[p.th_num][3];
//        actual_usage = new float[p.th_num][3];
//        actual_byte = 0;
//        this.is_zero=p.is_zero;
    }

    public void initFileCheck(int upperBound, int lowerBound) {
        this.fileCheck = new int[threadNumber][upperBound + 1];
        this.fileBase = lowerBound;
    }

    public void incrementFileCheck(int threadID, int upperBound) {
        ++this.fileCheck[threadID][upperBound];
    }

    public void decrementFileCheck(int threadID, int upperBound) {
        --this.fileCheck[threadID][upperBound];
    }

    public int getFileCheck(int threadID, int upperBound) {
        return fileCheck[threadID][upperBound];
    }

    //GETTERS AND SETTERS
    public boolean isStarted() { return started; }

    public int getThreadNumber() { return threadNumber; }

    public int getMaxLower() { return maxLower[0]; }

    public int getMaxUpper() { return maxUpper[0]; }

    public String getRootDirectory() { return rootDirectory; }

    public void setMaxUpper(int maxUpper) { this.maxUpper[0] = maxUpper; }

    public void setMaxLower(int maxLower) { this.maxLower[0] = maxLower; }

    //Member functions

    public void markStarted() { started = true; }

    public void addIteration() { ++countIterations[0]; }

    public boolean checkBreakNumber() {
        if (breakNumber == -1) {
            return false;
        } else {
            return countIterations[0] > breakNumber;
        }

    }

    public void markFinished() { globalFinished = true; }

    public void setThreadMaxUpper(int threadID, int value) { this.maxUpper[threadID] = value; }

    public void setThreadMaxLower(int threadID, int value) { this.maxLower[threadID] = value; }

    public int getThreadMaxUpper(int threadID) { return maxUpper[threadID]; }


    public int getThreadMaxLower(int threadID) { return maxLower[threadID]; }

    public int getMaxElementSize() { return maxElementSize; }

    public boolean isLocked(int threadID) { return isLocked[threadID]; }

    public void incrementCount(int thread) { ++this.countIterations[thread]; }

    public void setRoot() { this.isRoot = true; }

    public void decrementMaxUpper() { --this.maxUpper[0]; }

    public void incrementSubgraphNumber() { ++this.numberAS; }

    public boolean getKernel() { return kernel; }

    public void setKernel() { kernel = true; }

    public void setKernelSize(int kernelSize) { this.kernelSize = kernelSize; }

    public int getCount(int threadNumber) { return this.threadCount[threadNumber]; }

    public int getFrequency() {return this.frequency; }

    public void setTotal(int threadNumber) { this.total[threadNumber] = 0; }

    public boolean isFinished() { return globalFinished; }

    public void decrementTotal(int threadNumber) { --total[threadNumber]; }

    public void unlock(int i) { isLocked[i] = false; }

    public void lock(int i) { isLocked[i] = true; }

    public int getThreadTotal(int thread, int maxUpper) { return threadTotal[thread][maxUpper]; }

    public void setParallel() { isParallel = true; }
}
