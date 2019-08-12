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
class AOVColoringTest {

    private String testParam = getClass().getResource("/input-params-1.json").getPath();
    private Graph graph;
    private AOVColoring aov;

    @BeforeEach
    void before() throws IOException {
        graph = getDAG();
        aov = new AOVColoring(graph);
    }

    @Test
    void getAccessibleVertices() {
        List<Vertex> vertices = aov.getPassableVertices();
        assertEquals(4, vertices.size());
        for (Vertex v : vertices) assertEquals("download", v.getAction());
    }

    @Test
    void markVertex() {
        List<Vertex> vertices = aov.getPassableVertices();
        assertEquals(4, vertices.size());
        for (Vertex v : vertices) assertEquals("download", v.getAction());

        // g-1: download passed => g-1 mapping node available
        aov.markVertex(0, AOVColorEnum.PASSED);
        aov.markVertex(1, AOVColorEnum.PASSED);
        vertices = aov.getPassableVertices();
        assertEquals(5, vertices.size());
        for (Vertex v : vertices) {
            if (v.getIndex() == 4) assertEquals("mapping", v.getAction());
            else assertEquals("download", v.getAction());
        }

        // g-2: download passed => g-2 mapping node available
        aov.markVertex(2, AOVColorEnum.PASSED);
        aov.markVertex(3, AOVColorEnum.PASSED);
        vertices = aov.getPassableVertices();
        assertEquals(6, vertices.size());
        for (Vertex v : vertices) {
            if (v.getIndex() == 4 || v.getIndex() == 5) assertEquals("mapping", v.getAction());
            else assertEquals("download", v.getAction());
        }

        // g-1 & g-2 mapping finished => p-1 analysis node available
        aov.markVertex(4, AOVColorEnum.PASSED);
        aov.markVertex(5, AOVColorEnum.PASSED);
        vertices = aov.getPassableVertices();
        assertEquals(7, vertices.size());

        for (Vertex v : vertices) {
            if (v.getIndex() == 4 || v.getIndex() == 5) assertEquals("mapping", v.getAction());
            else if (v.getIndex() == 6) assertEquals("analysis", v.getAction());
            else assertEquals("download", v.getAction());
        }
    }

    @Test
    void getCurrentVerticesColor() {
        Map<Integer, AOVColorEnum> colorMap = aov.getCurrentVerticesColor();
        for (int i = 0; i < 4; i++) assertEquals(AOVColorEnum.AVAILABLE, colorMap.get(i));
        for (int i = 4; i < 7; i++) assertEquals(AOVColorEnum.BLOCKING, colorMap.get(i));
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