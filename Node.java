
import java.util.HashSet;
import java.util.Set;

//***********************************
//Himanshu Chaudhary
//
//is the class that is uses to link words with its possible neighbours
// this clsss contains fields which keeps track of the its name, Levenshtein distance, number of steps away from start word, priority for the search algorithm
// there are getters and setter for Levenshtein distance, and function to calculate Levenshtein distance
//***********************************
class Node
{
  static int distance;
  String Name;
  int lDistance; //Levenshtein distance to the End word
  int stepsFromStart; // number of steps from start
  Node cameFrom;  //previous word Node it came from
  int visited; //number of times visited, used if search needs to multiple word pairs in single run time
  int priority; //is the total estimated distance (cameFrom + lDistance) which is used to pick the best word in A* search

  Set<Node> possibility = new HashSet<>(); // contains all possible neighbours

  //***********************************
  //@param String word
  // is the word whose node needs to be created
  //This constructor also sets default value for all other fields
  //Method's Algorithm
  //***********************************

  public Node(String word)
  {
    this.Name = word;


    this.cameFrom = null;
    this.visited = 0;
  }

  //***********************************
  //@param Node end
  // is the end word for in the search
  //This setter function calls LDict function to set ldistance field of the current node
  //***********************************
  void setlD(Node End)
  {

    this.lDistance = LDist(this.Name, End.Name);
  }

  // Levenstine Distance Implemetation from https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
  private static int minimum(int a, int b, int c)
  {
    return Math.min(Math.min(a, b), c);
  }


  public static int LDist(CharSequence lhs, CharSequence rhs)
  {
    int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

    for (int i = 0; i <= lhs.length(); i++)
      distance[i][0] = i;
    for (int j = 1; j <= rhs.length(); j++)
      distance[0][j] = j;

    for (int i = 1; i <= lhs.length(); i++)
      for (int j = 1; j <= rhs.length(); j++)
        distance[i][j] = minimum(
        distance[i - 1][j] + 1,
        distance[i][j - 1] + 1,
        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

    return distance[lhs.length()][rhs.length()];
  }
}