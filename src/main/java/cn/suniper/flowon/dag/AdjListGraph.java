package cn.suniper.flowon.dag;

import java.util.Arrays;
import java.util.List;

/**
 * @author Rao Mengnan
 * on 2019-08-07.
 */
class AdjListGraph implements Graph {
    private List<Vertex> vertices;
    private int[] topology;
    private int[] vertexInDegree;

    public AdjListGraph(List<Vertex> vertices, int[] topology, int[] vertexInDegree) {
        this.vertices = vertices;
        this.topology = topology;
        this.vertexInDegree = vertexInDegree;
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    @Override
    public int[] getTopology() {
        return topology;
    }

    public void setTopology(int[] topology) {
        this.topology = topology;
    }

    @Override
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
