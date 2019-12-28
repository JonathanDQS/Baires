import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *BairesDev CodingChallenge
 *The following program takes a file called people.in in a predetermined format
 *and outputs a file called people.out with the IDs of the first 100 clients
 *with the greatest probability of acquiring our services.
**/

public class CodingChallenge
{
  //Constants to initiliaze arrays of probabilities and weights
  public static final int NUMOFFIELDS = 5;
  public static final int WEIGHTDIVISOR = (NUMOFFIELDS*(NUMOFFIELDS+1)) >> 1;

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
      int loopStop = 0;
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
      while ((temp = bufRole.readLine()) != null && loopStop++ < 10)
      {
        for (String role : temp.split(","))
        {
          hierarchy.put(role, (double)lines);
        }
        lines++;
      }

      //Needed to use the replaceAll method in map
      final int linesR = lines;

      //Place the weight in the map
      hierarchy.replaceAll((id,weight)->(linesR-weight)/linesR);

      System.out.println(hierarchy);


      /*Calculate the "importance" of the country based on its GDP in the file
      which can be modified and we can add more countries or a different
      parameter which would be better suited for countries*/

      //File containing the hierarchy of roles
      File countryW = new File("./gdpCountries.in");

      BufferedReader bufCount = new BufferedReader(new FileReader(countryW));

      //Map to store the importance of the role based on the order of the file
      HashMap<String, Double> gdp = new HashMap<String, Double>();

      //Place the roles in the map
      loopStop = lines = 0;
      while ((temp = bufCount.readLine()) != null && loopStop++ < 10)
      {
        gdp.put(temp, (double)lines);
        lines++;
      }

      //Needed to use the replaceAll method in map
      final int linesC = lines;

      //Place the weight in the map
      gdp.replaceAll((country,imp)->(linesC-imp)/linesC);

      System.out.println(gdp);

      /*Normalize input and guess the probability of the client acquiring our
      services based on different characteristics provided in the file*/
      File people = new File("./people.in");

      BufferedReader bufPe = new BufferedReader(new FileReader(people));

      int i = 0;
      while ((temp = bufPe.readLine()) != null && i < 10)
      {
        i++;
        temp = temp.toLowerCase().replaceAll("[\\p{Punct}&&[^\\|]]", "").replaceFirst("\\|[^\\|]*","").replaceFirst("\\|[^\\|]*","");
        String[] clients = temp.split("\\|");
        for (String field : clients)
        {
          System.out.print(field + ",");
        }
        System.out.println();
        if (clients.length != 6)
        {
          System.out.println(temp);
          System.out.println("Nope");
          for (String field : clients)
          {
            System.out.print(field + ",");
          }
          System.out.println();
        }
        // System.out.println(clients.length);
      }
      System.out.println("Hello World");
    }
    catch(IOException e)
    {
      System.err.println(e);
      System.err.println(e.getMessage());
    }
  }
}
