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
        private Vector<String> genes;
        private boolean cyclical;

        //Constructor
        private Chromosome(Vector<String> genes, boolean cyclical) {
            this.genes = new Vector<>(genes);
            this.cyclical = cyclical;
        }

        public String toString(){
            String chromosome = "";
            for (String gene : this.genes){
                chromosome = chromosome.concat(gene + " ");
            }

            if (this.cyclical) {
                chromosome = chromosome.concat("@");
            } else {
                chromosome = chromosome.concat("$");
            }

            return chromosome;
        }

        public boolean isCyclical(){
            return this.cyclical;
        }

        public int getSize(){
            return this.genes.size();
        }
    }


    //TODO: THINK ABOUT HOW TO ENUMERATE DUPLICATED GENES FOR NONCONTRACTED BPGRAPH
    private String name;
    private Vector<Chromosome> chromosomes;


    GenomeImpl(String name){
        this.name = name;
        this.chromosomes = new Vector<>();
    }

    public void addChromosome(Vector<String> genes, boolean cyclical) {
        Chromosome newChromosome = new Chromosome(genes, cyclical);
        this.chromosomes.add(newChromosome);
    }

    public String getName(){
        return this.name;
    }

    public Integer getSize(){
        return this.chromosomes.size();
    }

    public Chromosome getChromosome(int index){
        if (index < this.chromosomes.size()) {
            return this.chromosomes.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    public boolean isEmpty(){
        return this.chromosomes.size() == 0;
    }

    public String toString(){
        String endLine = System.getProperty("line.separator");
        String genome = this.name + endLine;
        for (Chromosome chromosome : this.chromosomes) {
            genome = genome.concat(chromosome.toString() + endLine);
        }

        return genome;
    }

    public static void main(String[] args){
        Genome genome = new GenomeImpl("Test");
        Vector<String> genes = new Vector<>();
        genes.add("1"); genes.add("3"); genes.add("5");

        genome.addChromosome(genes, true);
        genes.add("7");
        genome.addChromosome(genes, false);

        System.out.println(genome.toString());
    }

}
