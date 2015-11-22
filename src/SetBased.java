import javafx.util.Pair;

import java.util.*;

public class SetBased {
    private List<Fact> facts;
    private OrTree orTree;
    private int populationMax = 20;
    private int maxGeneration = 20;
    private double cullSize = 0.66;
    private int weightMinFilled, weightPref, weightPair, weightSecDiff;
    private Eval eval = new Eval();
    private FileParser fileParser;


    SetBased(int weightMinFilled, int weightPref, int weightPair, int weightSecDiff, FileParser fileParser) {
        facts = new ArrayList<>();
        orTree = new OrTree();
        this.weightMinFilled = weightMinFilled;
        this.weightPref = weightPref;
        this.weightPair = weightPair;
        this.weightSecDiff = weightSecDiff;
        this.fileParser = fileParser;
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

        while(currentGeneration < maxGeneration && bestFact.getValue() != 0){
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

    private int fWert(List<Slot> schedule){
        int value = 0;

        if(weightMinFilled > 0){
            value += eval.minFilled(schedule);
        }
        if(weightPref > 0){
            value += eval.pref(schedule, fileParser.getPreferences(), fileParser.getCourseSlots(), fileParser.getCourseSlots());
        }
        if(weightPair > 0){
            value += eval.pair(schedule, fileParser.getPairs());
        }
        if(weightSecDiff > 0){
            value += eval.secDiff(schedule);
        }

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
        while(facts.size() >= populationMax / cullSize){
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
