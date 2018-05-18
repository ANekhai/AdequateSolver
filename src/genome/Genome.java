package genome;

import java.util.ArrayList;


public class Genome {
    public class Chromosome {
        private ArrayList<String> genes;
        private boolean cyclical;

        //Constructor
        Chromosome(ArrayList<String> genes, boolean cyclical) {
            this.genes = new ArrayList<>(genes);
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

        public ArrayList<String> getGenes() { return (ArrayList<String>)genes.clone(); }
    }

    private String name;
    private ArrayList<Chromosome> chromosomes;

    public Genome(){
        name = "None";
        chromosomes = new ArrayList<>();
    }

    public Genome(String name){
        this.name = name;
        chromosomes = new ArrayList<>();
    }


    public Genome(String name, boolean allCircular, ArrayList<String>... chromosomes){
        this.name = name;
        this.chromosomes = new ArrayList<>();
        for (ArrayList<String> genes : chromosomes){
            addChromosome(genes, allCircular);
        }

    }

    public void addChromosome(ArrayList<String> genes, boolean cyclical) {
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

    public int getGeneNumber() {
        int geneNumber = 0;
        for (Chromosome chromosome : chromosomes) {
            geneNumber += chromosome.getSize();
        }
        return geneNumber;
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
