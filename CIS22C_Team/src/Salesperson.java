

public class Salesperson implements Visitor<String>
{
	@Override
	public void visit(String str)
	{
		System.out.println("Visited: " + str + ".");
	}
}
