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
public class AOVColoring {
    private Graph graph;
    private Map<Integer, AOVColorEnum> keepColorMap;

    public AOVColoring(Graph graph) {
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

    public List<Vertex> getPassableVertices() {
        List<Vertex> passable = new ArrayList<>();
        Map<Integer, Integer> inDegreeReduce = new HashMap<>();
        // iterate by topology sorting
        for (Integer index: graph.getTopology()) {
            if (keepColorMap.get(index) == AOVColorEnum.PASSED) {
                ArcNode arc = graph.getVertices().get(index).getFirstArc();
                while (arc != null) {
                    inDegreeReduce.compute(arc.getAdjVex(), (key, old) -> {
                        if (old == null) old = 0;
                        return old + 1;
                    });
                    arc = arc.getNextArc();
                }
            } else if (keepColorMap.get(index) == AOVColorEnum.AVAILABLE) {
                passable.add(graph.getVertices().get(index));
                continue;
            } else if (keepColorMap.get(index) == AOVColorEnum.BLOCKED) {
                continue;
            }

            int newInDegree = graph.getVertexInDegree()[index] - inDegreeReduce.computeIfAbsent(index, k -> 0);
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
