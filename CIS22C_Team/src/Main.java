import java.util.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * 
 * Hamiltonian Circuit:
 * 
 * Using the Held-Karp algorithm, this program will attempt to solve the
 * Traveling Salesperson Problem which requires the salesperson to
 * visit every city on the graph only once and in a complete cycle.
 * 
 * This program will allow for test file inputs, displaying breadth-first, depth-first
 * and adjacency list for the graph, adding and removing cities and paths, and
 * undoing previous removals.
 * 
 * @author Faisal Albannai, Michael Kang, Ao Yu Hsiao
 * 
 * Faisal: Hamiltonian code.
 * Michael: UI/Menu code.
 * Ao Yu: Data input files.
 *
 */

public class Main
{
	// Switch variable for System.out to either go to file or console.
	public static PrintStream ps_console = System.out;
	
	// Standard scanner variable.
	public static Scanner userScanner = new Scanner(System.in);
	
	// Graph variable to store city/path details. LinkedStack variable to store Edge removals for undo.
	public static Graph<String> cities = new Graph<>();
	public static LinkedStack<City<String>> undo = new LinkedStack<>();

	public static void main(String[] args)
	{
		mainMenu();
	}
	
	// Main menu method.
	public static void mainMenu()
	{
		int choice;
		boolean exit = false;

		while(!exit)
		{
			try
			{
				System.out.println("{ Hamiltonian Circuit }");
				System.out.println("{ Main Menu }" + '\n');
				System.out.println("|---------------------|\n");
				System.out.println("Please select an option below:" + '\n');
				System.out.println("	1. [Load Graph File]");
				System.out.println("	2. [Display Graph]");
				System.out.println("	3. [Solve Problem]");
				System.out.println("	4. [Add Path]");
				System.out.println("	5. [Remove Path]");
				System.out.println("	6. [Undo Previous Removal]");
				System.out.print("\nPlease enter choice now: ");
				choice = userScanner.nextInt();
				userScanner.nextLine();

				System.out.println("\n|---------------------|\n");
				if(choice > 0 && choice < 7)
				{
					switch(choice)
					{
					case 1: // Load graph file.
						loadGraph();
						break;

					case 2: // Display loaded graph.
						displayGraph();
						break;

					case 3: // Solve Hamiltonian Circuit.
						solveProblem();
						break;

					case 4: // Add new path.
						addEdge();
						break;

					case 5: // Remove path.
						removeEdge();
						break;

					case 6: // Undo previous removed path.
						undoRemove();
						break;
					}
				}
				else
				{
					System.out.println("\nInvalid option entered, please try again.\n");
					continue;
				}
			}

			catch(InputMismatchException im)
			{
				System.out.println("User input error. Returning to main menu.\n" + im);
				System.out.println("\n|---------------------|\n");
				userScanner.nextLine();
				continue;
			}
		}
	}
	
	// Function to take user input and open an input file, check if found
	// store information into appropriate variables, close file and confirm.
	public static void loadGraph()
	{
		String mainCity, tarCity;
		int conn, dist;
		cities.clear();
		Scanner check = openInputFile();
		
		// Check if file is found.
		if(check != null)
		{
			try
			{
				while(check.hasNext())
				{
					conn = check.nextInt();
					check.nextLine();
					mainCity = check.nextLine();
					if(conn == 1)
					{
						tarCity = check.nextLine();
						dist = check.nextInt();
						check.nextLine();
						cities.addEdge(mainCity, tarCity, dist);
					}
					else
					{
						for(int i = 0; i < conn; i++)
						{
							tarCity = check.nextLine();
							dist = check.nextInt();
							check.nextLine();
							cities.addEdge(mainCity, tarCity, dist);
						}
					}
				}
			}
			
			catch(NoSuchElementException ns)
			{
				System.out.println("File format incorrect. Returning to main menu.\n" + ns);
				System.out.println("\n|---------------------|\n");
				return;
			}
			check.close();
			System.out.println("\nSuccessfully added all cities from file.\n");
			System.out.println("\n|---------------------|\n");
		}
		
		// If check is null, display error message and exit program.
		else 
		{
			System.out.println("File not found.\n");
			return;
		}
	}
	
	// Function to display loaded graph with several options and the option to
	// output the adjacency list to a file specified by user.
	public static void displayGraph()
	{
		int choice;
		String term;
		boolean exit = false;
		Visitor<String> salesperson = new Salesperson();
		
		// Check if there is any data in cities.
		if(cities.vertexSet.size() == 0)
		{
			System.out.println("No city data loaded.");
			System.out.println("\n|---------------------|\n");
			return;
		}
		
		while(!exit)
		{
			System.out.println("{ Hamiltonian Circuit }");
			System.out.println("{ Display Graph }" + '\n');
			System.out.println("|---------------------|" + '\n');
			System.out.println("Please select an option below:" + '\n');
			System.out.println("	1. [Breadth First Traversal]");
			System.out.println("	2. [Depth First Traversal]");
			System.out.println("	3. [Adjacency List]");
			System.out.println("	4. [Output to File]");
			System.out.println("	5. [Return to Menu]");
			System.out.print("\nPlease enter choice now: ");
			choice = userScanner.nextInt();
			userScanner.nextLine();
			
			if(choice > 0 && choice < 6)
			{
				switch(choice)
				{
				case 1: // Breadth First Traversal.
					System.out.print("\nPlease enter starting position: ");
					term = userScanner.nextLine();
					cities.breadthFirstTraversal(cities.vertexSet.get(term).getData(), salesperson);
					System.out.println('\n');
					break;
					
				case 2: // Depth First Traversal.
					System.out.print("\nPlease enter starting position: ");
					term = userScanner.nextLine();
					cities.depthFirstTraversal(cities.vertexSet.get(term).getData(), salesperson);
					System.out.println('\n');
					break;
					
				case 3: // Adjacency List.
					cities.showAdjTable();
					System.out.println('\n');
					break;
					
				case 4: // Output to file.
					try
					{
						System.out.print("\nPlease enter filename you wish to save to: ");
						term = userScanner.nextLine();
						
						System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(term, true)), true));
						System.out.println("Adjacency List for Hamiltonian Circuit Graph:");
						cities.showAdjTable();
						System.setOut(ps_console);
						System.out.println("Successfully saved to file: " + term + ".");
						System.out.println("\n|---------------------|\n");
					}
					catch(IOException e)
					{
						e.printStackTrace();
						return;
					}
					break;
					
				case 5: // Exit to main menu.
					exit = true;
					return;
				}
			}
			else
			{
				System.out.println("\nInvalid option entered, please try again.\n");
				continue;
			}
			
			// Confirmation check to either stay in this function or return to main.
			if(choice != 5)
			{
				System.out.println("\nWould you like to:");
				System.out.println("	1. [Continue]");
				System.out.println("	2. [Exit to Main Menu]");
				System.out.print("\nPlease enter choice now: ");
				choice = userScanner.nextInt();
				userScanner.nextLine();
				
				if(choice == 1)
				{
					System.out.println("\n|---------------------|\n");
					continue;
				}
				else if(choice == 2)
				{
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
				else
				{
					System.out.println("Invalid option selected. Returning to main menu.");
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
			}
		}
	}
	
	// Solve Hamiltonian Circuit problem.
	public static void solveProblem()
	{
		int choice;
		String term;
		boolean solved = false;
		ArrayList<Vertex<String>> temp = new ArrayList<>();
		Hamiltonian<String> hamil = new Hamiltonian<>();
		
		System.out.println("{ Hamiltonian Circuit }");
		System.out.println("{ Solve Problem }" + '\n');
		System.out.println("|---------------------|" + '\n');
		System.out.println("Attempting to find the Hamiltonian Circuit:\n");
		
		// Check if there is any data in cities.
		if(cities.vertexSet.size() == 0)
		{
			System.out.println("No city data loaded.");
			System.out.println("\n|---------------------|\n");
			return;
		}

		// If solved, return true.
		if(hamil.solveHamil(cities, temp))
		{
			System.out.println("Solved!\n");
			solved = true;
		}
		else
			System.out.println("Can't be solved.\n");
		
		// If solved = true, print out results. Else skip.
		if(solved)
		{
			System.out.println("The Hamiltonian Circuit is:\n");
			for(int i = 0; i < temp.size(); i++)
				System.out.println(temp.get(i).data);
		}
		
		System.out.println("\nWould you like to:");
		System.out.println("	1. [Save to File]");
		System.out.println("	2. [Exit to Main Menu]");
		System.out.print("\nPlease enter choice now: ");
		choice = userScanner.nextInt();
		userScanner.nextLine();
		
		if(choice == 1)
		{
			System.out.println("\n|---------------------|\n");
			try
			{
				System.out.print("\nPlease enter filename you wish to save to: ");
				term = userScanner.nextLine();
				
				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(term, true)), true));
				
				if(solved)
				{
					System.out.println("\nThe Hamiltonian Circuit is:\n");
					System.out.println("\n------------------------\n");
					for(int i = 0; i < temp.size(); i++)
						System.out.println(temp.get(i).data);
				}
				else
				{
					System.out.println("\nThe Hamiltonian Circuit is:\n");
					System.out.println("\n------------------------\n");
					System.out.println("Unable to complete circuit.\n");
				}
				
				System.setOut(ps_console);
				System.out.println("Successfully saved to file: " + term + ".");
				System.out.println("\n|---------------------|\n");
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
			
		}
		else if(choice == 2)
		{
			System.out.println("\n|---------------------|\n");
			return;
		}
		else
		{
			System.out.println("Invalid option selected. Returning to main menu.");
			System.out.println("\n|---------------------|\n");
			return;
		}
	}
	
	// Function to add a new path to either new cities or existing.
	public static void addEdge()
	{
		String city, conn;
		int choice, total, dist;
		boolean exit = false;
		
		while(!exit)
		{
			System.out.println("{ Hamiltonian Circuit }");
			System.out.println("{ Add Path }" + '\n');
			System.out.println("|---------------------|" + '\n');
			System.out.println("Please select an option below:" + '\n');
			System.out.println("	1. [Add New Path]");
			System.out.println("	2. [Return to Menu]");
			System.out.print("\nPlease enter choice now: ");
			choice = userScanner.nextInt();
			userScanner.nextLine();

			if(choice > 0 && choice < 3)
			{
				switch(choice)
				{
				case 1: // Add edge.
					System.out.print("\nPlease enter city name: ");
					city = userScanner.nextLine();
					System.out.print("\nHow many cities are connected to " + city + "?: ");
					total = userScanner.nextInt();
					userScanner.nextLine();

					// Take user specified total number of connections and iterate through.
					for(int i = 0; i < total; i++)
					{
						System.out.println("\n[For City #" + (i + 1) + "]");
						System.out.print("Please enter connecting city name: ");
						conn = userScanner.nextLine();
						
						System.out.print("Please enter distance between: ");
						dist = userScanner.nextInt();
						userScanner.nextLine();
						
						cities.addEdge(city, conn, dist);
						System.out.println("\nSuccessfully added: " + city + " to the graph.");
					}
					break;

				case 2: // Exit to main menu.
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
			}
			else
			{
				System.out.println("\nInvalid option entered, please try again.\n");
				continue;
			}
			
			// Confirmation check to either stay in this function or return to main.
			if(choice != 2)
			{
				System.out.println("\nWould you like to:");
				System.out.println("	1. [Continue]");
				System.out.println("	2. [Exit to Main Menu]");
				System.out.print("\nPlease enter choice now: ");
				choice = userScanner.nextInt();
				userScanner.nextLine();
				
				if(choice == 1)
				{
					System.out.println("\n|---------------------|\n");
					continue;
				}
				else if(choice == 2)
				{
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
				else
				{
					System.out.println("Invalid option selected. Returning to main menu.");
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
			}
		}
	}
	
	// Function to remove a user specified edge and store removed edge for possible
	// undo command.
	public static void removeEdge()
	{
		String source, dest;
		int choice;
		boolean exit = false;
		
		// Check if there is any data in cities.
		if(cities.vertexSet.size() == 0)
		{
			System.out.println("No city data loaded.");
			System.out.println("\n|---------------------|\n");
			return;
		}
		
		while(!exit)
		{
			System.out.println("{ Hamiltonian Circuit }");
			System.out.println("{ Remove Path }" + '\n');
			System.out.println("|---------------------|" + '\n');
			System.out.println("Please select an option below:" + '\n');
			System.out.println("	1. [Remove Path]");
			System.out.println("	2. [Return to Menu]");
			System.out.print("\nPlease enter choice now: ");
			choice = userScanner.nextInt();
			userScanner.nextLine();
			
			if(choice > 0 && choice < 3)
			{
				switch(choice)
				{
				case 1: // Remove edge.
					System.out.print("\nPlease enter source city to remove: ");
					source = userScanner.nextLine();
					System.out.print("\nPlease enter destination city to remove: ");
					dest = userScanner.nextLine();
					
					// Store removed edge in temporary Edge object and push into undo stack.
					try
					{
						City<String> temp = new City<String>(cities.vertexSet.get(source), cities.vertexSet.get(source).adjList.get(dest).first, cities.vertexSet.get(source).adjList.get(dest).second);
						undo.push(temp);
						
						// Check if successfully removed.
						if(cities.remove(source, dest))
						{
							// Check if either source/dest city has any remaining connections,
							// if they don't, delete the city to ensure Hamiltonian correctness.
							if(cities.vertexSet.get(source).adjList.isEmpty())
								cities.vertexSet.remove(source);
							if(cities.vertexSet.get(dest).adjList.isEmpty())
								cities.vertexSet.remove(dest);
							
							System.out.println("Successfully removed path between " + source + " and " + dest + ".");
							break;
						}
						System.out.println("Unable to find path.\n");
					}
					
					catch(NullPointerException np)
					{
						System.out.println("Unable to find city or path.\n" + np);
						System.out.println("\n|---------------------|\n");
						continue;
					}
					break;
					
				case 2: // Exit to main menu.
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
			}
			else
			{
				System.out.println("\nInvalid option entered, please try again.\n");
				continue;
			}
			
			// Confirmation check to either stay in this function or return to main.
			if(choice != 2)
			{
				System.out.println("\nWould you like to:");
				System.out.println("	1. [Continue]");
				System.out.println("	2. [Exit to Main Menu]");
				System.out.print("\nPlease enter choice now: ");
				choice = userScanner.nextInt();
				userScanner.nextLine();
				
				if(choice == 1)
				{
					System.out.println("\n|---------------------|\n");
					continue;
				}
				else if(choice == 2)
				{
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
				else
				{
					System.out.println("Invalid option selected. Returning to main menu.");
					System.out.println("\n|---------------------|\n");
					exit = true;
					return;
				}
			}
		}
	}
	
	public static void undoRemove()
	{
		City<String> recover;
		int choice;
		
		// Check if there is any data in undo stack.
		if(undo.size() == 0)
		{
			System.out.println("No removals were done.");
			System.out.println("\n|---------------------|\n");
			return;
		}
		
		System.out.println("Would you like to undo previous removal?: ");
		System.out.println("	1. [Yes]");
		System.out.println("	2. [No]");
		System.out.print("\nPlease enter choice now: ");
		choice = userScanner.nextInt();
		userScanner.nextLine();
		
		if(choice == 1)
		{
			recover = undo.pop();
			cities.addEdge(recover.source.getData(), recover.dest.getData(), recover.cost);
			System.out.println("Undo successful: " + recover.toString());
			System.out.println("\n|---------------------|\n");
		}
		else if(choice == 2)
		{
			System.out.println("\n|---------------------|\n");
			return;
		}
		else
		{
			System.out.println("Invalid option selected. Returning to main menu.");
			System.out.println("\n|---------------------|\n");
		}
		return;
	}

	public static Scanner openInputFile()
	{
		String filename;
		Scanner scanner = null;

		System.out.print("Enter the input filename: ");
		filename = userScanner.nextLine();
		File file = new File(filename);

		try
		{
			scanner = new Scanner(file);
		}
		
		catch(FileNotFoundException fe)
		{
			System.out.println("Can't open input file.\n");
			return null;
		}
		return scanner;
	}
}
