package cn.suniper.flowon;

import cn.suniper.flowon.dag.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Rao Mengnan
 * on 2019-08-11.
 */
class AOVTest {

    private String testParam = getClass().getResource("/input-params-1.json").getPath();
    private AOVNet aov;

    @BeforeEach
    void before() throws IOException {
        Graph graph = getDAG();
        aov = new AOVNet(graph);
    }

    @Test
    void getAccessibleVertices() {
        List<Vertex> vertices = aov.getReachableVertices();
        assertEquals(4, vertices.size());
        for (Vertex v : vertices) assertEquals("download", v.getAction());
    }

    @Test
    void markVertex() {
        List<Vertex> vertices = aov.getReachableVertices();
        assertEquals(4, vertices.size());
        for (Vertex v : vertices) assertEquals("download", v.getAction());

        // g-1: download passed => g-1 mapping node available
        aov.updateVertexStatus(0, VertexStatusEnum.PASSED);
        aov.updateVertexStatus(1, VertexStatusEnum.PASSED);
        vertices = aov.getReachableVertices();
        assertEquals(3, vertices.size());
        for (Vertex v : vertices) {
            if (v.getIndex() == 4) assertEquals("mapping", v.getAction());
            else assertEquals("download", v.getAction());
        }

        // g-2: download passed => g-2 mapping node available
        aov.updateVertexStatus(2, VertexStatusEnum.PASSED);
        aov.updateVertexStatus(3, VertexStatusEnum.PASSED);
        vertices = aov.getReachableVertices();
        assertEquals(2, vertices.size());
        for (Vertex v : vertices) {
            assertEquals("mapping", v.getAction());
        }

        // g-1 & g-2 mapping finished => p-1 analysis node available
        aov.updateVertexStatus(4, VertexStatusEnum.PASSED);
        aov.updateVertexStatus(5, VertexStatusEnum.PASSED);
        vertices = aov.getReachableVertices();
        assertEquals(1, vertices.size());

        for (Vertex v : vertices) {
            assertEquals("analysis", v.getAction());
        }

        // analysis blocking by mapping
        aov.updateVertexStatus(5, VertexStatusEnum.BLOCKED);
        vertices = aov.getReachableVertices();
        assertEquals(1, vertices.size());
        for (Vertex v : vertices) {
            assertEquals("mapping", v.getAction());
        }
    }

    @Test
    void getCurrentVerticesColor() {
        Map<Integer, ? extends VertexStatusEnum> colorMap = aov.getCurrentVerticesStatus();
        for (int i = 0; i < 4; i++) assertEquals(VertexStatusEnum.REACHABLE, colorMap.get(i));
        for (int i = 4; i < 7; i++) assertEquals(VertexStatusEnum.UNREACHABLE, colorMap.get(i));
    }

    Graph getDAG() throws IOException {
        List<InputFile> inputFiles;
        ScopeMapper mapper = new ScopeMapper();
        Graphs graphs = new Graphs(mapper);
        Gson gson = new Gson();
        try (FileReader r = new FileReader(testParam)) {
            JsonReader reader = new JsonReader(r);
            inputFiles = gson.fromJson(reader, new TypeToken<List<InputFile>>() {
            }.getType());
        }

        List<Vertex> vertices = new ArrayList<>();
        int index = 0;

        // upload by file
        for (String uri : mapper.map(inputFiles, "file")) {
            Vertex v = new Vertex();
            v.setScope("file");
            v.setAction("download");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // mapping by group
        for (String uri : mapper.map(inputFiles, "group")) {
            Vertex v = new Vertex();
            v.setScope("group");
            v.setAction("mapping");
            v.setIndex(index++);
            v.setDependenciesNodeName("download");
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        // analysis by project
        for (String uri : mapper.map(inputFiles, "project")) {
            Vertex v = new Vertex();
            v.setScope("project");
            v.setAction("analysis");
            v.setDependenciesNodeName("mapping");
            v.setIndex(index++);
            v.setBindDataURI(uri);
            vertices.add(v);
        }

        return graphs.vertices2DAG(vertices);
    }
}