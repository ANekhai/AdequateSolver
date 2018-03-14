/*
* GenomeImpl
*
* Version 1.0
*
* 03/06/2018
*
* No copyright information
 */

package Structs;

import java.util.*; //Vector


class GenomeImpl implements Genome {
    static class Chromosome {
        private Vector<Integer> genes;
        private boolean cyclical;

        //Constructor
        private Chromosome(Vector<Integer> genes, boolean cyclical) {
            this.genes = genes;
            this.cyclical = cyclical;
        }
    }

    //TODO: THINK ABOUT HOW TO STORE DUPLICATED GENES
    private String name;
    private Vector<Chromosome> chromosomes;

    public GenomeImpl() {
        this.name = "";
        this.chromosomes = new Vector<>();
    }

    public GenomeImpl(String name){
        this.name = name;
        this.chromosomes = new Vector<>();
    }

    public void addChromosome(Vector<Integer> genes, boolean cyclical) {
        Chromosome newChromosome = new Chromosome(genes, cyclical);
        this.chromosomes.add(newChromosome);
    }

    public void printGenome() {
        for(int i = 0; i < this.chromosomes.size(); ++i) {
            Chromosome currChromosome = this.chromosomes.get(i);
            for(int j = 0; j < currChromosome.genes.size(); ++j) {
                System.out.print(Integer.toString(currChromosome.genes.get(j)) + " ");
            }
            if (currChromosome.cyclical) {
                System.out.print("@");
            } else {
                System.out.print("$");
            }
            System.out.print(System.getProperty("line.separator"));
        }

    }




}
