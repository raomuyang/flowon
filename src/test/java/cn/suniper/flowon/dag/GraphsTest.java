package cn.suniper.flowon.dag;

import cn.suniper.flowon.InputFile;
import cn.suniper.flowon.ScopeMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
class GraphsTest {

    private String testParam = getClass().getResource("/input-params-1.json").getPath();
    private List<InputFile> inputFiles;
    private ScopeMapper mapper = new ScopeMapper();
    private Graphs graphs = new Graphs(mapper);

    @BeforeEach
    void before() throws IOException {
        Gson gson = new Gson();
        try (FileReader r = new FileReader(testParam)){
            JsonReader reader = new JsonReader(r);
            inputFiles = gson.fromJson(reader, new TypeToken<List<InputFile>>(){}.getType());
        }
    }

    @Test
    @DisplayName("map the inputs to vertices and render DAG")
    void testVertices2DAG() {
        List<Vertex> vertices = new ArrayList<>();
        int index = 0;

        // upload by file
        for (String uri: mapper.map(inputFiles, "file")) {
            Vertex v = new Vertex();
            v.setScope("file");
            v.setAction("download");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // mapping by group
        for (String uri: mapper.map(inputFiles, "group")) {
            Vertex v = new Vertex();
            v.setScope("group");
            v.setAction("mapping");
            v.setIndex(index++);
            v.setDependenciesNodeName("download");
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // analysis by project
        for (String uri: mapper.map(inputFiles, "project")) {
            Vertex v = new Vertex();
            v.setScope("project");
            v.setAction("analysis");
            v.setDependenciesNodeName("mapping");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        assertEquals(index, vertices.size());

        Graph graph = graphs.vertices2DAG(vertices);
        for (int i = 0; i < 4; i++) {
            assertEquals(0, graph.getVertexInDegree()[i]);
        }

        for (int i = 4; i < 6; i++) {
            assertEquals(2, graph.getVertexInDegree()[i]);
        }

        assertEquals(2, graph.getVertexInDegree()[6]);
    }

    @Test
    @DisplayName("render DAG failed because of ring")
    void testVertices2DAGFailed() {
        List<Vertex> vertices = new ArrayList<>();
        int index = 0;

        // upload by file
        for (String uri: mapper.map(inputFiles, "file")) {
            Vertex v = new Vertex();
            v.setScope("file");
            v.setAction("download");
            v.setDependenciesNodeName("analysis");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // mapping by group
        for (String uri: mapper.map(inputFiles, "group")) {
            Vertex v = new Vertex();
            v.setScope("group");
            v.setAction("mapping");
            v.setIndex(index++);
            v.setDependenciesNodeName("download");
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // analysis by project
        for (String uri: mapper.map(inputFiles, "project")) {
            Vertex v = new Vertex();
            v.setScope("project");
            v.setAction("analysis");
            v.setDependenciesNodeName("mapping");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        graphs = new Graphs(new Scopes() {
            @Override
            public List<String> map(Object params, String scope) {
                return null;
            }

            @Override
            public boolean isInclude(String dataURI, String subDataURI, String dependenciesNodeName) {
                return true;
            }
        });
        // ring
        assertThrows(IllegalArgumentException.class, () -> graphs.vertices2DAG(vertices));

    }

}