package cn.suniper.flowon;

import cn.suniper.flowon.dag.ArcNode;
import cn.suniper.flowon.dag.Graph;
import cn.suniper.flowon.dag.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public class AOV {
    private Graph graph;
    private Map<Integer, AOVColorEnum> keepColorMap;

    public AOV(Graph graph) {
        this.graph = graph;
        keepColorMap = new ConcurrentHashMap<>();

        for (int index: graph.getTopology()) {
            if (graph.getVertexInDegree()[index] == 0) {
                keepColorMap.put(index, AOVColorEnum.AVAILABLE);
            } else {
                keepColorMap.put(index, AOVColorEnum.BLOCKING);
            }
        }
    }

    /**
     * Get the list of passable (accessible) vertices with excluding the passed vertices,
     * note that the passable/passed vertices must be with 0 in degree
     * @return list of {@link Vertex}
     */
    public List<Vertex> getPassableVertices() {
        List<Vertex> passable = new ArrayList<>();
        Map<Integer, Integer> inDegreeReduce = new HashMap<>();
        int[] vertexInitInDegree = this.graph.getVertexInDegree();

        // iterate by topology sorting
        for (Integer index: graph.getTopology()) {

            int newInDegree = vertexInitInDegree[index] - inDegreeReduce.computeIfAbsent(index, k -> 0);
            // the passable vertex must be with 0 in degree (after remove the passed vertex)
            if (newInDegree != 0) continue;

            if (keepColorMap.get(index) == AOVColorEnum.PASSED) {

                ArcNode arc = graph.getVertices().get(index).getFirstArc();
                while (arc != null) {
                    inDegreeReduce.compute(arc.getAdjVex(), (key, old) -> {
                        if (old == null) old = 0;
                        return old + 1;
                    });
                    arc = arc.getNextArc();
                }
                continue;
            } else if (keepColorMap.get(index) == AOVColorEnum.AVAILABLE) {
                passable.add(graph.getVertices().get(index));
                continue;
            } else if (keepColorMap.get(index) == AOVColorEnum.BLOCKED) {
                passable.add(graph.getVertices().get(index));
                continue;
            }
            if (newInDegree == 0) passable.add(graph.getVertices().get(index));

        }
        return passable;
    }

    public void markVertex(int index, AOVColorEnum color) {
        this.keepColorMap.put(index, color);
    }

    public void updateVerticesColor(Map<Integer, AOVColorEnum> newColor) {
        keepColorMap.putAll(newColor);
    }

    public Map<Integer, AOVColorEnum> getCurrentVerticesColor() {
        return keepColorMap;
    }
}
