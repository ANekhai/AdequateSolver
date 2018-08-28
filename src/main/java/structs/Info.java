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
    private int frequency = 1;
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
        rootDirectory = params.getRootFolder();
        result = new int[threadNumber];
        countIterations = new int[threadNumber];
        threadCount = new int[threadNumber];
        total = new int[threadNumber];
//        time = new long[thread];
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
        this.maxElementSize = params.getThreshold();
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

    public void incrementFileCheck(int thread, int upperBound) {
        ++this.fileCheck[thread][upperBound];
    }

    public void decrementFileCheck(int thread, int upperBound) {
        --this.fileCheck[thread][upperBound];
    }

    public int getFileCheck(int thread, int upperBound) {
        return fileCheck[thread][upperBound];
    }

    //GETTERS AND SETTERS
    public boolean isStarted() {
        return started;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public int getMaxLower() {
        return maxLower[0];
    }

    public int getMaxUpper() {
        return maxUpper[0];
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setMaxUpper(int maxUpper) {
        this.maxUpper[0] = maxUpper;
    }

    public void setMaxLower(int maxLower) {
        this.maxLower[0] = maxLower;
    }

    //Member functions

    public void markStarted() {
        started = true;
    }

    public void addIteration() {
        ++countIterations[0];
    }

    public boolean checkBreakNumber(int thread) {
        if (breakNumber == -1) {
            return false;
        } else {
            return countIterations[thread] > breakNumber;
        }

    }

    public void markFinished() {
        globalFinished = true;
    }

    public void setUpperBound(int thread, int value) {
        this.maxUpper[thread] = value;
    }

    public void setLowerBound(int thread, int value) {
        this.maxLower[thread] = value;
    }

    public int getUpperBound(int thread) {
        return maxUpper[thread];
    }


    public int getLowerBound(int thread) {
        return maxLower[thread];
    }

    public int getMaxElementSize() {
        return maxElementSize;
    }

    public boolean isLocked(int thread) {
        return isLocked[thread];
    }

    public void incrementCount(int thread) {
        ++this.countIterations[thread];
    }

    public void setRoot() {
        this.isRoot = true;
    }

    public void decrementMaxUpper() {
        --this.maxUpper[0];
    }

    public void incrementSubgraphNumber() {
        ++this.numberAS;
    }

    public boolean getKernel() {
        return kernel;
    }

    public void setKernel() {
        kernel = true;
    }

    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    public int getCount(int thread) {
        return this.threadCount[thread];
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void resetTotal(int thread) {
        this.total[thread] = 0;
    }
    
    public void setTotal(int thread, int value) {
        
    }

    public boolean isFinished() {
        return globalFinished;
    }

    public void decrementTotal(int thread) {
        --total[thread];
    }

    public void unlock(int i) {
        isLocked[i] = false;
    }

    public void lock(int i) {
        isLocked[i] = true;
    }

    public int getThreadTotal(int thread, int maxUpper) {
        return threadTotal[thread][maxUpper];
    }

    public void setParallel() {
        isParallel = true;
    }

    public void decreaseTotal(int thread, int value) {
        this.total[thread] -= value;
    }

    public void increaseTotal(int thread, int value) {
        this.total[thread] += value;
    }

    public void decreaseTotal(int thread, int upperBound, int value) {
        this.threadTotal[thread][upperBound] -= value;
    }

    public void increaseTotal(int thread, int upperBound, int value) {
        this.threadTotal[thread][upperBound] += value;
    }

    public boolean isParallel() {
        return parallel;
    }

    public int checkRunning() {
        int result = 0;
        int sumCount = 0;
        for (int i = 0; i < this.threadNumber; ++i) {
            if (this.maxLower[i] < this.maxUpper[i] && this.countIterations[i] <= this.breakNumber ) {
                result++;
                sumCount += this.countIterations[i];
            }
        }
        if (sumCount >= this.breakNumber){
            this.globalFinished = true;
        }
        return result;
    }

    public void decrementUpperBound(int thread) {
        --this.maxUpper[thread];
    }
}
