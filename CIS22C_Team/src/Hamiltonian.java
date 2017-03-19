import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * City object to store specified information for later use with the undo-stack.
 * 
 * @author Michael Kang
 *
 */

class City<E> implements Comparable<City<E>>
{
	 Vertex<E> source, dest;
	 double cost;

	 City(Vertex<E> src, Vertex<E> dst, Double cst)
	 {
	    source = src;
	    dest = dst;
	    cost = cst;
	 }

	 City(Vertex<E> src, Vertex<E> dst, Integer cst)
	 {
	    this (src, dst, cst.doubleValue());
	 }

	 City()
	 {
	    this(null, null, 1.);
	 }

	 public String toString()
	 { 
		 return "Edge: " + source.getData() + " to " + dest.getData() + ", distance: " + cost;
	 }

	 public int compareTo(City<E> rhs)
	 {
	    return (cost < rhs.cost? -1 : cost > rhs.cost? 1 : 0);
	 }
}

/**
 * 
 * Hamiltonian Circuit algorithm code to solve graph problem.
 * 
 * @author Faisal Albannai
 *
 * @param graph = data inputed via file
 * @param result = list to pass the answer.
 * 
 */

public class Hamiltonian<E> extends Graph<E>
{

    public boolean getHamiltonianCycle(Graph<E> graph, List<Vertex<E>> result)
    {
        Vertex<E> startVertex = graph.vertexSet.values().iterator().next();
        Set<Vertex<E>> visited = new HashSet<Vertex<E>>();
        
        return findHamiltonian(startVertex, startVertex, result, visited, graph.vertexSet.values().size());
    }
    
    private boolean findHamiltonian(Vertex<E> startVertex, Vertex<E> currentVertex, List<Vertex<E>> result, Set<Vertex<E>> visited, int totalVertex)
    {
        visited.add(currentVertex);
        result.add(currentVertex);
        Iterator<Entry<E, Pair<Vertex<E>, Double>>> vertexIter = currentVertex.iterator();
        
        while(vertexIter.hasNext())
        {
        	Vertex<E> child = vertexIter.next().getValue().first;
            if(startVertex.equals(child) && totalVertex == result.size())
            {
                result.add(startVertex);
                return true;
            }
            
            if(!visited.contains(child))
            {
                boolean isHamil = findHamiltonian(startVertex, child, result, visited, totalVertex);
                if(isHamil)
                    return true;
            }
        }
        result.remove(result.size()-1);
        visited.remove(currentVertex);
        return false;
    }
}
