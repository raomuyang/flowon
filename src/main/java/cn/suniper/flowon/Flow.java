package cn.suniper.flowon;

import cn.suniper.flowon.dag.Graph;
import cn.suniper.flowon.dag.Graphs;
import cn.suniper.flowon.dag.Scopes;
import cn.suniper.flowon.dag.Vertex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public Graph getDAG(File configFile, T params) {
        Map<String, NodeRef> nodeRefConf = parser.parseFile(configFile);
        return getGraph(params, nodeRefConf);
    }

    public Graph getDAG(String confContent, T params) {
        Map<String, NodeRef> nodeRefConf = parser.parseString(confContent);
        return getGraph(params, nodeRefConf);
    }

    private Graph getGraph(T params, Map<String, NodeRef> nodeRefs) {
        List<Vertex> vertices =  nodeRefs.values().stream()
                .map(nodeRefList -> this.mapToVertices(nodeRefList, params))
                .reduce((list, newIncrease) -> {
                    list.addAll(newIncrease);
                    return list;
                })
                .orElse(new ArrayList<>());
        for (int index = 0; index < vertices.size(); index++) {
            // reset index
            vertices.get(index).setIndex(index);
        }
        return this.graphs.vertices2DAG(vertices);
    }

    private List<Vertex> mapToVertices(NodeRef nodeRef, T params) {
        return scopeMapper.map(params, nodeRef.getScope())
                .stream()
                .map(uri -> {
                    Vertex v = new Vertex();
                    v.setBindDataURI(uri);
                    v.setAction(nodeRef.getName());
                    v.setDependenciesNodeName(nodeRef.getDependencies());
                    v.setScope(nodeRef.getScope());
                    return v;
                })
                .collect(Collectors.toList());
    }

}
