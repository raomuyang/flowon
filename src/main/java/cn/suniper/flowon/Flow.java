package cn.suniper.flowon;

import cn.suniper.flowon.dag.AdjListGraph;
import cn.suniper.flowon.dag.Graphs;
import cn.suniper.flowon.dag.Scopes;
import cn.suniper.flowon.dag.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public class Flow<T> {
    private static final FlowParser DEFAULT_PARSER = new FlowParser();
    private Scopes<T> scopeMapper;
    private FlowParser parser;
    private Graphs graphs;

    public Flow(Scopes<T> scopeMapper) {
        this(scopeMapper, DEFAULT_PARSER);
    }

    public Flow(Scopes<T> scopeMapper, FlowParser parser) {
        this.scopeMapper = scopeMapper;
        this.parser = parser;
        this.graphs = new Graphs(this.scopeMapper);
    }

    public AdjListGraph getDAG(String confContent, T params) {
        List<NodeRef> nodeRefs = parser.parseString(confContent);
        List<Vertex> vertices =  nodeRefs.stream()
                .map(nodeRef -> this.mapToVertices(nodeRef, params))
                .reduce((list, newList) -> {
                    list.addAll(newList);
                    return list;
                }).orElse(new ArrayList<>());
        return this.graphs.vertices2DAG(vertices);
    }

    private List<Vertex> mapToVertices(NodeRef nodeRef, T params) {
        return scopeMapper.map(params, nodeRef.getScope())
                .stream()
                .map(uri -> {
                    Vertex v = new Vertex();
                    v.setBindDataURI(uri);
                    v.setAction(nodeRef.getName());
                    v.setDependencies(nodeRef.getDependencies());
                    v.setScope(nodeRef.getScope());
                    return v;
                })
                .collect(Collectors.toList());
    }

}
