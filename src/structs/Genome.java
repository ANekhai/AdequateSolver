package structs;

import java.util.Vector;


public class Genome {
    public class Chromosome {
        private Vector<String> genes;
        private boolean cyclical;

        //Constructor
        Chromosome(Vector<String> genes, boolean cyclical) {
            this.genes = new Vector<>(genes);
            this.cyclical = cyclical;
        }

        public String toString(){
            String chromosome = "";
            for (String gene : genes){
                chromosome = chromosome.concat(gene + " ");
            }

            if (cyclical) {
                chromosome = chromosome.concat("@");
            } else {
                chromosome = chromosome.concat("$");
            }

            return chromosome;
        }

        public boolean isCyclical(){
            return cyclical;
        }

        public int getSize(){
            return genes.size();
        }

        public Vector<String> getGenes() { return (Vector<String>)genes.clone(); }
    }


    //TODO: THINK ABOUT HOW TO ENUMERATE DUPLICATED GENES FOR NONCONTRACTED BPGRAPH
    private String name;
    private Vector<Chromosome> chromosomes;

    public Genome(){
        name = "None";
        chromosomes = new Vector<>();
    }

    public Genome(String name){
        this.name = name;
        chromosomes = new Vector<>();
    }

    public void addChromosome(Vector<String> genes, boolean cyclical) {
        Chromosome newChromosome = new Chromosome(genes, cyclical);
        chromosomes.add(newChromosome);
    }

    public String getName(){
        return this.name;
    }

    public Integer getSize(){
        return this.chromosomes.size();
    }

    public Chromosome getChromosome(int index){
        if (index < chromosomes.size()) {
            return chromosomes.get(index);
        }
        throw new IndexOutOfBoundsException();
    }

    public boolean isEmpty(){
        return this.chromosomes.size() == 0;
    }

    public String toString(){
        String endLine = System.getProperty("line.separator");
        String genome = name + endLine;
        for (Chromosome chromosome : chromosomes) {
            genome = genome.concat(chromosome.toString() + endLine);
        }

        return genome;
    }


}
