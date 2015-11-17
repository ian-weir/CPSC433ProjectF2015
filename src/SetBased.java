import javafx.util.Pair;

import java.util.*;

public class SetBased {
    private List<Fact> facts;
    private OrTree orTree;
    private int populationMax = 20;
    private int maxGeneration = 20;
    private double cullSize = 0.66;

    SetBased() {
        facts = new ArrayList<>();
        orTree = new OrTree();
    }

    private Fact runSearch(FileParser fileParser) {
        Fact bestFact = new Fact();
        List<Slot> newSchedule;
        int currentGeneration = 1;
        int currentPopulation;

        for(currentPopulation = 0; currentPopulation < (populationMax * cullSize) ; currentPopulation++){
            newSchedule = orTree.initialize();
            facts.add(new Fact(newSchedule, fWert(newSchedule)));
        }

        while(currentGeneration < maxGeneration){
            if(currentPopulation == populationMax){
                cull();
                currentGeneration++;
            } else {
                newSchedule = orTree.crossover(fSelect(), fSelect());
            }
        }

        //If size of facts is larger than X cull
        //Otherwise select 2 facts to use as parents
        //


        return bestFact;
    }

    private int fWert(List<Slot> slot){
        int value = 0;


        return value;
    }
    private List<Slot> fSelect(){
        int totalValue = findAllWeights();
        int randomInt;
        int lowerBound, upperBound;
        lowerBound = 0;
        List<Slot> selectedSchedule = new ArrayList<>();

        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(totalValue);

        for(Fact fact: facts){
            upperBound = lowerBound + fact.getSelectionChance();
            if(randomInt > lowerBound && randomInt <= upperBound){
                selectedSchedule = fact.getSchedule();
                break;
            }
            lowerBound = upperBound;
        }
        return selectedSchedule; //change this once method is fully implemented
    }

    private void cull(){
        while(facts.size() >= populationMax * cullSize){
            //get lowest score
            //remove lowest scoring fact
        }
    }

    private int findAllWeights(){
        int worstSchedule = 0;
        int totalWeights = 0;

        for(Fact fact: facts){
            worstSchedule = (fact.getValue() > worstSchedule ? fact.getValue() : worstSchedule); //if fact has a higher value assign it to worstSchedule otherwise keep worstSchedule as is
        }
        for(Fact fact: facts){
            fact.setSelectionChance(worstSchedule - fact.getValue());
            totalWeights+=fact.getSelectionChance();
        }
        return totalWeights;
    }


}
