//Hello I am from the future

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) { //Order is  weightMinFilled, weightPref, weightPair, weightSecDiff
        FileParser fileParser = new FileParser();
        Eval eval = new Eval();
        int weightMinFilled = 1, weightPref = 1, weightPair = 1, weightSecDiff = 1;

        fileParser.setupData("C:/Users/Johnny/workspace/AI/src/BigTest1");


        if (args.length == 4) {
            weightMinFilled = Integer.parseInt(args[0]);
            weightPref = Integer.parseInt(args[1]);
            weightPair = Integer.parseInt(args[2]);
            weightSecDiff = Integer.parseInt(args[3]);

        } 
        
        else if (args.length == 0) {
            weightMinFilled = 1;
            weightPref = 1;
            weightPair = 1;
            weightSecDiff = 1;
            
        } 
        else 
        {
            System.out.println("Incorrect number of weights! Either have no weights at all will be set to 1 or have each weight included");
        }

        // OrTree test = new OrTree();
        //test.run();
//        SetBased setBased = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
//        setBased.runSearch();
        SetBased search = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
        Fact answer = search.runSearch();
     //   OrTree orTree = new OrTree(fileParser);
    //    List<Slot> schedule = orTree.initialize();
     //   List<Slot> test =  new ArrayList<>();
    //    List<Slot> test2 =  new ArrayList<>();
        Output output = new Output();
      //  output.output(schedule, 0);
     //   test = orTree.crossover(schedule, test2);
       
        List<Slot> schedule = answer.getSchedule();
        if(schedule == null)
        {
        	System.out.print("no schedule(is this suppose to be possible for us?"); // TODO schedule comes back as null not sure why atm!
        }
        else
        {
        	int evalTot = eval.evalTot(schedule, fileParser);
        	output.output(schedule, evalTot);
        	output.output(schedule, evalTot);
        }
    }

}