//Hello I am from the future


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

        fileParser.setupData("test.txt");


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
//        SetBased setBased2 = new SetBased(weightMinFilled, weightPref, weightPair, weightSecDiff, fileParser);
        Fact fact = setBased.runSearch();
//        Fact fact2  = setBased2.runSearch();
//
//        Fact fact3 = setBased.runCross(fact,fact2);
        Output output = new Output();
        output.output(fact.getSchedule(), fact.getValue());
//        Fact fact2 = setBased.runSearch();
//        output.output(fact2.getSchedule(), fact2.getValue());
       // output.output(fact3.getSchedule(), fact3.getValue());
    }
}