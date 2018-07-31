package genome;


import java.io.*;
import java.util.ArrayList;


public class Grimm {

    public static class Reader {

        //Parse multiple genomes in a GRIMM file to a list of genomes
        public static ArrayList<Genome> parseGRIMM(BufferedReader in) throws IOException {
            ArrayList<Genome> genomes = new ArrayList<>();
            Genome currGenome = null;


            for (String line; (line = in.readLine()) != null; ) {

                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                } else if (line.startsWith(">")) {
                    if (currGenome != null) {
                        if (currGenome.isEmpty()) throw new IOException("Cannot parse empty genomes");
                        else genomes.add(currGenome);
                    }
                    currGenome = new Genome(line.substring(1));
                } else {
                    String[] labels = line.split(" ");
                    ArrayList<String> genes = new ArrayList<>();
                    for (String label : labels) {
                        if (label.equals("$") || label.equals("@")) {
                            if (!genes.isEmpty()) {
                                currGenome.addChromosome(genes, label.equals("@"));
                                genes = null;
                            } else {
                                // Attempting to add empty chromosome
                                throw new IOException("Cannot parse empty chromosomes");
                            }
                            break;
                        } else {
                            genes.add(label);
                        }
                    }
                    // if (genes.size() > 0) throw new IOException("Chromosomes must end in '@' or '$' ");
                    if (genes != null) {
                        if (genes.isEmpty()) throw new IOException("Cannot parse empty genomes");
                        else throw new IOException("Chromosomes must end in '@' or '$'");
                    }

                }

            }
            if (currGenome != null) {
                if (currGenome.getSize() > 0) genomes.add(currGenome);
                else throw new IOException("Cannot parse empty genomes");

                return genomes;
            } else {
                throw new IOException("No genomes found in file");
            }
        }


        public ArrayList<Genome> readFile(String fileName) throws IOException {

            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                return Reader.parseGRIMM(in);
            }
        }
    }


    //TODO: Add functionality to write genomes to a file or stream in GRIMM format.
//    static class Writer {
//
//    }

//    public static void main(String[] argv){
//        String fileName = "test.txt";
//
//        Reader test = new Reader();
//        ArrayList<Genome> genomes = new ArrayList<>();
//        // genomes = test.readFile(fileName);
//
//    }

}