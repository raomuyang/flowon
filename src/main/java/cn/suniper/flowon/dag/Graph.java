package cn.suniper.flowon.dag;

import java.util.List;

/**
 * @author Rao Mengnan
 * on 2019-08-11.
 */
public interface Graph {
    List<Vertex> getVertices();
    int[] getTopology();
    int[] getVertexInDegree();
}
