package scheduler;

import detector.Detector;
import graphs.BPGraph;
import solver.ExactThread;
import structs.Info;
import structs.Parameters;
import structs.SearchList;

import java.io.File;

public class LoadBalancer {
    public static boolean balance(Info info, int maxUpper, int toThread) {
        boolean result = false;
        // add lock

        int fMax = 0;
        int fromThread = 0;
        for (int i = 0; i < info.getThreadNumber(); ++i) {
            if (info.getFileCheck(i, maxUpper) > fMax) {
                fMax = info.getFileCheck(i, maxUpper);
                fromThread = i;
            }
        }

        if (fMax >= 1) {
            System.out.println("Balancing files from thread " + fromThread + " to " + toThread);
            int distributeAmount = (fMax / 2) >= 1 ? (fMax / 2) : 1;
            for (int i = 0; i < distributeAmount; ++i) {
                //TODO: Modify to run across platforms
                String fromName = info.getRootDirectory() + "/" + fromThread + "_" + maxUpper + "_" +
                        info.getFileCheck(fromThread, maxUpper);
                File from = new File(fromName);

                String toName = info.getRootDirectory() + "/" + toThread + "_" + maxUpper + "_" +
                        (info.getFileCheck(toThread, maxUpper) + 1);
                File to = new File(toName);

                if (!from.renameTo(to))
                    System.out.println("Not successful!");
                else
                    System.out.println("Balancing files from thread " + fromThread + " to " + toThread + " of file " +
                            (info.getFileCheck(toThread, maxUpper) + 1));

                info.decrementFileCheck(fromThread, maxUpper);
                info.incrementFileCheck(toThread, maxUpper);

                //TODO: Implement these
//                info.total[from_thread] -= info.max_elem_sz;
//                info.total[to_thread] += info.max_elem_sz;
//
//                info.th_total[from_thread][max_ub] -= info.max_elem_sz;
//                info.th_total[to_thread][max_ub] += info.max_elem_sz;

            }
            result = true;
        }

        // release lock
        info.unlock(fromThread);
        return result;
    }

    public static boolean balanceStack(BPGraph graph, Parameters params, Info info, Detector detector, SearchList list,
                                       int maxUpper, int fromThread) {

//        int gran = list.list[maxUpper].c_check / 2; //TODO: add this
        if (gran < 200)
            return false;
        // distribute stacks
        int toThread = -1;
        for (int i = 0; i < info.getThreadNumber(); ++i) {
            if (info.getThreadMaxUpper(i) == info.getThreadMaxLower(i) && !info.isLocked(i)) {
                toThread = i;
                info.lock(i);
                break;
            }
        }
        if (toThread == -1)
            return false;

        System.out.println("Balancing stacks from thread " + fromThread + " to thread " + toThread);
        info.setThreadMaxLower(toThread, info.getThreadMaxLower(fromThread));
        info.setThreadMaxUpper(toThread, info.getThreadMaxUpper(fromThread));
        SearchList tlist = new SearchList();
        tlist.init(info, toThread);
//        tlist.list[info.max_up[to_thread]].fork(gran, //TODO: Add this
//                list.list[info.max_up[from_thread]], from_thread, to_thread,
//                info);

//        BPGraph tg = new BPGraph(graph); //Todo: need to implement a graph copying function
        Detector tdetector = new Detector();

        (new Thread(new ExactThread(tg, params, info, tdetector, tlist, toThread))).start();
        info.unlock(toThread);
        return true;
    }

    public static void forkThreads(BPGraph graph, Parameters params, Info info, Detector detector, SearchList list) {
        int gran = info.getThreadTotal(0, info.getMaxUpper()) / params.getThreadNumber();
        for (int i = 1; i < params.getThreadNumber(); ++i) {
            SearchList tlist = new SearchList();
            tlist.init(info, i);
//            tlist.list[info.max_up[0]].fork(gran, list.list[info.max_up[0]], 0, \\TODO: Add this
//                    i, info);
//            BPGraph tg = new BPGraph(graph);
            Detector tdetector = new Detector();

            info.setThreadMaxLower(i, info.getMaxLower());
            info.setThreadMaxUpper(i, info.getMaxUpper());
            (new Thread(new ExactThread(tg, params, info, tdetector, tlist, i))).start();

        }
        info.setParallel();


    }

}
