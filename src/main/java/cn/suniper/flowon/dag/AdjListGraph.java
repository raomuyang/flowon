package cn.suniper.flowon.dag;

import java.util.Arrays;
import java.util.List;

/**
 * @author Rao Mengnan
 * on 2019-08-07.
 */
public class AdjListGraph {
    private List<Vertex> vertices;
    private int[] topology;
    private int[] vertexInDegree;

    public AdjListGraph(List<Vertex> vertices, int[] topology, int[] vertexInDegree) {
        this.vertices = vertices;
        this.topology = topology;
        this.vertexInDegree = vertexInDegree;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public int[] getTopology() {
        return topology;
    }

    public void setTopology(int[] topology) {
        this.topology = topology;
    }

    public int[] getVertexInDegree() {
        return vertexInDegree;
    }

    public void setVertexInDegree(int[] vertexInDegree) {
        this.vertexInDegree = vertexInDegree;
    }

    @Override
    public String toString() {
        return "AdjListGraph{" +
                "vertices=" + vertices +
                ", topology=" + Arrays.toString(topology) +
                ", vertexInDegree=" + Arrays.toString(vertexInDegree) +
                '}';
    }
}
