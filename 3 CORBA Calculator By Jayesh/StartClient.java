import Calculator.*;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import java.util.*;

public class StartClient {
    private static Calc calcObj;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            calcObj = (Calc) CalcHelper.narrow(ncRef.resolve_str("Calculator"));

            while (true) {
                // asking for input and read it
                System.out.println("------------------------------------------");
                System.out.println("Enter the parameters in this format [operator][sp][operand1][sp][operand2]."
                        + "\nFor example: + 1 2");
                Scanner c = new Scanner(System.in);
                String input = c.nextLine();

                // if the command is exit, request the server to shutdown
                if (input.toLowerCase().equals("exit")) {
                    calcObj.exit();
                    break;
                }

                // test the input
                String[] inputParams = input.split(" ");
                if (inputParams.length != 3) {
                    System.out.println("Client Exception: Wrong number of parameters. Try again...");
                    continue;
                }
                int operatorCode;
                int operand1;
                int operand2;

                // set calculation type
                if (inputParams[0].equals("+")) {
                    operatorCode = 1;
                } else if (inputParams[0].equals("-")) {
                    operatorCode = 2;
                } else if (inputParams[0].equals("*")) {
                    operatorCode = 3;
                } else if (inputParams[0].equals("/")) {
                    operatorCode = 4;
                } else {
                    System.out.println("Client Exception: Un-recognized operation code. Try again...");
                    continue;
                }

                // test input operands are integers
                try {
                    operand1 = Integer.parseInt(inputParams[1]);
                    operand2 = Integer.parseInt(inputParams[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Client Exception: Wrong number format. Try again...");
                    continue;
                }

                // check if it is divided by zero
                if (operatorCode == 4 && operand2 == 0) {
                    System.out.println("Client Exception: Can't be divided by zero. Try again...");
                    continue;
                }

                // do the calculation and return result
                int result = calcObj.calculate(operatorCode, operand1, operand2);
                String resultDisplay = "";
                if (result == Integer.MAX_VALUE) {
                    resultDisplay = "There might be an Integer Overflow. Please try again...";
                } else if (result == Integer.MIN_VALUE) {
                    resultDisplay = "There might be an Integer Underflow. Please try again...";
                } else {
                    resultDisplay = String.valueOf(result);
                }
                System.out.println("The result is: " + resultDisplay);
            }
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
