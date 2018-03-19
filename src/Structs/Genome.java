package Structs;

import java.util.Vector;

public interface Genome {

    void addChromosome(Vector<String> genes, boolean cyclical);

    boolean isEmpty();

    String getName();

    Integer getSize();

    GenomeImpl.Chromosome getChromosome(int index);

    String toString();

}
