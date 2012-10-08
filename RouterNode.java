import javax.swing.*;        

public class RouterNode {
	private boolean poisonedReverse = false;

	private int myID;
	private GuiTextArea myGUI;
	private RouterSimulator sim;
	private int[] costs = new int[RouterSimulator.NUM_NODES];
	private int[][] distanceTable = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
	private int[] minRoute = new int[RouterSimulator.NUM_NODES];

	//--------------------------------------------------
	public RouterNode(int ID, RouterSimulator sim, int[] costs) {
		myID = ID;
		this.sim = sim;
		myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");

		System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
		
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
			for (int j = 0; j < RouterSimulator.NUM_NODES; j++)
				distanceTable[i][j] = RouterSimulator.INFINITY;
		
		System.arraycopy(costs, 0, distanceTable[myID], 0, RouterSimulator.NUM_NODES);
		
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
		{
			if (costs[i] != RouterSimulator.INFINITY)
				minRoute[i] = i;
			else
				minRoute[i] = RouterSimulator.INFINITY;
		}
		
		sendDistanceVector();
	}

	//--------------------------------------------------
	public void recvUpdate(RouterPacket pkt) {
		
		boolean changed = false;
		
		System.arraycopy(pkt.mincost, 0, this.distanceTable[pkt.sourceid], 0, RouterSimulator.NUM_NODES);
		
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
		{
			if (costs[i] + pkt.mincost[i] < distanceTable[myID][i])
			{
				distanceTable[myID][i] = costs[i] + pkt.mincost[i];
				minRoute[i] = pkt.sourceid;
				changed = true;
			}
		}
		
		if (changed)
			sendDistanceVector();
	}

	private void sendDistanceVector() {
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
		{
			if (i == myID || costs[i] == RouterSimulator.INFINITY)
				continue;
			
			int[] distVector = new int[RouterSimulator.NUM_NODES];
			for (int k = 0; k < RouterSimulator.NUM_NODES; k++)
			{
				if (poisonedReverse && i == minRoute[k])
					distVector[k] = RouterSimulator.INFINITY;
				else
					distVector[k] = distanceTable[myID][k];
			}
			
			RouterPacket pkt = new RouterPacket(myID, i, distanceTable[myID]);
			sendUpdate(pkt);
		}
	}
	
	//--------------------------------------------------
	private void sendUpdate(RouterPacket pkt) {
		sim.toLayer2(pkt);

	}


	//--------------------------------------------------
	public void printDistanceTable() {
		StringBuilder b;
		
		myGUI.println("Current table for " + myID +
				"  at time " + sim.getClocktime());
		
		myGUI.println("Distancetable:");
		b = new StringBuilder(F.format("dst", 7) + " | ");
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
			b.append(F.format(i, 5));
		
		for (int source = 0; source < RouterSimulator.NUM_NODES; source++)
		{
			if (source == myID)
				continue;
			
			b = new StringBuilder("nbr" + F.format(source, 3) + " | ");
			for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
				b.append(F.format(distanceTable[source][i], 5));
			myGUI.println(b.toString());
		}
		
		myGUI.println("\nOur distance vector and routes:");
		
		b = new StringBuilder(F.format("dst", 7) + " | ");
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
			b.append(F.format(i, 5));
		
		myGUI.println(b.toString());
		for (int i = 0; i < b.length(); i++)
			myGUI.print("-");
		myGUI.println();
		
		b = new StringBuilder(F.format("cost", 7) + " | ");
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
			b.append(F.format(distanceTable[myID][i], 5));
		myGUI.println(b.toString());
		
		b = new StringBuilder(F.format("route", 7) + " | ");
		for (int i = 0; i < RouterSimulator.NUM_NODES; i++)
		{
			if (minRoute[i] != RouterSimulator.INFINITY)
				b.append(F.format(minRoute[i], 5));
			else
				b.append(F.format("-", 5));
		}
		myGUI.println(b.toString());
		myGUI.println();
	}

	//--------------------------------------------------
	public void updateLinkCost(int dest, int newcost) {
	}

}
