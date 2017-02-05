import java.io.*;
import java.util.*;

//***********************************
//Himanshu Chaudhary
//
//This class contains main method.
//This class creates instances of Node from words in the dictionary, creates graph and implements A* to search path
//The main method takes 2n+1 number of arguments , first argument being the dictionary and the rest pairs of words
//***********************************
public class pathFinder
{
  Map<String, Node> dictionaryMap = new HashMap<>();

  //***********************************
  //It is a constructor that is called from main method
  // @param String[] args
  //   is the arguments passed by the user
  //   args needs to be (2n+1) arguments
  //Here args is read, validated then words are read from dictionary and the graph is created from them
  //Args is read to obtain pair of words whose path needs to be found
  //path is found using A* algorithm
  //***********************************
  public pathFinder(String[] args)
  {
    validate(args);
    File dictionary = new File(args[0]);
    try
    {
      BufferedReader getInfo = new BufferedReader(new FileReader(dictionary));
      String word = getInfo.readLine();
      while (word != null)
      {
        Node tempNode = new Node(word);
        addChar(tempNode);
        replaceChar(tempNode);
        deleteChar(tempNode);
        //word is added to dictionary only after all its possible neighbors are found and stored
        dictionaryMap.put(word, tempNode);
        word = getInfo.readLine();

      }
      Node wordToSearch1;
      Node wordToSearch2;

      for (int i = 1; i < args.length; i++)
      {
        //checks if both the word in pair is a valid word
        if (!checkWord(args[i]) && !checkWord(args[i + 1]))
        {
          System.out.println("Only Alphabets Allowed to search");
        }

        wordToSearch1 = dictionaryMap.get(args[i]);
        wordToSearch2 = dictionaryMap.get(args[i + 1]);

        if (wordToSearch1 == null || wordToSearch2 == null)
        {
          //displays required error message for words not in dictionary
          if (wordToSearch1 == null && wordToSearch2 != null) System.out.println(args[i] + " not found");
          else if (wordToSearch2 == null && wordToSearch1 != null) System.out.println(args[i + 1] + " not found");
          else System.out.println(args[i] + " & " + args[i + 1] + " not found");
        } else
        {
          // if words are okay and are in dictionary, search takes place
          shortestPath(wordToSearch1, wordToSearch2);
          // the number of search is also store for A* algorithm to work multiple times
          Node.distance++;

        }
        i++;
      }
      // i is incremented again beacuse two words have been read

    // handles exception
    } catch (FileNotFoundException e)
    {
      System.out.println("File Read Error");
      System.exit(0);

    } catch (IOException e)
    {
      System.out.println("I/O exception");
    }

  }

  //***********************************
  //@param String[] args
  //  arguments passed through command line
  //  needs to have (2n+1) arguments
  //This method checks the number of arguments and if its less than 2 or even an error message is displayed
  //then the program is terminated
  //***********************************
  void validate(String[] args)
  {
    // checks required two cases
    if (args.length < 2)
    {
      System.out.println("More than one Argument required");
      System.exit(1);
    }
    if ((args.length % 2) != 1)
    {
      System.out.println("Arguments must be of odd length");
      System.exit(1);
    }
  }

  //***********************************
  //@param String word
  // it is the word read from String[] args passed in command line
  //returns true is all characters are letters and false if not
  //This methos converts the String into char Array and check if each char is a valid letter or not
  //***********************************
  boolean checkWord(String word)
  {
    char[] toCheck = word.toCharArray();

    for (char c : toCheck)
    {
      if (!Character.isLetter(c)) return false; // checks is all characters are letter
    }

    return true;
  }

  //***********************************
  //@param Node wordNode
  // is the node of word read from dictionary
  //This method adds every possible letter in every location ans checks if the word exits in dictionary
  //If it exists then the both nodes are added to its neighbors
  //***********************************

  void addChar(Node wordNode)
  {
    String word = wordNode.Name;

    for (int i = 0; i <= word.length(); i++)
    {
      String beginning = word.substring(0, i);
      String end = word.substring(i, word.length());
      for (char j = 'a'; j <= 'z'; j++)
      {
        //separates the string in two parts and adds a letter in the middle
        String middle = Character.toString(j);
        String last = beginning + middle + end;
        checkDictionary(last, wordNode);
      }

    }

  }

  //***********************************
  //@param Node wordNode
  // is the node of word read from dictionary
  //This method replaces letter at each location with every possible letter and checks if it exits in dictionary
  //If it exists then the both nodes are added to its neighbors
  //***********************************
  void replaceChar(Node node)
  {
    String word = node.Name;
    for (int i = 0; i < word.length(); i++)
    {
      String beginning = word.substring(0, i);
      String end = word.substring(i + 1, word.length());
      //loops with j from characters a-z
      for (char j = 'a'; j <= 'z'; j++)
      {
        //separates the string and replaces one character
        String middle = Character.toString(j);
        String last = beginning + middle + end;
        if ((node.Name.equals(last)))
        {
        } else
        {
          checkDictionary(last, node);
        }
      }

    }

  }

  //***********************************
  //@param Node wordNode
  // is the node of word read from dictionary
  //This method removes one letter at each location and checks if it exits in dictionary
  //If it exists then the both nodes are added to its neighbors
  //***********************************
  void deleteChar(Node wordNode)
  {
    String word = wordNode.Name;
    for (int i = 0; i < word.length(); i++)
    {
      //separates the string in two parts, omiiting one letter in each loop
      String beginning = word.substring(0, i);
      String end = word.substring(i + 1, word.length());
      String last = beginning + end;
      checkDictionary(last, wordNode);
    }

  }

  //***********************************
  //@param String wordToSearch,
  //   is the word to search in dictionary
  //@param Node wordNode
  //  is the node of word read from dictionary
  //This method searches if wordToSearch exits in dictionary
  //If it exists then the words are added to each other's neighbour
  //***********************************

  void checkDictionary(String wordToSearch, Node wordNode)
  {
    if (!(dictionaryMap.containsKey(wordToSearch))) return;
    // if word is in the dictionary, it is added in neighbour of both words
    Node tempNode = dictionaryMap.get(wordToSearch);
    wordNode.possibility.add(tempNode);
    tempNode.possibility.add(wordNode);


  }
  //***********************************
  //@param String Start,
  // is the node of start word,
  //@param Node End
  //  is the node of the final word (end word)
  //This methods finds the shortest path using A*
  //Initially the neighbour of Start is stores in a linkedList and all of those elemnts have a priorty assigned
  // the priority is calculated by addidng the levenshtein distance from neighbour to final word and stepsFromStart (the number of steps takes to reach from Start word to neighbour)
  // while choosing the neigbours, already visited neighbours are omited unless their stepFromStart is less than the current word
  // the loops runs until End word is found or until there are no elemnets in the queue
  // PsudeoCode used from http://www.redblobgames.com/pathfinding/a-star/introduction.html
  //***********************************

  void shortestPath(Node Start, Node End)
  {

    Queue<Node> frontier = new LinkedList<>(); // queue containing all neighbours that needs to be searched

    frontier.add(Start);
    Start.cameFrom = null;
    Start.visited++;
    Node currentNode = Start;
    int newCost;

    while (frontier.size() > 0)
    {
      // gets the word Node with lowest priority and removes it from the list
      currentNode = deQueue(frontier);
      frontier.remove(currentNode);


      if (currentNode.Name.equals(End.Name))
      {
        printPath(currentNode);
        return;
      }


      for (Node element : currentNode.possibility)
      {

        newCost = currentNode.stepsFromStart + 1;
        // if word is not in the path or a shorter path is found
        if (contains(currentNode, element) || newCost < element.stepsFromStart)
        {
          if (element.visited <= Node.distance)
          {
            element.cameFrom = currentNode;
            //if the word not in already in the path
            if (contains(currentNode, element)) element.visited = Node.distance + 1;
            element.setlD(End);
            element.stepsFromStart = newCost;
            int priority = element.lDistance + newCost;
            element.priority = priority;
            frontier.add(element);
          }
        }
      }


    }
    System.out.println("NO POSSIBLE PATH: " + Start.Name + " " + End.Name);

  }

  //***********************************
  //@param Queue<Node> queue
  // is the  queue which contains the the neighbors from start word to the current word in search
  // returns the node with the lowest priority
  //every node has priority assigned to it and this method returns the node with lowest priority
  //every Node element in the queue is visited and the lowest one is stored in a temp Node variable which is returned
  Node deQueue(Queue<Node> queue)
  {
    Node finalNode = queue.element();
    int temp = finalNode.priority;

    for (Node element : queue)
    {
      if (temp > element.priority)
      {
        finalNode = element;
        temp = finalNode.priority;
      }
    }
    return finalNode;
  }

  //***********************************
  //@param Node start,
  // is the node which is linked with its path
  //@param node toFind
  //  is the word Node whcih needs to be checked into start's path
  // Returns false if toFind exists in the path of Start else it returns true
  //This method backtracks the cameFrom value of start to check if toFind exits there
  //A loop runs until the camefrom value is null (which means the start is reached), if toFind is not found before null is reaches, toFind doesnot exits;
  //***********************************

  boolean contains(Node start, Node toFind)
  {
    Node tempNode = start;
    while (tempNode != null)
    {
      if (tempNode.Name.equals(toFind.Name)) return false;
      tempNode = tempNode.cameFrom;
    }
    return true;
  }

  //***********************************
  //@param Node endNode
  // is the node of the final word (end word) of the path
  // Prints the path beginning with Start word to the End word
  //This method backtracks the cameFrom value of endNode and stores it the word in a string which will be printed
  //A loop runs until the camefrom value is null (which means the start is reached), for every cameFrom, its Name (word) is added to a String in back order
  //***********************************
  void printPath(Node endNode)
  {
    String toPrint = endNode.Name;
    Node tempNode = endNode.cameFrom;
    while (tempNode != null)
    {
      String Name = tempNode.Name;
      toPrint = Name + " " + toPrint;
      tempNode = tempNode.cameFrom;
    }
    System.out.println(toPrint);
  }


  // @param String[] args
  //   is the arguments passed by the user
  //   args needs to be (2n+1) arguments
  //this is the main method which calls a constructor
  // this constructor then passes the String[] args to provide path the

  public static void main(String[] args)
  {
    new pathFinder(args);
  }
}