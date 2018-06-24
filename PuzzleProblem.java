import java.util.*;
import java.lang.Math;

class Node implements Comparable<Node>{
    
    public int[][] matrix;
    
    public Node parent;
    
    // Blank tile coords
    public int x, y;
    
    //cost for A*
    public int cost;
    
    //depth of the node in the search space
    public int depth=0;
    
    public Node(int[][] m, int x, int y, int newX, int newY, Node parent) {
        this.parent = parent;
        if(parent != null)
            this.depth = parent.depth +1;
        this.matrix = new int[3][3];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                matrix[i][j] += m[i][j];
            }
        }
        
        // Swap values
        this.matrix[x][y]       = this.matrix[x][y] + this.matrix[newX][newY];
        this.matrix[newX][newY] = this.matrix[newX][newY] - this.matrix[x][y];
        
        this.cost = Integer.MAX_VALUE;
        
        this.x = newX;
        this.y = newY;
    }
    
    public int compareTo(Node n) {
        if(depth+cost>n.depth+n.cost) 
            return 1;  
        else if(depth+cost<n.depth+n.cost)
            return -1;  
        else
            return 0;  
    }  
    
}


public class PuzzleProblem {
	
	//dimensions, 3*3
	public int n = 3;

	//goal state
	public String goalStr = "123456780";
	
	//no of moves
	public int moves;

	//count of enqueued states
	public int enQ;

	// up, down, left, right
	int[] row = { -1, 1, 0, 0 };
	int[] col = { 0, 0, -1, 1 };

	//to check if the intial state can reach the goal using inversion property
	public boolean isReachable(int[][] initial) {
		int count = 0;
		ArrayList<Integer> array = new ArrayList<Integer>();
		for (int i = 0; i < initial.length; i++) {
			for (int j = 0; j < initial.length; j++) {
				array.add(initial[i][j]);
			}
		}
		for (int i = 0; i < array.size() - 1; i++) {
			for (int j = i + 1; j < array.size(); j++) {
				if (array.get(i) != 0 && array.get(j) != 0 && array.get(i) > array.get(j)) {
					count++;
				}
			}
		}
		return count % 2 == 0;
	}

	public void printState(int[][] m) {
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				if(m[i][j] == 0)
					System.out.print("* ");
				else
					System.out.print(m[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void printPath(Node root) {
		moves=-1;
		if (root == null) {
			return;
		}
		Stack<Node> stack = new Stack<Node>();
		while(root!= null){
			stack.add(root);
			root = root.parent;
		}
		while(!stack.isEmpty()){
			printState(stack.pop().matrix);
			moves++;
			System.out.println();
		}
	}

	//converting matrix to string
	public String convertToString(int[][] m) {
		String ans = "";
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				ans += m[i][j];
			}
		}
		return ans;
	}

	public boolean isPossible(int x, int y) {
		return (x >= 0 && x < n && y >= 0 && y < n);
	}
	
	//calculating heuristic cost for A* using manhattan distance
	public int calcCost(int[][] m) {
		int cost = 0;
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				int val = m[i][j]; 
            	if (val != 0) { 
                	int targetX = (val - 1) / n; // expected row
                	int targetY = (val - 1) % n; // expected col
                	int dx = i - targetX; // x-distance
                	int dy = j - targetY; // y-distance 
                	cost += Math.abs(dx) + Math.abs(dy); 
            	} 
			}
		}
		return cost;
	}

	public void solveDFS(int[][] initial, int x, int y){

		HashSet<String> dfsVisited = new HashSet<String>();
		enQ = 1;
		Stack<Node> stack = new Stack<Node>();

		Node root = new Node(initial, x, y, x, y, null);

		stack.add(root);

		while (!stack.isEmpty())
		{
			Node curr=stack.pop();

			String s = convertToString(curr.matrix);

			if( goalStr.equals(s) ){
	    		printPath(curr);
	    		return;
	    	}

	    	if(!dfsVisited.contains(s)){

	    		//children
				for (int i = 0; i < 4; i++) {
	          		if (isPossible(curr.x + row[i], curr.y + col[i])) {
	          			Node child = new Node(curr.matrix, curr.x, curr.y, curr.x + row[i], curr.y + col[i], curr);
	          			String s1 = convertToString(child.matrix);
	          			if(!dfsVisited.contains(s1)) {
	          				stack.add(child);
	          				enQ++;
	          			}
	          		}
	        	}

	        	dfsVisited.add(s);
			}
		}

	}

	public void solveIDS(int[][] initial, int x, int y){

		int d=0;

		while(true){

			HashSet<String> idsVisited= new HashSet<String>();
			enQ = 1;
			Stack<Node> stack = new Stack<Node>();

			Node root = new Node(initial, x, y, x, y, null);

			stack.add(root);

			while (!stack.isEmpty())
			{
				Node curr=stack.pop();

				String s = convertToString(curr.matrix);

				if( goalStr.equals(s) ){
					printPath(curr);	
					return;
				}

				if(!idsVisited.contains(s) && curr.depth<d){
					
					//children
					for (int i = 0; i < 4; i++) {
			      		if (isPossible(curr.x + row[i], curr.y + col[i])) {
			      			Node child = new Node(curr.matrix, curr.x, curr.y, curr.x + row[i], curr.y + col[i], curr);
			      			String s1 = convertToString(child.matrix);
			      			if(!idsVisited.contains(s1)) {
			      				stack.add(child);
			      				enQ++;
			      			}
			      		}
			    	}

			    	idsVisited.add(s);
				}
			}

			d++;
		
		}

	}

	public void solveAstar(int[][] initial, int x, int y) {

		HashSet<String> asClosed = new HashSet<String>();
		enQ = 1;
		PriorityQueue<Node> pq = new PriorityQueue<Node>();

		Node root = new Node(initial, x, y, x, y, null);
		root.cost = calcCost(initial);

		pq.add(root);
		
		while (!pq.isEmpty()) {

			Node min = pq.poll();

			String s = convertToString(min.matrix);

			if( goalStr.equals(s) ) {
				printPath(min);
				return;
			}

			if(!asClosed.contains(s)){

				//children
				for (int i = 0; i < 4; i++) {
	          		if (isPossible(min.x + row[i], min.y + col[i])) {
	          			Node child = new Node(min.matrix, min.x, min.y, min.x + row[i], min.y + col[i], min);
	          			String s1 = convertToString(child.matrix);
	          			if(!asClosed.contains(s1)) {
	          				child.cost = calcCost(child.matrix);
	          				pq.add(child);
	          				enQ++;
	          			}
	          		}
	        	}

	        	asClosed.add(s);
			}
			
		}
	}

	public static void solve(int algo){

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the initial board configuration: ");

		int[][] initial = new int[3][3];
		int x=0,y=0; //position of the blank tile
		for (int i = 0; i < initial.length; i++) {
			for (int j = 0; j < initial.length; j++) {
				String ip = sc.next();
				if( ip.equals("*") ){
					initial[i][j] = 0;
					x=i; y=j;
				}
				else
					initial[i][j] = Integer.parseInt(ip);
			}
		}

		PuzzleProblem p = new PuzzleProblem();

		if (p.isReachable(initial)) {
			System.out.println();
			System.out.println("Output:");
			System.out.println();

			switch(algo)
			{
				case 1 :
					p.solveDFS(initial, x, y);
					break;
				case 2 :
					p.solveIDS(initial, x, y);
					break;
				case 3 :
					p.solveAstar(initial, x, y);
			}

			System.out.println("Number of moves = "+p.moves);
			System.out.println("Number of states enqueued = "+p.enQ);
		} 
		else {
			System.out.println("The given initial is impossible to solve");
		}

	}
	
	public static void main(String[] args) {
			if(args.length == 0){
				System.out.println("Enter the name of an algorithm");
				return;
			}
			switch(args[0])
			{
				case "dfs" :
					solve(1);
					break;
				case "ids" :
					solve(2);
					break;
				case "a*" :
					solve(3);
					break;
				default :
					System.out.println(" You have entered an invalid algorithm name. ");

			}
	}

}
