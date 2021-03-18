package socialnetwork.domain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Graph {

    private int v;
    private final LinkedList<Integer>[] adjList;
    private final ArrayList<Integer> path=new ArrayList<>();
    private final ArrayList<Integer> maxPath=new ArrayList<>();


    /**
     * creates an adjacency list with the users and another adjacency list for every user
     * @param v not null
     */
    public Graph(int v){
        this.v=v;
        adjList= new LinkedList[v];
        for(int i=0;i<v;i++){
            adjList[i]=new LinkedList();
        }
    }

    /**
     * creates the edges between 2 users(vertexes)
     * @param u1 identification id of user 1
     * @param u2 identification id of user 2
     */
    public void addRelation(int u1, int u2){
        adjList[u1].add(u2);
        adjList[u2].add(u1);
    }


    /**
     * DFS algorithm, visits all the reachable friends from one user
     * @param u
     * @param visited
     */
    void visitNeighbours(int u, boolean visited[]){
        visited[u]=true;
        Iterator<Integer> i= adjList[u].listIterator();
        while(i.hasNext()){
            int next=i.next();
            if(!visited[next]) {
                visitNeighbours(next, visited);
            }
        }

    }

    /**
     * @return the number of isolated groups
     */
    public int countGroups(){
        boolean visited[]= new boolean[v];
        int existing_groups=0;
        for(int i=0;i<v;i++) {
            if (!visited[i]) {
                existing_groups++;
                visitNeighbours(i,visited);
            }
        }

        return existing_groups;
    }

    public ArrayList<Integer> getLongestPath(){


        for(int i=0;i<v;i++){
            {
                path.add(i);
                visitNeighboursPath(i,path,maxPath);
                path.clear();
            }
        }
        return maxPath;
    }

    private void visitNeighboursPath(int u, ArrayList<Integer> path, ArrayList<Integer> maxPath) {
       ArrayList<Integer> savePoint=new ArrayList<Integer>(path);

        Iterator<Integer> i= adjList[u].listIterator();
        while(i.hasNext()){
            int f=i.next();
            if(!path.contains(f)){
                ArrayList<Integer> newPath=new ArrayList<Integer>(savePoint);
                newPath.add(f);
                visitNeighboursPath(f,newPath,maxPath);

                if(maxPath.size()<newPath.size()){
                    maxPath.clear();
                    maxPath.addAll(newPath);
                }
            }
        }
    }
}
