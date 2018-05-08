package structs;

import detector.Detector;
import graphs.BPGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Element {
    private char[] writeBuffer;

    private String[][] parent;
    private int[][] parentSize;
    private int[][] remainingParents;
    private int parentIndex;
    private int parentCheck;

    private String[][] child;
    private int[][] childSize;
    private int childIndex;
    private int childCheck;

    private int elementSize;
    private int elementOtherSize;
    private int maxElementNumber;
    private int upperBound;

    private boolean expanded;
    private boolean first;
    private boolean available[];
    private boolean full[];

    private int finalParentIndex[];
    private int finalParentCheck[];
    private int finalChildIndex[];
    private int finalChildCheck[];
//    int timeStamp[];

    int currentPosition;
    int bufferSize;

//    int globalTime;

    FileWriter writer;

    //TODO: MAKE SURE BUFFER SIZE WORKS
    public Element(Info info, int upperBound, int threadID) {
        maxElementNumber = info.getMaxElementSize();
        expanded = false;
        elementSize = maxElementNumber * 20;
        elementOtherSize = maxElementNumber + 1;
        this.upperBound = upperBound;
        //TODO: look more into this
//        info.threadTotal[threadID][upperBound] = 0;
        this.first = true;
        this.currentPosition = 0;
        this.bufferSize = 4;

    }

    public void expand() {
        this.parent = new String[bufferSize][elementSize];
        this.parentIndex = 0;
        this.parentCheck = 0;
        this.parentSize = new int[bufferSize][elementOtherSize];
        this.remainingParents = new int[bufferSize][elementOtherSize];
        this.writeBuffer = new char[elementSize * 2];

        this.child = new String[bufferSize][elementSize];
        this.childSize = new int[bufferSize][elementOtherSize];

        this.available = new boolean[bufferSize];
        this.full = new boolean[bufferSize];

        this.finalParentIndex = new int[bufferSize];
        this.finalParentCheck = new int[bufferSize];
        this.finalChildIndex = new int[bufferSize];
        this.finalChildCheck = new int[bufferSize];

        for (int i = 0; i < bufferSize; ++i) {
            available[i] = true;
            full[i] = false;
        }

        this.childIndex = 0;
        this.childCheck = 0;
        this.expanded = true;

        //TODO: add back time stuff when needed

    }

    public void addNode(BPGraph graph, Detector detector, int start, int end, Info info, int threadID) {
        if (!this.expanded)
            this.expand();

        if(this.childCheck >= info.getMaxElementSize()) {
            this.switchWritePosition(info, threadID);
        }

        //add parent
        if (this.first) {
            for (int i = 0; i < graph.getTempSubgraphsSize(); ++i) {
                this.parent[this.currentPosition][parentIndex] = graph.getTempSubgraphVertex(i);
                ++this.parentIndex;
            }
            this.first = false;
            this.parentSize[this.currentPosition][parentCheck] = graph.getTempSubgraphsSize();
            //TODO: this might need to be changed for duplicated case
            this.remainingParents[this.currentPosition][parentCheck] = 1;
            ++parentCheck;
        } else
            this.remainingParents[this.currentPosition][parentCheck - 1] += 1;

        //add Child
        for (int i = start; i < end; ++i) {
            this.child[this.currentPosition][childIndex] = detector.getSubgraphVertex(i);
            ++this.childIndex;
        }
        this.childSize[this.currentPosition][childCheck] = (end - start);
        ++childCheck;
        //TODO: check this
//        info.threadTotal[threadID][upperBound]++;

    }

    private void switchWritePosition(Info info, int threadID) {
        toIndex();
        this.currentPosition = detectAvailableWritePosition();
//        this.time_stamp[this.cur_pos] = (++this.global_time);
        if (this.amountFull() >= 2) {
            FileThread fileThread = new FileThread(threadID, info, this, true);

            fileThread.run();
        }
        this.refresh();
    }

    private void toIndex() {
        full[currentPosition] = true;
        finalParentIndex[currentPosition] = parentIndex;
        finalParentCheck[currentPosition] = parentCheck;
        finalChildIndex[currentPosition] = childIndex;
        finalChildCheck[currentPosition] = childCheck;

        parentIndex = 0;
        parentCheck = 0;
        childIndex = 0;
        childCheck = 0;
    }

    private int detectAvailableWritePosition() {
        int position = -1;
        for (int i = 0; i < this.bufferSize; ++i) {
            if (!(this.full[i]) && this.available[i]) {
//                if (this.timeStamp[i] < time) {
//                    time = this.time_stamp[i];
//                    pos = i;
//                }
                position = i;
            }
        }
        return position;
    }

    public boolean getNode(BPGraph graph, Info info, int threadID) {
        if (this.childCheck == 0) {
            if (!this.switchReadPosition(info, threadID))
                return false;
        }
        //get parent
        int parentStart = parentIndex - parentSize[this.currentPosition][parentCheck-1];
        for (int i = parentStart; i < parentIndex; ++i) {
            graph.addTempSubgraphVertex(parent[this.currentPosition][i]);
        }
        --remainingParents[this.currentPosition][parentCheck - 1];

        if (remainingParents[this.currentPosition][parentCheck - 1] == 0) {
            --parentCheck;
            parentIndex = parentStart;
        }

        //get child
        int childStart = childIndex - childSize[this.currentPosition][childCheck - 1];
        for (int i = childStart; i < childIndex; ++i) {
            graph.addTempSubgraphVertex(child[this.currentPosition][i]);
        }
        childIndex = childStart;
        --childCheck;
//        info.th_total[thread_id][ub]--;
        return true;
    }

    private boolean switchReadPosition(Info info, int threadID) {
        this.currentPosition = detectAvailableReadPosition();
        if (this.currentPosition != -1) {
            full[currentPosition] = false;
            parentIndex = finalParentIndex[currentPosition];
            parentCheck = finalParentCheck[currentPosition];
            childIndex = finalChildIndex[currentPosition];
            childCheck = finalChildCheck[currentPosition];
        } else {
            if (info.f_check[threadID][ub] == 0) {
                return false;
            } else {
                this.currentPosition = 0;
                this.fromFile(threadID, info);
            }
        }

        if (this.amountFull() < 2) {
            //read files
            FileThread fileThread = new FileThread (threadID, info, this, false);
            fileThread.run();
        }
        return true;
    }

    private int detectAvailableReadPosition() {

        int position = -1;
        for (int i = 0; i < this.bufferSize; ++i) {
            if (this.full[i] && this.available[i])
                position = i;
        }
        return position;
    }

    private int amountFull() {
        int result = 0;
        for (int i = 0; i < this.bufferSize; ++i) {
            if (this.full[i])
                ++result;
        }
        return result;
    }

    public void fork(int gran, Element element, int fromThread, int toThread, Info info) {

    }

    public void p_r() {

    }

    public void refresh() { this.first = true; }

    public boolean isOverFlow() {
        if (this.childCheck >= this.maxElementNumber)
            return true;
        return false;
    }


}

class FileThread implements Runnable {
    private int threadID;
    private Info info;
    private Element element;
    private boolean write;

    public FileThread(int threadID, Info info, Element element, boolean write)  {
        super();
        this.threadID = threadID;
        this.info = info;
        this.element = element;
        this.write = write;
    }

    @Override
    public void run() {
        if (!this.write) {
            element.fromFile(threadID, info);
        } else
            element.toFile(threadID, info);
    }


}

