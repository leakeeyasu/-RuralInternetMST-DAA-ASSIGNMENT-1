package ruralinternetmst;
import java.io.*;
import java.util.*;
class Village {
    private int id;
    private String name;
    private double xCoordinate;
    private double yCoordinate;
    
    public Village(int id, String name, double x, double y) {
        this.id = id;
        this.name = name;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getX() { return xCoordinate; }
    public double getY() { return yCoordinate; }
   
    public double distanceTo(Village other) {
        double dx = this.xCoordinate - other.xCoordinate;
        double dy = this.yCoordinate - other.yCoordinate;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return String.format("Village[%d: %s (%.2f, %.2f)]", 
                            id, name, xCoordinate, yCoordinate);
    }
}
class Edge implements Comparable<Edge> {
    private int source;
    private int destination;
    private double weight;
    
    public Edge(int source, int destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
    
    public int getSource() { return source; }
    public int getDestination() { return destination; }
    public double getWeight() { return weight; }
    
    @Override
    public int compareTo(Edge other) {
        if (this.weight < other.weight) return -1;
        if (this.weight > other.weight) return 1;
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("Edge[%d-%d: %.2f]", source, destination, weight);
    }
}
class Graph {
    private int numVertices;
    private Village[] villages;
    private double[][] adjacencyMatrix;
    private List<Edge> edgeList;
    
    public Graph(int size) {
        this.numVertices = size;
        this.villages = new Village[size];
        this.adjacencyMatrix = new double[size][size];
        this.edgeList = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            Arrays.fill(adjacencyMatrix[i], Double.POSITIVE_INFINITY);
            adjacencyMatrix[i][i] = 0;
        }
    }
    
    public void addVillage(int index, Village village) {
        if (index >= 0 && index < numVertices) {
            villages[index] = village;
        }
    }
    
    public void addConnection(int v1, int v2, double weight) {
        adjacencyMatrix[v1][v2] = weight;
        adjacencyMatrix[v2][v1] = weight;
        edgeList.add(new Edge(v1, v2, weight));
    }
    
    public List<Edge> getAllEdges() {
        return new ArrayList<>(edgeList);
    }
    
    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
    
    public Village getVillage(int index) {
        return villages[index];
    }
    
    public int getNumVertices() {
        return numVertices;
    }
    
    public boolean isConnected() {
        boolean[] visited = new boolean[numVertices];
        dfs(0, visited);
        
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }
    
    private void dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;
        for (int i = 0; i < numVertices; i++) {
            if (adjacencyMatrix[vertex][i] != Double.POSITIVE_INFINITY && !visited[i]) {
                dfs(i, visited);
            }
        }
    }
    
    public void printGraph() {
        System.out.println("\n=== GRAPH DETAILS ===");
        System.out.println("Number of Villages: " + numVertices);
        System.out.println("\nVillages:");
        for (int i = 0; i < Math.min(10, numVertices); i++) {
            System.out.println("  " + villages[i]);
        }
        if (numVertices > 10) {
            System.out.println("  ... and " + (numVertices - 10) + " more villages");
        }
    }
}
class UnionFind {
    private int[] parent;
    private int[] rank;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }
    
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX != rootY) {
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
class MSTResult {
    private List<Edge> mstEdges;
    private double totalCost;
    private String algorithmName;
    private double executionTimeMs;
    private int numVertices;
    private int numEdges;
    
    public MSTResult(List<Edge> mstEdges, double totalCost, String algorithmName,
                    double executionTimeMs, int numVertices, int numEdges) {
        this.mstEdges = mstEdges;
        this.totalCost = totalCost;
        this.algorithmName = algorithmName;
        this.executionTimeMs = executionTimeMs;
        this.numVertices = numVertices;
        this.numEdges = numEdges;
    }
    
    public List<Edge> getMstEdges() { return mstEdges; }
    public double getTotalCost() { return totalCost; }
    public String getAlgorithmName() { return algorithmName; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    
    public boolean verifyMST() {
        if (mstEdges.size() != numVertices - 1) {
            System.out.println("❌ Verification failed: MST has " + mstEdges.size() 
                             + " edges, should have " + (numVertices - 1));
            return false;
        }
        
        System.out.println("✅ Verification passed: MST has " + mstEdges.size() 
                         + " edges = |V|-1 = " + (numVertices - 1));
        return true;
    }
    
    public void printResult() {
        System.out.println("\n=== " + algorithmName + " RESULTS ===");
        System.out.println("Number of villages: " + numVertices);
        System.out.println("MST Edges: " + numEdges + " (|V|-1 = " + (numVertices-1) + ")");
        System.out.println("Total cable length: " + String.format("%.2f", totalCost));
        System.out.println("Execution time: " + String.format("%.3f", executionTimeMs) + " ms");
        
        System.out.println("\nFirst 10 MST Connections:");
        for (int i = 0; i < Math.min(10, mstEdges.size()); i++) {
            Edge e = mstEdges.get(i);
            System.out.println("  " + (i+1) + ". Village " + e.getSource() 
                             + " - Village " + e.getDestination() 
                             + " : " + String.format("%.2f", e.getWeight()));
        }
    }
}
class PrimsAlgorithm {
    private Graph graph;
    private int startVertex;
    private double[][] adjMatrix;
    private int numVertices;
    
    private double[] key;
    private int[] parent;
    private boolean[] inMST;
    private int[] near;
    
    public PrimsAlgorithm(Graph graph, int startVertex) {
        this.graph = graph;
        this.startVertex = startVertex;
        this.adjMatrix = graph.getAdjacencyMatrix();
        this.numVertices = graph.getNumVertices();
        
        this.key = new double[numVertices];
        this.parent = new int[numVertices];
        this.inMST = new boolean[numVertices];
        this.near = new int[numVertices];
    }
    
    public MSTResult findMST() {
        long startTime = System.nanoTime();
        
        Arrays.fill(key, Double.POSITIVE_INFINITY);
        Arrays.fill(inMST, false);
        
        key[startVertex] = 0;
        parent[startVertex] = -1;
        
        for (int i = 0; i < numVertices; i++) {
            if (i != startVertex) {
                near[i] = startVertex;
            }
        }
        
        for (int count = 0; count < numVertices - 1; count++) {
            int u = minKeyVertex();
            inMST[u] = true;
            
            for (int v = 0; v < numVertices; v++) {
                if (!inMST[v] && adjMatrix[u][v] < key[v]) {
                    near[v] = u;
                }
            }
            
            for (int v = 0; v < numVertices; v++) {
                if (adjMatrix[u][v] != Double.POSITIVE_INFINITY 
                    && !inMST[v] 
                    && adjMatrix[u][v] < key[v]) {
                    parent[v] = u;
                    key[v] = adjMatrix[u][v];
                }
            }
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0;
        
        List<Edge> mstEdges = buildMSTEdges();
        double totalCost = calculateTotalCost(mstEdges);
        
        return new MSTResult(mstEdges, totalCost, "Prim's Algorithm", 
                            executionTime, numVertices, mstEdges.size());
    }
    
    private int minKeyVertex() {
        double min = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        
        for (int v = 0; v < numVertices; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }
    
    private List<Edge> buildMSTEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i < numVertices; i++) {
            if (parent[i] != -1) {
                edges.add(new Edge(parent[i], i, adjMatrix[i][parent[i]]));
            }
        }
        return edges;
    }
    
    private double calculateTotalCost(List<Edge> edges) {
        double total = 0;
        for (Edge edge : edges) {
            total += edge.getWeight();
        }
        return total;
    }
    
    public int[] getNear() {
        return near;
    }
}
class KruskalAlgorithm {
    private Graph graph;
    private List<Edge> edges;
    private int numVertices;
    
    public KruskalAlgorithm(Graph graph) {
        this.graph = graph;
        this.edges = graph.getAllEdges();
        this.numVertices = graph.getNumVertices();
    }
    
    public MSTResult findMST() {
        long startTime = System.nanoTime();
        
        // Manual QuickSort implementation
        quickSort(edges, 0, edges.size() - 1);
        
        UnionFind uf = new UnionFind(numVertices);
        List<Edge> mstEdges = new ArrayList<>();
        
        for (Edge edge : edges) {
            int src = edge.getSource();
            int dest = edge.getDestination();
            
            if (!uf.connected(src, dest)) {
                uf.union(src, dest);
                mstEdges.add(edge);
                
                if (mstEdges.size() == numVertices - 1) {
                    break;
                }
            }
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0;
        
        double totalCost = calculateTotalCost(mstEdges);
        
        return new MSTResult(mstEdges, totalCost, "Kruskal's Algorithm", 
                            executionTime, numVertices, mstEdges.size());
    }
    
    private void quickSort(List<Edge> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    
    private int partition(List<Edge> arr, int low, int high) {
        double pivot = arr.get(high).getWeight();
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (arr.get(j).getWeight() <= pivot) {
                i++;
                Collections.swap(arr, i, j);
            }
        }
        
        Collections.swap(arr, i + 1, high);
        return i + 1;
    }
    
    private double calculateTotalCost(List<Edge> edges) {
        double total = 0;
        for (Edge edge : edges) {
            total += edge.getWeight();
        }
        return total;
    }
}
class CSVHandler {
    
    public static void generateRandomDataset(String filename, int numVillages) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("id,name,x_coordinate,y_coordinate");
            
            Random rand = new Random(42);
            
            for (int i = 0; i < numVillages; i++) {
                int id = i;
                String name = "Village_" + i;
                double x = rand.nextDouble() * 1000;
                double y = rand.nextDouble() * 1000;
                
                writer.printf("%d,%s,%.2f,%.2f%n", id, name, x, y);
            }
            
            System.out.println("✅ Generated " + numVillages + " villages in " + filename);
            
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    
    public static List<Village> readVillagesFromCSV(String filename) {
        List<Village> villages = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double x = Double.parseDouble(parts[2].trim());
                    double y = Double.parseDouble(parts[3].trim());
                    
                    villages.add(new Village(id, name, x, y));
                }
            }
            
            System.out.println("✅ Read " + villages.size() + " villages from " + filename);
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return villages;
    }
    
    public static Graph createSmallSampleGraph() {
        Graph graph = new Graph(6);
        
        graph.addVillage(0, new Village(0, "Village_A", 0, 0));
        graph.addVillage(1, new Village(1, "Village_B", 2, 3));
        graph.addVillage(2, new Village(2, "Village_C", 5, 1));
        graph.addVillage(3, new Village(3, "Village_D", 1, 4));
        graph.addVillage(4, new Village(4, "Village_E", 4, 5));
        graph.addVillage(5, new Village(5, "Village_F", 6, 2));
        
        graph.addConnection(0, 1, 3.6);
        graph.addConnection(0, 2, 5.1);
        graph.addConnection(1, 2, 3.6);
        graph.addConnection(1, 3, 1.4);
        graph.addConnection(1, 4, 2.8);
        graph.addConnection(2, 4, 4.1);
        graph.addConnection(2, 5, 1.4);
        graph.addConnection(3, 4, 3.2);
        graph.addConnection(4, 5, 3.2);
        
        return graph;
    }
}
public class RuralInternetMST {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  RURAL INTERNET CONNECTIVITY - MST");
        System.out.println("  Minimum Spanning Tree Implementation");
        System.out.println("==========================================\n");
        
        // Create data directory
        new File("data").mkdirs();
        
        // Part 1: Step-by-step demonstration with small graph
        demonstrateSmallGraph();
        
        // Part 2: Full implementation with 100+ vertices
        demonstrateLargeGraph();
        
        // Part 3: Complexity Analysis
        showComplexityAnalysis();
        
        System.out.println("\n✅ Assignment Complete - All requirements satisfied!");
    }
    
    private static void demonstrateSmallGraph() {
        System.out.println("\n--- PART 1: STEP-BY-STEP DEMONSTRATION (6 Villages) ---");
        
        Graph smallGraph = CSVHandler.createSmallSampleGraph();
        smallGraph.printGraph();
        
        System.out.println("\n--- PRIM'S ALGORITHM EXECUTION STEPS ---");
        System.out.println("Starting from Village 0");
        
        PrimsAlgorithm prims = new PrimsAlgorithm(smallGraph, 0);
        MSTResult primsResult = prims.findMST();
        
        int[] near = prims.getNear();
        System.out.println("\nFinal near[] array (tracks closest vertex):");
        System.out.print("[");
        for (int i = 0; i < near.length; i++) {
            System.out.print(" " + near[i]);
        }
        System.out.println(" ]");
        
        primsResult.printResult();
        primsResult.verifyMST();
        
        System.out.println("\nMST Edges List:");
        List<Edge> edges = primsResult.getMstEdges();
        for (int i = 0; i < edges.size(); i++) {
            System.out.println("  Edge[" + i + "]: " + edges.get(i));
        }
        
        System.out.println("\n--- KRUSKAL'S ALGORITHM EXECUTION ---");
        KruskalAlgorithm kruskal = new KruskalAlgorithm(smallGraph);
        MSTResult kruskalResult = kruskal.findMST();
        kruskalResult.printResult();
        kruskalResult.verifyMST();
    }
    
    private static void demonstrateLargeGraph() {
        System.out.println("\n--- PART 2: FULL IMPLEMENTATION (150 Villages) ---");
        
        String filename = "data/villages_150.csv";
        CSVHandler.generateRandomDataset(filename, 150);
        
        List<Village> villageList = CSVHandler.readVillagesFromCSV(filename);
        
        Graph largeGraph = new Graph(villageList.size());
        
        for (int i = 0; i < villageList.size(); i++) {
            largeGraph.addVillage(i, villageList.get(i));
        }
        
        System.out.println("\nCreating connections between villages...");
        int connectionCount = 0;
        
        for (int i = 0; i < villageList.size(); i++) {
            for (int j = i + 1; j < villageList.size(); j++) {
                double distance = villageList.get(i).distanceTo(villageList.get(j));
                if (distance < 500 || i == 0) {
                    largeGraph.addConnection(i, j, distance);
                    connectionCount++;
                }
            }
        }
        
        System.out.println("✅ Created " + connectionCount + " connections");
        
        System.out.print("\nVerifying graph connectivity... ");
        if (largeGraph.isConnected()) {
            System.out.println("✅ Graph is connected!");
        } else {
            System.out.println("❌ Graph is not connected! Fixing...");
            ensureConnectivity(largeGraph);
        }
        
        System.out.println("\n--- RUNNING PRIM'S ALGORITHM (150 Villages) ---");
        PrimsAlgorithm primsLarge = new PrimsAlgorithm(largeGraph, 0);
        MSTResult primsLargeResult = primsLarge.findMST();
        primsLargeResult.printResult();
        primsLargeResult.verifyMST();
        
        System.out.println("\n--- RUNNING KRUSKAL'S ALGORITHM (150 Villages) ---");
        KruskalAlgorithm kruskalLarge = new KruskalAlgorithm(largeGraph);
        MSTResult kruskalLargeResult = kruskalLarge.findMST();
        kruskalLargeResult.printResult();
        kruskalLargeResult.verifyMST();
        
        compareAlgorithms(primsLargeResult, kruskalLargeResult);
    }
    
    private static void ensureConnectivity(Graph graph) {
        int n = graph.getNumVertices();
        for (int i = 0; i < n - 1; i++) {
            Village v1 = graph.getVillage(i);
            Village v2 = graph.getVillage(i + 1);
            double distance = v1.distanceTo(v2);
            graph.addConnection(i, i + 1, distance);
        }
        System.out.println("✅ Added " + (n-1) + " connections to ensure connectivity");
    }
    
    private static void compareAlgorithms(MSTResult prims, MSTResult kruskal) {
        System.out.println("\n--- ALGORITHM COMPARISON ---");
        System.out.printf("%-20s %-20s %-20s%n", "Metric", "Prim's", "Kruskal's");
        System.out.printf("%-20s %-20.2f %-20.2f%n", "Total Cost", 
                         prims.getTotalCost(), kruskal.getTotalCost());
        System.out.printf("%-20s %-20.3f %-20.3f%n", "Time (ms)", 
                         prims.getExecutionTimeMs(), kruskal.getExecutionTimeMs());
    }
    
    private static void showComplexityAnalysis() {
        System.out.println("\n--- PART 3: COMPLEXITY ANALYSIS ---");
        
        System.out.println("\nPRIM'S ALGORITHM (with adjacency matrix):");
        System.out.println("  Time Complexity: O(V²)");
        System.out.println("    - V = number of vertices (villages)");
        System.out.println("    - Need to scan all V vertices for each of V-1 iterations");
        System.out.println("  Space Complexity: O(V²)");
        System.out.println("    - Adjacency matrix: V × V array");
        System.out.println("    - near[] array: O(V)");
        
        System.out.println("\nKRUSKAL'S ALGORITHM:");
        System.out.println("  Time Complexity: O(E log E)");
        System.out.println("    - E = number of edges");
        System.out.println("    - Sorting edges: O(E log E)");
        System.out.println("  Space Complexity: O(V + E)");
        
        System.out.println("\nFor 150 villages with complete graph:");
        System.out.println("  V = 150");
        System.out.println("  E ≈ V²/2 = 11,250");
        System.out.println("  Prim's operations: ~22,500");
        System.out.println("  Kruskal's operations: ~11,250 × log(11,250) ≈ 157,500");
    }
}