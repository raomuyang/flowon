package cn.suniper.flowon.dag;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * @author Rao Mengnan
 * on 2019-08-07.
 */
public class Graphs {

    private Scopes scopes;

    public Graphs(Scopes scopes) {
        this.scopes = scopes;
    }

    public Graph vertices2DAG(List<Vertex> vertexNodes) {
        return vertices2DAG(vertexNodes, true);
    }

    public Graph vertices2DAG(List<Vertex> vertexNodes, boolean sortedByIndex) {
        if (scopes == null) {
            throw new IllegalStateException("scopes kit was not set");
        }
        if (!sortedByIndex) {
            vertexNodes.sort((v1, v2) -> v1.getIndex() - v2.getIndex());
        }
        int[] inDegree = new int[vertexNodes.size()];
        for (Vertex v1 : vertexNodes) {
            ArcNode arc = null;
            for (Vertex v2 : vertexNodes) {
                if (v1.getIndex() == v2.getIndex()) continue;

                // action: v2 dependency v1; resource: dataURI_v1 is subset of dataURI_v2
                if (v1.getAction().equals(v2.getDependencies())
                        && scopes.isSubset(v2.getBindDataURI(), v1.getBindDataURI())) {
                    ArcNode arcV1ToV2 = new ArcNode(v2.getIndex(), null);
                    inDegree[v2.getIndex()]++;
                    if (arc == null) {
                        arc = arcV1ToV2;
                        v1.setFirstArc(arc);
                    } else {
                        arc.setNextArc(arcV1ToV2);
                        arc = arcV1ToV2;
                    }
                }
            }
        }
        int[] topo = topologySorting(vertexNodes, inDegree);
        return new AdjListGraph(vertexNodes, topo, inDegree);
    }


    public int[] topologySorting(List<Vertex> vertexNodes, int[] inDegree) {
        if (vertexNodes == null || vertexNodes.size() != inDegree.length) {
            throw new IllegalArgumentException("the numbers of vertex list must be equals with the length of inDegreeArray.");
        }

        int[] topo = new int[inDegree.length];
        inDegree = Arrays.copyOf(inDegree, inDegree.length);
        Stack<Integer> zidIndex = new Stack<>();

        int index = 0;
        for (int i : inDegree) {
            if (i == 0) zidIndex.push(index);
            index++;
        }

        index = 0;
        while (!zidIndex.empty()) {
            int i = zidIndex.pop();
            topo[index++] = i;
            ArcNode arc = vertexNodes.get(i).getFirstArc();
            while (arc != null) {
                inDegree[arc.getAdjVex()]--;
                if (inDegree[arc.getAdjVex()] == 0) zidIndex.push(arc.getAdjVex());
                arc = arc.getNextArc();
            }
        }
        if (index < inDegree.length) throw new IllegalArgumentException("Existence ring!");
        return topo;
    }

}
