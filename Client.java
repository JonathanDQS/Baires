/**
  * Client class
  * This is a helper class, used to store the information of multiple clients in
  * a dynamic way and to calculate probabilities based on that information
  * @author Jonathan David Quespaz Sanchez
  */

public class Client implements Comparable<Client>
{
  //Fields to be taken into account from the input file
  private int id;
  /*0: roleProb
   *1: connectProb
   *2: countryProb
   *3: recomProb
   *4: industryProb
   */
  private double[] probs = new double[5];
  private double total = 0;
  public Client(int cId, double[] passed)
  {
    id = cId;
    for (int i = 0; i < passed.length; i++)
    {
      probs[i] = passed[i];
    }
    //Irrelevant for now but useful to implement further metrics
    probs[4] = 1;
  }

  /**
    * Compute the weighted probability to sort the list of clients with
    * @param weights the relevance of each field given
    */
  public void computeTotal(double[] weights)
  {
    for (int i = 0; i < weights.length; i++)
    {
      total += (weights[i] * probs[i]);
    }
  }

  /**
    * Set the recommendations probability given the maximum # of recommendations
    * @param maxR which the highest number of recommendations among clients
    */
  public void setRecom(int maxR)
  {
    probs[3] /= (double)maxR;
  }

  /**
    * Set the connections probability given the maximum # of connections
    * @param maxC which the highest number of connections from the clients
    */
  public void setConnect(int maxC)
  {
    probs[1] /= (double)maxC;
  }


  /**
    * In order to find the ordering of clients we override the compareTo method
    * @param client to be compared with this client
    * @return If total of this client is less than the one provided = 1
    *         Else -1 or 0 if equals
    */
  @Override
  public int compareTo(Client client)
  {
    if (this.total - client.getTotal() < 0) return 1;
    return (this.total - client.getTotal()) > 0 ? -1 : 0;
  }

  //To keep consistency with the compareTo method.
  @Override
  public boolean equals(Object other)
  {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Client)) return false;
    Client otherMyClass = (Client) other;
    return this.compareTo(otherMyClass) == 0;
  }

  /**
    * Provides the total which must be pre calculated
    * @return the total probability of the client acquiring our services
    */

  public double getTotal()
  {
    return total;
  }

  //Used for debugging purposes
  @Override
  public String toString()
  {
    return id + ", " + total;
  }

  /**
    * Accesor method to return the id of the potential client
    * @return id of the potential client
    */
  public String getId()
  {
    return id + "\n";
  }
}
