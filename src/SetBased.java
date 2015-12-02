import java.util.*;

public class SetBased {
    private List<Fact> facts;
    private OrTree orTree;
    private int populationMax = 20;
    private int maxGeneration = 5;
    private double cullSize = 0.66;
    private int weightMinFilled, weightPref, weightPair, weightSecDiff;
    private Eval eval = new Eval();
    private FileParser fileParser;


    SetBased(int weightMinFilled, int weightPref, int weightPair, int weightSecDiff, FileParser fileParser) {
        facts = new ArrayList<>();
        orTree = new OrTree(fileParser);
        this.weightMinFilled = weightMinFilled;
        this.weightPref = weightPref;
        this.weightPair = weightPair;
        this.weightSecDiff = weightSecDiff;
        this.fileParser = fileParser;
    }

    public Fact runCross(Fact fact1, Fact fact2) {
        List<Slot> newSchedule;
        newSchedule = orTree.runCrossover(fact1.getSchedule(), fact2.getSchedule());
        if (newSchedule != null) {
            orTree = new OrTree(fileParser);
            return new Fact(newSchedule, fWert(newSchedule));
        } else {

            return null;
        }
    }


    public Fact runSearch() {
        Fact bestFact = new Fact();
        List<Slot> newSchedule;
        int currentGeneration = 1;
        int currentPopulation;

        for (currentPopulation = 0; currentPopulation < (populationMax * cullSize); currentPopulation++) {
            newSchedule = orTree.initialize();
            if (newSchedule != null) {
                facts.add(new Fact(newSchedule, fWert(newSchedule)));
                orTree = new OrTree(fileParser);
            } else {
                currentPopulation--;
            }
        }
//        facts.add(runCross(facts.get(0), facts.get(1)));
//        bestFact = runCross(facts.get(0), facts.get(1));
        bestFact = facts.get(0);
        while (currentGeneration < maxGeneration && bestFact.getValue() != 0) {
            if (currentPopulation == populationMax) {
                cull();
                currentGeneration++;
            } else {
                orTree = new OrTree(fileParser);
                Fact newFact = runCross(fSelect(), fSelect());
                if (newFact.getSchedule() != null) {
                    facts.add(newFact);
                }
                currentPopulation++;
                for (Fact fact : facts) {
                    bestFact = (fact.getValue() < bestFact.getValue() ? fact : bestFact);
                }

            }
        }

        //If size of facts is larger than X cull
        //Otherwise select 2 facts to use as parents
        //
        for (Fact fact : facts) {
            bestFact = (fact.getValue() < bestFact.getValue() ? fact : bestFact);
        }


        return bestFact;
    }

    private int fWert(List<Slot> schedule) {
        int value = 0;

        if (weightMinFilled > 0) {
            value += weightMinFilled * eval.minFilled(schedule);
        }
        if (weightPref > 0) {
            value += weightPref * eval.pref(schedule, fileParser.getPreferences(), fileParser.getCourseSlots(), fileParser.getLabSlots());
        }
        if (weightPair > 0) {
            value += weightPair * eval.pair(schedule, fileParser.getPairs());
        }
        if (weightSecDiff > 0) {
            value += weightSecDiff * eval.secDiff(schedule);
        }

        return value;
    }

    private Fact fSelect() {
        int totalValue = findAllWeights();
        int randomInt;
        int lowerBound, upperBound;
        lowerBound = 0;
        Fact selectedFact = null;

        if (totalValue == 0) { //this means all the facts are equal
            totalValue = facts.size();
        }

        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(totalValue);
        while(randomInt < 0){
            randomInt = randomGenerator.nextInt(totalValue);
        }
        if (totalValue != facts.size()) {
            for (Fact fact : facts) {
                upperBound = lowerBound + fact.getSelectionChance();
                if (randomInt >= lowerBound && randomInt < upperBound) {
                    selectedFact = fact;
                    break;
                }
                lowerBound = upperBound;
            }
        } else {
            selectedFact = facts.get(randomInt);
        }

        return selectedFact;
    }

    private void cull() {
        while (facts.size() >= populationMax / cullSize) {
            Fact currentWorst = facts.get(0);
            for (Fact fact : facts) {
                currentWorst = (fact.getValue() > currentWorst.getValue() ? fact : currentWorst);
            }
            facts.remove(currentWorst);
        }
    }

    private int findAllWeights() {
        int worstSchedule = 0;
        int totalWeights = 0;
        int selectionChance;


        for (Fact fact : facts) {
            worstSchedule = (fact.getValue() > worstSchedule ? fact.getValue() : worstSchedule); //if fact has a higher value assign it to worstSchedule otherwise keep worstSchedule as is
        }
        for (Fact fact : facts) {
            selectionChance = (worstSchedule == 0 ? fact.getValue() : worstSchedule - fact.getValue());
            fact.setSelectionChance(selectionChance);
            totalWeights += fact.getSelectionChance();
        }
        return totalWeights;
    }


}
