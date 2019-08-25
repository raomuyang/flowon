package cn.suniper.flowon;

import cn.suniper.flowon.dag.ArcNode;
import cn.suniper.flowon.dag.Graph;
import cn.suniper.flowon.dag.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public class AOVNet {
    private Graph graph;
    private Map<Integer, VertexStatusEnum> keepColorMap;

    public AOVNet(Graph graph) {
        this.graph = graph;
        keepColorMap = new ConcurrentHashMap<>();

        for (int index: graph.getTopology()) {
            if (graph.getVertexInDegree()[index] == 0) {
                keepColorMap.put(index, VertexStatusEnum.REACHABLE);
            } else {
                keepColorMap.put(index, VertexStatusEnum.UNREACHABLE);
            }
        }
    }

    /**
     * Get the list of passable (accessible) vertices with excluding the passed vertices,
     * note that the passable/passed vertices must be with 0 in degree
     * @return list of {@link Vertex}
     */
    public List<Vertex> getReachableVertices() {
        List<Vertex> reachable = new ArrayList<>();
        int[] inDegreeReduce = new int[this.graph.getVertices().size()];
        int[] vertexInitInDegree = this.graph.getVertexInDegree();

        // iterate by topology sorting
        for (Integer index: graph.getTopology()) {

            int newInDegree = vertexInitInDegree[index] - inDegreeReduce[index];
            // the reachable vertex must be with 0 in degree (after remove the passed vertex)
            if (newInDegree != 0) continue;

            if (keepColorMap.get(index) == VertexStatusEnum.PASSED) {

                ArcNode arc = graph.getVertices().get(index).getFirstArc();
                while (arc != null) {
                    inDegreeReduce[arc.getAdjVex()]++;
                    arc = arc.getNextArc();
                }
                continue;
            }

            reachable.add(graph.getVertices().get(index));

        }
        return reachable;
    }

        public void markVertex(int index, VertexStatusEnum status) {
        this.keepColorMap.put(index, status);
    }

    public void updateVerticesStatus(Map<Integer, VertexStatusEnum> newVerticesStatus) {
        keepColorMap.putAll(newVerticesStatus);
    }

    public Map<Integer, ? extends VertexStatusEnum> getCurrentVerticesStatus() {
        return keepColorMap;
    }
}
