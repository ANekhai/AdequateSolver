package Structs;

import java.util.Vector;

public interface Genome {
    Grimm GRIMM = new Grimm();

    void addChromosome(Vector<Integer> genes, boolean cyclical);

    void printGenome();

}
