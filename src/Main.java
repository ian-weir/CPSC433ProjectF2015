//Hello I am from the future

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) { //Order is  weightMinFilled, weightPref, weightPair, weightSecDiff
        FileParser fileParser = new FileParser();
        Eval eval = new Eval();
        int weightMinFilled, weightPref, weightPair, weightSecDiff;

     //   fileParser.setupData("tester.txt");


        if (args.length == 4) {
            weightMinFilled = Integer.parseInt(args[0]);
            weightPref = Integer.parseInt(args[1]);
            weightPair = Integer.parseInt(args[2]);
            weightSecDiff = Integer.parseInt(args[3]);

        } else if(args.length == 0){
            weightMinFilled = 1;
            weightPref = 1;
            weightPair = 1;
            weightSecDiff = 1;
        } else {
            System.out.println("Incorrect number of weights! Either have no weights at all will be set to 1 or have each weight included");
        }

       // OrTree test = new OrTree();
        //test.run();
    }

}