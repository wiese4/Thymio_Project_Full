package main;

import java.util.ArrayList;

import context.Coordinate;
import context.Node;
import context.OpenList;

public class Pathfinder {

	/*
	 * Pathfinder currently works only without Thymio in its own main scope.
	 * It creates a map with a size of HEIGHT * WIDTH,
	 * populates the map with OBSTACLE_COUNT random obstacles
	 * sets the startNode to the bottom left corner,
	 * sets the goalNode to the top right corner,
	 * and through its findPath method finds the shortest path from start to goal using A-Star.
	 * It prefers the path with the least turnings.
	 * TODO Next step would be to integrate the Pathfinder in the Thymio environment.
	 * Hope that's not that big of a deal ;)
	 */
	private final int HEIGHT = 10;
	private final int WIDTH = 10;
	private final int OBSTACLE_COUNT = 10;

	private final int START = HEIGHT * WIDTH - WIDTH;
	private final int GOAL = WIDTH - 1;

	private Node startNode;
	private Node goalNode;

	private Node[] nodes = new Node[HEIGHT * WIDTH];

	private OpenList ol = new OpenList();
	private ArrayList<Node> closedList = new ArrayList<Node>();

	public Pathfinder() {
		populateMap();
		setLinkNodes();
	}

	private int[] getRandomObstacles() {
		int[] randoms = new int[OBSTACLE_COUNT];
		for (int i = 0; i < randoms.length; i++) {
			int r = (int) (Math.random() * WIDTH * HEIGHT);
			for (int j = 0; j < i; j++) {
				if (randoms[j] == r || randoms[j] == START
						|| randoms[j] == GOAL) {
					i--;
					break;
				}
			}
			randoms[i] = r;
		}
		return randoms;
	}

	private void populateMap() {
		int[] obstacles = getRandomObstacles();
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node(i, i / WIDTH, i % WIDTH);
			if (i == START) {
				nodes[i].setContent('T');
				startNode = nodes[i];
			} else if (i == GOAL) {
				nodes[i].setContent('G');
				goalNode = nodes[i];
			} else {
				nodes[i].setContent('_');
				for (int j = 0; j < obstacles.length; j++) {
					if (obstacles[j] == i) {
						nodes[i].setContent('O');
						closedList.add(nodes[i]);
					}
				}
			}
		}
	}

	private void markPath() {
		Node n = goalNode;
		while (n.getPredecessor() != startNode) {
			n.getPredecessor().setContent('x');
			n = n.getPredecessor();
		}
	}

	private void setLinkNodes() {
		Node curNode;
		Coordinate c;
		ArrayList<Node> linkNodes;
		int x, y;
		for (int i = 0; i < nodes.length; i++) {
			linkNodes = new ArrayList<Node>();
			curNode = nodes[i];
			c = curNode.getCoordinate();
			x = c.getX();
			y = c.getY();
			// cut out upper row
			if (x - 1 >= 0) {
				// cut out left col
				// if (y - 1 >= 0) {
				// linkNodes.add(nodes[getNodeID(x - 1, y - 1)]);
				// }
				// // cut out right col
				// if (y + 1 < WIDTH) {
				// linkNodes.add(nodes[getNodeID(x - 1, y + 1)]);
				// }
				// always needed
				linkNodes.add(nodes[getNodeID(x - 1, y)]);
			}
			// cut out lower row
			if (x + 1 < HEIGHT) {
				// // cut out left col
				// if (y - 1 >= 0) {
				// linkNodes.add(nodes[getNodeID(x + 1, y - 1)]);
				// }
				// // cut out right col
				// if (y + 1 < WIDTH) {
				// linkNodes.add(nodes[getNodeID(x + 1, y + 1)]);
				// }
				// always needed
				linkNodes.add(nodes[getNodeID(x + 1, y)]);
			}
			// check left
			if (y - 1 >= 0) {
				linkNodes.add(nodes[getNodeID(x, y - 1)]);
			}
			// check right
			if (y + 1 < WIDTH) {
				linkNodes.add(nodes[getNodeID(x, y + 1)]);
			}
			curNode.setLinkNodes(linkNodes);
			curNode.setDist(startNode, goalNode);
		}
	}

	private int getNodeID(int x, int y) {
		return x * WIDTH + y;
	}

	private void printMap() {
		for (int i = 0; i < nodes.length; i++) {
			System.out.print(nodes[i].getContent() + " ");
			if ((i + 1) % WIDTH == 0) {
				System.out.print("\n");
			}
		}
	}

	public void findPath() {
		ol.enqueue(startNode, 0);
		Node curNode;
		while (!ol.isEmpty()) {
			curNode = ol.removeMin();
			if (curNode == goalNode) {
				System.out.println("Done with cost of: " + ol.getCost());
				markPath();
				printMap();
				return;
			}
			closedList.add(curNode);
			expandNode(curNode);
		}
		System.out.println("No Path");
		printMap();
	}

	private void expandNode(Node n) {
		double tentative_g;
		for (Node nextNode : n.getLinkNodes()) {
			if (closedList.contains(nextNode)) {
				continue;
			}
			tentative_g = n.getDistFromStart() + n.getDistTo(nextNode);
			if (ol.contains(nextNode)
					&& tentative_g >= nextNode.getDistFromStart()) {
				continue;
			}
			nextNode.setPredecessor(n);
			nextNode.setDistFromStart(tentative_g);
			double f = tentative_g + nextNode.getDistToGoal();
			if (ol.contains(nextNode)) {
				ol.decreaseKey(nextNode, f);
			} else {
				ol.enqueue(nextNode, f);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pathfinder pf = new Pathfinder();
		pf.findPath();
	}

}
