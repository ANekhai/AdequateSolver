package Structs;


import java.io.*;
import java.util.Vector;


public class Grimm {

    static class Reader {

        //Parse multiple genomes in a GRIMM file to a vector of genomes
        static Vector<Genome> parseGRIMM(BufferedReader in) throws IOException {
            Vector<Genome> genomes = new Vector<>();
            Genome currGenome = null;


            try {
                for (String line; (line = in.readLine()) != null; ) {

                    if (line.length() == 0 || line.startsWith("#")) {
                        continue;
                    } else if (line.startsWith(">")) {
                        if (currGenome != null) {
                            genomes.add(currGenome);
                        }
                        currGenome = new GenomeImpl(line.substring(1));
                    } else {
                        String[] labels = line.split(" ");
                        Vector<String> genes = new Vector<>();
                        for (String label : labels) {
                            if (label.equals("$") || label.equals("@")) {
                                if (!genes.isEmpty()) {
                                    currGenome.addChromosome(genes, label.equals("@"));
                                }
                                break;
                            } else {
                                genes.add(label);
                            }
                        }
                    }

                }

            } catch (IOException e){
                System.err.println("Error reading file");
                throw e;
            }

            return genomes;
        }


        public Vector<Genome> readFile(String fileName) throws IOException {

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
//        Vector<Genome> genomes = new Vector<>();
//        // genomes = test.readFile(fileName);
//
//    }

}