package structs;


import detector.Detector;
import graphs.BPGraph;

import java.io.File;

public class SearchList {
    private Element[] list;
    private String rootFolder;
    private int threadID;

    public void init(Info info, int threadID) {
        this.threadID = threadID;
        this.rootFolder = info.getRootFolder();
        info.setThreadMaxUpper(threadID, info.getMaxUpper());
        info.setThreadMaxLower(threadID, info.getMaxLower());
        this.list = new Element[info.getThreadMaxUpper(threadID) + 1];

        //TODO: figure out why buffered vs not buffered is important, will probably just use buffered
//        if (!info.is_buffered)
//            for (int i = info.max_low[thread_id]; i < info.max_up[thread_id] + 1; i++)
//                list[i] = new ElemNoBuffer(info, i, thread_id);
//        else
//            for (int i = info.max_low[thread_id]; i < info.max_up[thread_id] + 1; i++)
//                list[i] = new ElemWithBuffer(info, i, thread_id);

        for (int i = info.getThreadMaxLower(threadID); i < info.getThreadMaxUpper(threadID) + 1; ++i) {
            list[i] = new Element(info, i, threadID);
        }
    }

    public void add(BPGraph graph, Detector detector, int s, int e, int upperBound, Info info) {
        list[upperBound].addNode(graph, detector, s, e, info, threadID);
    }

    public boolean get(int upperBound, BPGraph graph, Info info) {
        graph.cleanTemporarySubgraphs();
        if (list[upperBound].getNode(graph, info, threadID))
            return true;
        else {
            list[upperBound] = null;
            System.gc(); // call garbage collector
            return false;
        }
    }

    public void refreshAll(int upperBound, Info info) {
        for (int i = info.getThreadMaxLower(threadID); i < upperBound + 1; ++i) {
            list[i].refresh();
        }
    }

    public void clean(int lowerBound, Info info ) {
        for (int k = info.getThreadMaxLower(threadID); k < lowerBound; ++k) {
            //TODO: Figure out what f_check does
            for (int j = 1; j <= info.getFileCheck(this.threadID, k); ++j) {
               // constructing files...
               String name = "Temp/temp" + k + "_" + j;
               File file = new File(name);
//               info.space_usage[this.threadID][Const.DSC_USG] -= (float) file.length() / Const.MB;
                file.delete();
            }
//            info.total[this.threadID] -= info.th_total[this.threadID][k];
            list[k] = null;
            info.setThreadMaxLower(threadID, lowerBound);
            System.gc();
        }
    }

    public void setNull(int upperBound) { this.list[upperBound] = null; }
}
