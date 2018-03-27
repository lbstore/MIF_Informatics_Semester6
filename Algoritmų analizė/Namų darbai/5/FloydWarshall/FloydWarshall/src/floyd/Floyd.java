package floyd;

public class Floyd {
	private static Reader reader;
	private static String[][] path;
	
	private static void fillMatrix() {
		String str;
		boolean firstRow = true;
		int rowNmb = 0;
		while ((str = reader.getLine()) != null) {
			str.trim(); // istrinam tarpus is priekio ir galo jeigu atsirastu
			String[] row = str.split(" ");
			if (firstRow) {
				path = new String[row.length][row.length];
				firstRow = false;
			}
			path[rowNmb] = row;
			rowNmb++;
		}
	}

	private static void printMatrix() {
		for (int i=0; i< path.length ; i++) {
			for (int j=0; j<path[i].length ; j++) {
				System.out.print(path[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	private static void findShortestPath() {
		for (int i=0; i<path.length ; i++) { //einama pro eilutes
			printMatrix();
			System.out.println("-------------------------------");
			for (int j=0; j<path[i].length ; j++) { //einama pro stulpelius
				if (i==j) 
					continue; //praeinam tuos 0 istrizaineje arba kur yra inf
				
				for (int k=0; k<path[i].length; k++) { //bandoma surasti trumpiausia kelia
					if ((j==k) 
							|| (i==k)
							|| path[i][k].equalsIgnoreCase("inf") 
							|| path[k][j].equalsIgnoreCase("inf"))
						continue; //praeinam tuos 0 istrizaineje arba kur yra inf
					
					int ik = Integer.parseInt(path[i][k]);
					int kj = Integer.parseInt(path[k][j]);
					
					if (path[i][j].equalsIgnoreCase("inf")) {
						path[i][j] = Integer.toString(ik+kj);
					}
					else {
						int ij = Integer.parseInt(path[i][j]);
						
						if (ij > ik+kj)
							path[i][j] = Integer.toString(ik+kj);
					}
				}
			}
		}
	}
	public static void main(String[] args) {
	
		//if (args.length == 0)
		//	System.exit(0);
		
		reader = new Reader("e://floyd.txt");
		
		fillMatrix();
		
		reader.close();
		
		findShortestPath();
		
		printMatrix();
		
	}
}
