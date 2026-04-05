package com.egyptianpyramids;

import java.util.*;
import org.json.simple.*;

public class EgyptianPyramidsApp {

    private static final String lineBreak = "--------------------------------------------------------------------------";

    // I've used two arrays here for O(1) reading of the pharaohs and pyramids.
    // other structures or additional structures can be used
    protected Pharaoh[] pharaohArray;
    protected Pyramid[] pyramidArray;
    protected ArrayList<Integer> requestedPyramids;
    protected Map<String, Pharaoh> pharaohHashMap;

    public static void main(String[] args) {
        // create and start the app
        EgyptianPyramidsApp app = new EgyptianPyramidsApp();
        app.start();
    }

    // main loop for app
    public void start() {
        Scanner scan = new Scanner(System.in);
        boolean userQuit = false;

        // show menu once at startup
        printMenu();

        // loop until user quits
        while (!userQuit) {
            System.out.print("Enter a command or 'm' to show menu: ");
            Character command = menuGetCommand(scan);

            executeCommand(scan, command);

            if (command == 'q') {
                userQuit = true;
            }
        }
    }

    // constructor to initialize the app and read commands
    public EgyptianPyramidsApp() {
        // read egyptian pharaohs
        String pharaohFile = "/pharaoh.json";
        JSONArray pharaohJSONArray = JSONFile.readArray(pharaohFile);

        // create and intialize the pharaoh array
        initializePharaoh(pharaohJSONArray);

        // read pyramids
        String pyramidFile = "/pyramid.json";
        JSONArray pyramidJSONArray = JSONFile.readArray(pyramidFile);

        // create and initialize the pyramid array
        initializePyramid(pyramidJSONArray);

        // initialize the requested pyramids list
        requestedPyramids = new ArrayList<Integer>();
    }

    // parse a single pharaoh JSON object
    private Pharaoh parsePharaoh(JSONObject jsonObject) {
        Integer id = toInteger(jsonObject, "id");
        String name = jsonObject.get("name").toString();
        Integer begin = toInteger(jsonObject, "begin");
        Integer end = toInteger(jsonObject, "end");
        Integer contribution = toInteger(jsonObject, "contribution");
        String hieroglyphic = jsonObject.get("hieroglyphic").toString();
        return new Pharaoh(id, name, begin, end, contribution, hieroglyphic);
    }

    // initialize the pharaoh array
    private void initializePharaoh(JSONArray pharaohJSONArray) {
        // create array and hash map
        pharaohArray = new Pharaoh[pharaohJSONArray.size()];
        pharaohHashMap = new HashMap<String, Pharaoh>();

        // initalize the array
        for (int i = 0; i < pharaohJSONArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) pharaohJSONArray.get(i);
            Pharaoh p = parsePharaoh(jsonObject);
            pharaohArray[i] = p;
            pharaohHashMap.put(p.hieroglyphic, p);
        }
    }

    // parse a single pyramid JSON object
    private Pyramid parsePyramid(JSONObject jsonObject) {
        Integer id = toInteger(jsonObject, "id");
        String name = jsonObject.get("name").toString();
        JSONArray contributorsJSONArray = (JSONArray) jsonObject.get("contributors");
        String[] contributors = new String[contributorsJSONArray.size()];
        for (int j = 0; j < contributorsJSONArray.size(); j++) {
            contributors[j] = contributorsJSONArray.get(j).toString();
        }
        return new Pyramid(id, name, contributors);
    }

    // initialize the pyramid array
    private void initializePyramid(JSONArray pyramidJSONArray) {
        // create array
        pyramidArray = new Pyramid[pyramidJSONArray.size()];

        // initalize the array
        for (int i = 0; i < pyramidJSONArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) pyramidJSONArray.get(i);
            Pyramid p = parsePyramid(jsonObject);
            pyramidArray[i] = p;
        }
    }

    // get a integer from a json object, and parse it
    private Integer toInteger(JSONObject jsonObject, String key) {
        String stringValue = jsonObject.get(key).toString();
        Integer result = Integer.parseInt(stringValue);
        return result;
    }

    // get first character from input
    private static Character menuGetCommand(Scanner scan) {
        Character command = '_';

        String rawInput = scan.nextLine();

        if (rawInput.length() > 0) {
            rawInput = rawInput.toLowerCase();
            command = rawInput.charAt(0);
        }

        return command;
    }

    // print all pharaohs (with pagination)
    private void printAllPharaoh(Scanner scan) {
        int pageSize = 5; // number of pharaohs per page
        for (int i = 0; i < pharaohArray.length; i++) {
            printMenuLine();
            pharaohArray[i].print(); // print pharaoh at index i, O(1) array access
            // check if we should pause and ask user
            if ((i + 1) % pageSize == 0 && i + 1 < pharaohArray.length) {
                System.out.print("Press Enter for more or 'q' to stop: ");
                String input = scan.nextLine().trim().toLowerCase();
                if (input.equals("q")) {
                    break; // exit the loop
                }
            }
        }
        printMenuLine(); // final separator line
    }

    // find a pharaoh by hieroglyphic hash
    private Pharaoh findPharaohByHieroglyphic(String hieroglyphic) {
        return pharaohHashMap.get(hieroglyphic);
    }

    // print all pyramids with contributors
    private void printAllPyramids(Scanner scan) {
        int pageSize = 3; // number of pyramids per page
        for (int i = 0; i < pyramidArray.length; i++) {
            printMenuLine();
            System.out.printf("Pyramid %s (id: %d)\n", pyramidArray[i].name, pyramidArray[i].id);
            System.out.printf("\tContributors:\n");
            for (int j = 0; j < pyramidArray[i].contributors.length; j++) {
                Pharaoh p = findPharaohByHieroglyphic(pyramidArray[i].contributors[j]);
                if (p != null) {
                    System.out.printf("\t\t%s\n", p.name);
                }
            }
            // check if we should pause and ask user
            if ((i + 1) % pageSize == 0 && i + 1 < pyramidArray.length) {
                System.out.print("Press Enter for more or 'q' to stop: ");
                String input = scan.nextLine().trim().toLowerCase();
                if (input.equals("q")) {
                    break; // exit the loop
                }
            }
        }
        printMenuLine(); // final separator line
    }

    // get integer input from user
    private int scanInteger(Scanner scan, String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Return an invalid ID to trigger the error handling logic in the calling methods
        }
    }

    // display a specific pharaoh by id
    private void printPharaoh(Scanner scan) {
        int pharaohId = scanInteger(scan, "Enter a pharaoh id: ");
        if (pharaohId >= 0 && pharaohId < pharaohArray.length) {
            printMenuLine();
            pharaohArray[pharaohId].print();
            printMenuLine();
        } else {
            System.out.println("ERROR: Invalid pharaoh id");
        }
    }

    // display a specific pyramid by id with contributor details
    private void printPyramid(Scanner scan) {
        int pyramidId = scanInteger(scan, "Enter a pyramid id: ");
        if (pyramidId >= 0 && pyramidId < pyramidArray.length) {
            printMenuLine();
            System.out.printf("Pyramid %s (id: %d)\n", pyramidArray[pyramidId].name, pyramidArray[pyramidId].id);
            printPyramidContributors(pyramidArray[pyramidId]);
            trackRequestedPyramid(pyramidId);
        } else {
            System.out.println("ERROR: Invalid pyramid id");
        }
    }

    // print pyramid contributors and total gold
    private void printPyramidContributors(Pyramid pyramid) {
        int totalGold = 0;
        for (int i = 0; i < pyramid.contributors.length; i++) {
            Pharaoh p = findPharaohByHieroglyphic(pyramid.contributors[i]);
            if (p != null) {
                System.out.printf("\t%s: %d gold coins\n", p.name, p.contribution);
                totalGold = totalGold + p.contribution;
            }
        }
        System.out.printf("\tTotal contribution: %d gold coins\n", totalGold);
        printMenuLine();
    }

    // track this pyramid request
    private void trackRequestedPyramid(int pyramidId) {
        if (!requestedPyramids.contains(pyramidId)) {
            requestedPyramids.add(pyramidId);
        }
    }

    private Boolean executeCommand(Scanner scan, Character command) {
        Boolean success = true;

        switch (command) {
            case '1':
                printAllPharaoh(scan);
                break;
            case '2':
                printPharaoh(scan);
                break;
            case '3':
                printAllPyramids(scan);
                break;
            case '4':
                printPyramid(scan);
                break;
            case '5':
                printRequestedPyramids();
                break;
            case 'm':
                printMenu();
                break;
            case 'q':
                System.out.println("Thank you for using Nassef's Egyptian Pyramid App!");
                break;
            default:
                System.out.println("ERROR: Unknown commmand");
                success = false;
        }

        return success;
    }

    // print all previously requested pyramids
    private void printRequestedPyramids() {
        if (requestedPyramids.size() == 0) {
            System.out.println("No pyramids have been requested yet.");
            return;
        }
        for (int i = 0; i < requestedPyramids.size(); i++) {
            int pyramidId = requestedPyramids.get(i);
            printMenuLine();
            System.out.printf("Pyramid %s (id: %d)\n", pyramidArray[pyramidId].name, pyramidArray[pyramidId].id);
            printMenuLine();
        }
    }

    private static void printMenuCommand(Character command, String desc) {
        System.out.printf("%s\t\t%s\n", command, desc);
    }

    private static void printMenuLine() {
        System.out.println(lineBreak);
    }

    // clear the console screen
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // prints the menu
    public static void printMenu() {
        clearScreen();
        printMenuLine();
        System.out.println("Nassef's Egyptian Pyramids App");
        printMenuLine();
        System.out.printf("Command\t\tDescription\n");
        System.out.printf("-------\t\t---------------------------------------\n");
        printMenuCommand('1', "List all the pharoahs");
        printMenuCommand('2', "Displays a specific Egyptian pharaoh");
        printMenuCommand('3', "List all the pyramids");
        printMenuCommand('4', "Displays a specific pyramid");
        printMenuCommand('5', "Displays a list of requested pyramids.");
        printMenuCommand('m', "Show menu");
        printMenuCommand('q', "Quit");
        printMenuLine();
    }
}
