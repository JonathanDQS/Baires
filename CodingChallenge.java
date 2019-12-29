import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * BairesDev CodingChallenge
 * The following program takes a file called people.in in a predetermined format
 * and outputs a file called people.out with the IDs of the first 100 clients
 * with the greatest probability of acquiring our services.
 * @author Jonathan David Quespaz Sanchez
 */

public class CodingChallenge
{
  //Constants to initiliaze arrays of probabilities and weights
  private static final int NUMOFFIELDS = 5;
  private static final int WEIGHTDIVISOR = (NUMOFFIELDS*(NUMOFFIELDS+1)) >> 1;

  /**
    * For the program to work properly it needs 3 files:
    * gdpCountries.in: contains the countries sorted by GDP,
    * rolesRanking.in: contains job roles ordered based on its "importance",
    * people.in: the file provided to rank the potential clients
    */
  public static void main(String[] args)
  {
    //Weights to give to the different fields
    double[] WEIGHTS = new double[NUMOFFIELDS];

    for(int i = 0; i < NUMOFFIELDS; i++)
    {
      WEIGHTS[i] = (double)(NUMOFFIELDS-i)/WEIGHTDIVISOR;
    }

    try
    {
      /*This section of code will calculate the importance of the role based on
      the order of the file which can be modified and we can add more roles
      for future improvements*/

      //File containing the hierarchy of roles
      File rolesProbs = new File("./rolesRanking.in");

      BufferedReader bufRole = new BufferedReader(new FileReader(rolesProbs));

      //Map to store the importance of the role based on the order of the file
      HashMap<String, Double> hierarchy = new HashMap<String, Double>();

      //Place the roles in the map
      int lines = 0;
      String temp;
      while ((temp = bufRole.readLine()) != null)
      {
        lines++;
        for (String role : temp.split(","))
        {
          hierarchy.put(role, (double)lines);
        }
      }

      //Needed to use the replaceAll method in map
      final int linesR = lines;

      //Place the weight in the map
      hierarchy.replaceAll((id,weight)->(linesR-weight)/linesR);


      /*Calculate the "importance" of the country based on its GDP in the file
      which can be modified and we can add more countries or a different
      parameter which would be better suited for countries acquiring our
      services*/

      //File containing the hierarchy of roles
      File countryW = new File("./gdpCountries.in");

      BufferedReader bufCount = new BufferedReader(new FileReader(countryW));

      //Map to store the importance of the role based on the order of the file
      HashMap<String, Double> gdp = new HashMap<String, Double>();

      //Place the roles in the map
      lines = 0;
      while ((temp = bufCount.readLine()) != null)
      {
        lines++;
        gdp.put(temp, (double)lines);
      }

      //Needed to use the replaceAll method in map
      final int linesC = lines;

      //Place the weight in the map
      gdp.replaceAll((country,imp)->(linesC-imp)/linesC);

      /*Normalize input and guess the probability of the client acquiring our
      services based on different characteristics provided in the file*/
      File people = new File("./people.in");

      BufferedReader bufPe = new BufferedReader(new FileReader(people));

      //Create the list of clients to be evaluated
      ArrayList<Client> list = new ArrayList<Client>();

      //Temp variables
      int passId = 0;
      double[] pass = new double[4];
      String search = "";
      String[] clients;

      //Find max number  of connections and recommendations
      int maxConnection = 0;
      int maxRecom = 0;

      //Read lines of the file provided
      while ((temp = bufPe.readLine()) != null)
      {
        temp = temp.toLowerCase().replaceAll("[\\p{Punct}&&[^\\|]]", "")
                .replaceFirst("\\|[^\\|]*","").replaceFirst("\\|[^\\|]*","");
        clients = temp.split("\\|");

        /*clients[0] = id,
         *clients[1] = role,
         *clients[2] = country,
         *clients[3] = industry,
         *clients[4] = recommendations,
         *clients[5] = connections.
         */

        //Get the id
        passId = Integer.parseInt(clients[0]);

        //If the role field exists use a method to find it in our map
        pass[0] = (clients[1] != "" ? searchRole(clients[1], hierarchy) : 0.0);

        //If number of connections known add to array
        pass[1] = (clients[5] != "" ? Double.parseDouble(clients[5]) : 0.0);

        //If country know provide probability based on its GDP
        pass[2] = (gdp.containsKey(clients[2]) ? gdp.get(clients[2]) : 0.0);

        //If number of recommendations known add it to the array
        pass[3] = (clients[4] != "" ? Double.parseDouble(clients[4]) : 0.0);

        //Get max number of recommendations and connections
        if(maxRecom < (int)pass[3]) maxRecom = (int)pass[3];
        if(maxConnection < (int)pass[1]) maxConnection = (int)pass[1];

        /*Pass array
         *0: roleProb
         *1: connectProb
         *2: countryProb
         *3: recomProb
         *4: industryProb
         */
        //Add clients to our map
        list.add(new Client(passId, pass));
      }

      //Avoid NaN error if # of connections or recommendations not available
      if(maxConnection > 0)
      {
        final int maxC = maxConnection;
        list.forEach((potential)->potential.setConnect(maxC));
      }
      if(maxRecom > 0)
      {
        final int maxR = maxRecom;
        list.forEach((potential)->potential.setRecom(maxR));
      }

      //Compute the probability of all our clients
      list.forEach((potential)->potential.computeTotal(WEIGHTS));

      //Sort the list of potential clients
      list.sort(null);

      // Used for debugging purposes
      // System.out.println(list);

      //Write the output to the file with the name suggested
      BufferedWriter writer = new BufferedWriter(new FileWriter("people.out"));
      //Only the 1st 100 clients
      for (int i = 0; i < 100; i++)
      {
        writer.write(list.get(i).getId());
      }
      writer.close();
    } /*Catch close in case one of the files is not available or there is a
      problem while reading it*/
    catch(IOException e)
    {
      System.err.println(e);
      System.err.println(e.getMessage());
    }
  }


  /**
    * Method to find the "importance" of the role depending on a given file
    * which has been processed previously so we pass the Map produced by it
    *
    * @param role The role to be searched
    * @param rolesMap The map with probabilities to be given to different roles
    *
    * @return The importance of the role based on a previously evaluated file
    */

  private static double searchRole(String role, HashMap<String, Double> rolesMap)
  {
    //Split search string
    String[] roleSplit = role.split(" ");
    for (String search : roleSplit)
    {
      //Search each word of the role in the map
      if(rolesMap.containsKey(search))
      {
        return rolesMap.get(search);
      }
    }
    //If only one word and not found return 0
    if(roleSplit.length < 2)
    {
        return 0.0;
    }
    //Otherwise try using combined words as roles are usually more than one word
    String temp = roleSplit[0];
    for (int i = 1; i < roleSplit.length; i++)
    {
      temp += (" " + roleSplit[i]);
      if(rolesMap.containsKey(temp))
      {
        return rolesMap.get(temp);
      }
    }
    return 0.0;
  }
}
