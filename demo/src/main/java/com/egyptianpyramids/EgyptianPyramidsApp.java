package com.egyptianpyramids;

import java.util.*;
import org.json.simple.*;

public class EgyptianPyramidsApp {

  private static final String line = "--------------------------------------------------------------------------";


  // I've used two arrays here for O(1) reading of the pharaohs and pyramids.
  // other structures or additional structures can be used
  protected Pharaoh[] pharaohArray;
  protected Pyramid[] pyramidArray;
  protected ArrayList<Integer> requestedPyramids;

  public static void main(String[] args) {
    // create and start the app
    EgyptianPyramidsApp app = new EgyptianPyramidsApp();
    app.start();
  }

  // main loop for app
  public void start() {
    Scanner scan = new Scanner(System.in);
    boolean userQuit = false;

    // loop until user quits
    for (; !userQuit; ) {
      printMenu();
      System.out.print("Enter a command: ");
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
    String pharaohFile =
      "demo/src/main/java/com/egyptianpyramids/pharaoh.json";
    JSONArray pharaohJSONArray = JSONFile.readArray(pharaohFile);

    // create and intialize the pharaoh array
    initializePharaoh(pharaohJSONArray);

    // read pyramids
    String pyramidFile =
      "demo/src/main/java/com/egyptianpyramids/pyramid.json";
    JSONArray pyramidJSONArray = JSONFile.readArray(pyramidFile);

    // create and initialize the pyramid array
    initializePyramid(pyramidJSONArray);

    // initialize the requested pyramids list
    requestedPyramids = new ArrayList<Integer>();
  }

  // initialize the pharaoh array
  private void initializePharaoh(JSONArray pharaohJSONArray) {
    // create array and hash map
    pharaohArray = new Pharaoh[pharaohJSONArray.size()];

    // initalize the array
    for (int i = 0; i < pharaohJSONArray.size(); i++) {
      // get the object
      JSONObject o = (JSONObject) pharaohJSONArray.get(i);

      // parse the json object
      Integer id = toInteger(o, "id");
      String name = o.get("name").toString();
      Integer begin = toInteger(o, "begin");
      Integer end = toInteger(o, "end");
      Integer contribution = toInteger(o, "contribution");
      String hieroglyphic = o.get("hieroglyphic").toString();

      // add a new pharoah to array
      Pharaoh p = new Pharaoh(id, name, begin, end, contribution, hieroglyphic);
      pharaohArray[i] = p;
    }
  }

    // initialize the pyramid array
    private void initializePyramid(JSONArray pyramidJSONArray) {
      // create array and hash map
      pyramidArray = new Pyramid[pyramidJSONArray.size()];
  
      // initalize the array
      for (int i = 0; i < pyramidJSONArray.size(); i++) {
        // get the object
        JSONObject o = (JSONObject) pyramidJSONArray.get(i);
  
        // parse the json object
        Integer id = toInteger(o, "id");
        String name = o.get("name").toString();
        JSONArray contributorsJSONArray = (JSONArray) o.get("contributors");
        String[] contributors = new String[contributorsJSONArray.size()];
        for (int j = 0; j < contributorsJSONArray.size(); j++) {
          String c = contributorsJSONArray.get(j).toString();
          contributors[j] = c;
        }
  
        // add a new pyramid to array
        Pyramid p = new Pyramid(id, name, contributors);
        pyramidArray[i] = p;
      }
    }

  // get a integer from a json object, and parse it
  private Integer toInteger(JSONObject o, String key) {
    String s = o.get(key).toString();
    Integer result = Integer.parseInt(s);
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

  // print all pharaohs
  private void printAllPharaoh() {
    for (int i = 0; i < pharaohArray.length; i++) {
      printMenuLine();
      pharaohArray[i].print();
      printMenuLine();
    }
  }

  // find a pharaoh by hieroglyphic hash
  private Pharaoh findPharaohByHieroglyphic(String hieroglyphic) {
    for (int i = 0; i < pharaohArray.length; i++) {
      if (pharaohArray[i].hieroglyphic.equals(hieroglyphic)) {
        return pharaohArray[i];
      }
    }
    return null;
  }

  // print all pyramids with contributors
  private void printAllPyramids() {
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
      printMenuLine();
    }
  }

  // display a specific pharaoh by id
  private void printPharaoh(Scanner scan) {
    System.out.print("Enter a pharaoh id: ");
    int pharaohId = Integer.parseInt(scan.nextLine());
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
    System.out.print("Enter a pyramid id: ");
    int pyramidId = Integer.parseInt(scan.nextLine());
    if (pyramidId >= 0 && pyramidId < pyramidArray.length) {
      printMenuLine();
      System.out.printf("Pyramid %s (id: %d)\n", pyramidArray[pyramidId].name, pyramidArray[pyramidId].id);
      int totalGold = 0;
      for (int i = 0; i < pyramidArray[pyramidId].contributors.length; i++) {
        Pharaoh p = findPharaohByHieroglyphic(pyramidArray[pyramidId].contributors[i]);
        if (p != null) {
          System.out.printf("\t%s: %d gold coins\n", p.name, p.contribution);
          totalGold = totalGold + p.contribution;
        }
      }
      System.out.printf("\tTotal contribution: %d gold coins\n", totalGold);
      printMenuLine();
      // track this pyramid request
      if (!requestedPyramids.contains(pyramidId)) {
        requestedPyramids.add(pyramidId);
      }
    } else {
      System.out.println("ERROR: Invalid pyramid id");
    }
  }

  private Boolean executeCommand(Scanner scan, Character command) {
    Boolean success = true;

    switch (command) {
      case '1':
        printAllPharaoh();
        break;
      case '2':
        printPharaoh(scan);
        break;
      case '3':
        printAllPyramids();
        break;
      case '4':
        printPyramid(scan);
        break;
      case '5':
        printRequestedPyramids();
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
    System.out.println(line);
  }

  // prints the menu
  public static void printMenu() {
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
    printMenuCommand('q', "Quit");
    printMenuLine();
  }
}
