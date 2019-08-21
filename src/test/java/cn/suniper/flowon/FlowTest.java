package cn.suniper.flowon;

import cn.suniper.flowon.dag.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rao Mengnan
 * on 2019-08-11.
 */
class FlowTest {

    private List<InputFile> inputParameters;
    private File flowConfigFile;

    @BeforeEach
    void before() throws IOException {
        String testParam = getClass().getResource("/input-params-1.json").getPath();
        flowConfigFile = new File(getClass().getResource("/flow.conf").getPath());
        Gson gson = new Gson();
        try (FileReader r = new FileReader(testParam)) {
            JsonReader reader = new JsonReader(r);
            inputParameters = gson.fromJson(reader, new TypeToken<List<InputFile>>() {
            }.getType());
        }
    }

    @Test
    void getDAG() {
        Scopes<List<InputFile>> scopes = new ScopeMapper();
        Flow<List<InputFile>> flow = new Flow<>(scopes);
        Graph graph = flow.getDAG(flowConfigFile, inputParameters);
        AOVNet aov = new AOVNet(graph);
        List<Vertex> passable = aov.getReachableVertices();
        assertEquals(4, passable.size());
        passable.forEach(v -> assertEquals("download", v.getAction()));
    }
}