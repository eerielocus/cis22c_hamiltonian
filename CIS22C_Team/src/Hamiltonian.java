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

public class Hamiltonian<E> extends Graph<E>{

	public boolean solveHamil(Graph<E> graph, List<Vertex<E>> cycleList)
	{
		//The starting vertex is irrelevant.
		return backtrackHamil(graph.vertexSet.values().iterator().next(), graph.vertexSet.values().iterator().next(), cycleList,graph.vertexSet.values().size());
	}

	private boolean backtrackHamil(Vertex<E> startV, Vertex<E> currV, List<Vertex<E>> cycleList, int graphSize)
	{
		cycleList.add(currV);
		Iterator<Entry<E, Pair<Vertex<E>, Double>>> iterV = currV.iterator();

		while(iterV.hasNext())
		{ 
			//iterate through all vertices
			Vertex<E> currVEdge = iterV.next().getValue().first; //get the current vertex

			if(startV.equals(currVEdge) && graphSize == cycleList.size())
			{ 
				//completed the cycle
				cycleList.add(startV);
				return true;
			}
			if(!cycleList.contains(currVEdge))
			{ 
				//recursive cycle if vertex hasn't been visited
				boolean isHamil = backtrackHamil(startV, currVEdge, cycleList, graphSize);
				if(isHamil)
					return true;
			}
		}
		//if the cycle cannot be finished, start backtracking and take another path 
		cycleList.remove(cycleList.size()-1);
		return false;
	}
}