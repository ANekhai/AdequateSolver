package Structs;


import java.io.*;
import java.util.Vector;
import static java.lang.System.exit;


public class Grimm {
    static String endLine = System.getProperty("line.separator");

    static class Reader {

        //TODO: function cannot handle inline comments. Should fix this.
        Vector<Genome> parseGRIMM(String fileName){
            Vector<Genome> genomes = new Vector();
            Genome currGenome = null;

            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                for(String line; (line = in.readLine()) != null; ) {
                    if (line.length() == 0 || line.startsWith("#")) {
                        continue;
                    }else if (line.startsWith(">")) {
                        if (currGenome != null){
                            genomes.add(currGenome);
                        }
                        currGenome = new GenomeImpl(line.substring(1));
                    }else {
                        String[] labels = line.split(" ");
                        Vector<Integer> genes = new Vector<>();
                        boolean circular = true;
                        for (int i = 0; i < labels.length; ++i) {
                            if (labels[i].equals("$")) {
                                circular = false;
                            }else if ( labels[i].equals("@")) {
                                circular = true;
                            }else {
                                genes.add(Integer.valueOf(labels[i]));
                            }
                        }
                        currGenome.addChromosome(genes, circular);
                    }

                }
                genomes.add(currGenome);

                in.close();

            }catch (FileNotFoundException e){
                System.out.println("File Not Found");
                exit(1);
            } catch (IOException e) {
                System.out.println("Error parsing file");
                e.printStackTrace();
            }

            return genomes;
        }

    }

    //TODO: Add functionality to write genomes to a file or stream in GRIMM format.
//    static class Writer {
//
//    }

    public static void main(String[] argv){
        String fileName = "test.txt";

        Reader test = new Reader();
        Vector<Genome> genomes = new Vector<>();
        genomes = test.parseGRIMM(fileName);

    }

}