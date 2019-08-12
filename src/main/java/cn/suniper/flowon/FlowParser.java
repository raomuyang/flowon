package cn.suniper.flowon;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public class FlowParser {

    private ConfigParseOptions options = ConfigParseOptions.defaults();

    public FlowParser() {
    }

    public FlowParser(ConfigParseOptions options) {
        this.options = options;
    }

    public Map<String, NodeRef> parseFile(File conf) {
        Config config = ConfigFactory.parseFile(conf, options);
        return getNodeRefs(config);
    }
    public Map<String, NodeRef> parseString(String conf) {
        Config config = ConfigFactory.parseString(conf, options);
        return getNodeRefs(config);
    }

    private static Map<String, NodeRef> getNodeRefs(Config config) {
        Map<String, NodeRef> nodeRefConf = new HashMap<>();
        for (Map.Entry<String, ConfigValue> e: config.root().entrySet()) {
            NodeRef r = ConfigBeanFactory.create(config.getConfig(e.getKey()), NodeRef.class);
            r.setName(e.getKey());
            nodeRefConf.put(e.getKey(), r);
        }
        return nodeRefConf;
    }
}
