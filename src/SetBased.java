import java.util.*;

public class SetBased {
    private List<Fact> facts;
    private OrTree orTree;
    private int populationMax = 100;
    private int maxGeneration = 100;
    private double cullSize = 0.50;
    private int weightMinFilled, weightPref, weightPair, weightSecDiff, penCourseMin, penLabMin, penNotPaired, penSection;
    private Eval eval = new Eval();
    private FileParser fileParser;


    SetBased(int weightMinFilled, int weightPref, int weightPair, int weightSecDiff, int penCourseMin, int penLabMin,  int penNotPaired,  int penSection, FileParser fileParser) {
        facts = new ArrayList<>();
        orTree = new OrTree(fileParser);
        this.weightMinFilled = weightMinFilled;
        this.weightPref = weightPref;
        this.weightPair = weightPair;
        this.weightSecDiff = weightSecDiff;
        this.fileParser = fileParser;
        this.penCourseMin = penCourseMin;
        this.penLabMin = penLabMin;
        this.penNotPaired = penNotPaired;
        this.penSection = penSection;
    }

    public Fact runCross(Fact fact1, Fact fact2, FileParser fileParser) {
        List<Slot> newSchedule;
        newSchedule = orTree.runCrossover(fact1.getSchedule(), fact2.getSchedule(), fileParser);
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

        System.out.println("Creating inital facts...");
        for (currentPopulation = 0; currentPopulation < (populationMax * cullSize); currentPopulation++) {
            newSchedule = orTree.initialize();
            if (newSchedule != null) {
                facts.add(new Fact(newSchedule, fWert(newSchedule)));
//                System.out.println(facts.size());
                orTree = new OrTree(fileParser);
            } else {
                currentPopulation--;
            }
        }

        System.out.println("Running the search...");
        bestFact = facts.get(0);
        while (currentGeneration < maxGeneration && bestFact.getValue() != 0) {
            if (currentPopulation == populationMax) {
                cull();
                currentGeneration++;
                currentPopulation = facts.size();
            } else {
                orTree = new OrTree(fileParser);
                Fact newFact = runCross(fSelect(), fSelect(), fileParser);
                if (newFact != null && newFact.getSchedule() != null && newFact.getSchedule().size() == facts.get(0).getSchedule().size()) {
                    facts.add(newFact);
//                    System.out.println(facts.size());
                    currentPopulation++;
                }
                for (Fact fact : facts) {
                    bestFact = (fact.getValue() < bestFact.getValue() ? fact : bestFact);
                }

            }
        }

        for (Fact fact : facts) {
            bestFact = (fact.getValue() < bestFact.getValue() ? fact : bestFact);
        }


        return bestFact;
    }

    private int fWert(List<Slot> schedule) {
        int value = 0;

        if (weightMinFilled > 0) {
            value += weightMinFilled * eval.minFilled(schedule, penCourseMin, penLabMin);
        }
        if (weightPref > 0) {
            value += weightPref * eval.pref(schedule, fileParser.getPreferences(), fileParser.getCourseSlots(), fileParser.getLabSlots());
        }
        if (weightPair > 0) {
            value += weightPair * (eval.pair(schedule, fileParser.getPairs()) * penNotPaired);
        }
        if (weightSecDiff > 0) {
            value += weightSecDiff * (eval.secDiff(schedule) * penSection);
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
        double lowerBound = populationMax * cullSize;
        while (facts.size() >= lowerBound) {
            Fact currentWorst = facts.get(0);
            int worstIndex = 0;
            for (Fact fact : facts) {
                if(fact.getValue() < currentWorst.getValue()){
                    currentWorst = fact;
                    worstIndex = facts.indexOf(fact);
                }
            }
            facts.remove(worstIndex);
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
