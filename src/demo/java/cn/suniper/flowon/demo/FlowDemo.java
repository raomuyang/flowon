package cn.suniper.flowon.demo;

import cn.suniper.flowon.AOVNet;
import cn.suniper.flowon.Flow;
import cn.suniper.flowon.dag.Graph;
import cn.suniper.flowon.dag.Scopes;
import cn.suniper.flowon.dag.Vertex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author Rao Mengnan
 * on 2019-08-11.
 */
class FlowDemo {

    private List<InputFile> inputParameters;
    private File flowConfigFile;

    public FlowDemo() throws IOException {
        String testParam = getClass().getResource("/input-params-1.json").getPath();
        flowConfigFile = new File(getClass().getResource("/flow.conf").getPath());
        Gson gson = new Gson();
        try (FileReader r = new FileReader(testParam)) {
            JsonReader reader = new JsonReader(r);
            inputParameters = gson.fromJson(reader, new TypeToken<List<InputFile>>() {
            }.getType());
        }
    }

    void testDAG() {
        Scopes<List<InputFile>> scopes = new ScopeMapper();
        Flow<List<InputFile>> flow = new Flow<>(scopes);
        Graph graph = flow.getDAG(flowConfigFile, inputParameters);
        AOVNet aov = new AOVNet(graph);

        List<Vertex> reachableVertices = aov.getReachableVertices();
        for (Vertex v: reachableVertices) {
            assert v.getAction().equals("download");
        }

    }
}