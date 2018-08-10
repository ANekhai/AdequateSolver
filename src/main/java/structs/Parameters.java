package structs;

import org.apache.commons.cli.*;

public class Parameters {


    private String rootFolder = "tmp";
    private String inFile;
    private String outFile = "out.gen"; //TODO: use this back to places where I write to files
    private int threadNumber = 1;
    private int breakNumber = -1;
    private int threshold = 2000; //from params file they provided

    public Parameters() { } //TODO: get rid of this

    public Parameters(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Input file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Output file");
        output.setRequired(false);
        options.addOption(output);

        Option threadCount = new Option("t", "threads", true, "Number of threads used");
        output.setRequired(false);
        options.addOption(threadCount);

        Option breakNumber =
                new Option("b", "break number", true, "Maximum iteration number");
        breakNumber.setRequired(false);
        options.addOption(breakNumber);

        Option threshold = new Option("t", "threshold", true, "Set threshold for ...");
        threshold.setRequired(false);
        options.addOption(threshold);

        Option help = new Option("h", "help", true, "Print help information");
        help.setRequired(false);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args );
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
        }

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

    public int getThreshold() {
        return threshold;
    }
}
