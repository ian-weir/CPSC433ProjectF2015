//Hello I am from the future

import java.util.*;

public class Main {
    public static void main(String[] args) { //Order is  weightMinFilled, weightPref, weightPair, weightSecDiff
        FileParser fileParser = new FileParser();
        Eval eval = new Eval();
        int weightMinFilled = 1;
        int weightPref = 1;
        int weightPair = 1;
        int weightSecDiff = 1;

//        String filename;
//        System.out.println("Enter filename to read from");
//        Scanner scan= new Scanner(System.in);
//        filename = scan.nextLine();
//        System.out.println("Reading from file " + filename);
//        fileParser.setupData(filename);

        fileParser.setupData("tester.txt");


        if (args.length == 4) {
            weightMinFilled = Integer.parseInt(args[0]);
            weightPref = Integer.parseInt(args[1]);
            weightPair = Integer.parseInt(args[2]);
            weightSecDiff = Integer.parseInt(args[3]);

        } else if (args.length == 0) {
            weightMinFilled = 1;
            weightPref = 1;
            weightPair = 1;
            weightSecDiff = 1;
        } else {
            System.out.println("Incorrect number of weights! Either have no weights at all will be set to 1 or have each weight included");
        }

        SetBased setBased = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
        Fact fact = setBased.runSearch();

     //   Fact fact3 = setBased.runCross(fact,fact2);
        Output output = new Output();
        output.output(fact.getSchedule(), fact.getValue());
        Fact fact2 = setBased.runSearch();
        output.output(fact2.getSchedule(), fact2.getValue());
       // output.output(fact3.getSchedule(), fact3.getValue());

//        OrTree orTree = new OrTree(fileParser);
//        List<Slot> schedule = orTree.initialize();
        // OrTree test = new OrTree();
        //test.run();
//        SetBased setBased = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
//        setBased.runSearch();
//        SetBased search = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
//        Fact answer = search.runSearch();
        //   OrTree orTree = new OrTree(fileParser);
        //    List<Slot> schedule = orTree.initialize();
        //   List<Slot> test =  new ArrayList<>();
        //    List<Slot> test2 =  new ArrayList<>();
      //  output.output(schedule, 0);
     //   test = orTree.crossover(schedule, test2);
       
        //List<Slot> schedule = answer.getSchedule();
//        if(schedule == null)
//        {
//        	System.out.print("no schedule(is this suppose to be possible for us?"); // TODO schedule comes back as null not sure why atm!
//        }
//        else
//        {
//        	int evalTot = eval.evalTot(schedule, fileParser);
//        	output.output(schedule, evalTot);
//        	output.output(schedule, evalTot);
//        }
    }

}