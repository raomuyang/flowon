package cn.suniper.flowon;

import cn.suniper.flowon.dag.Scopes;

import java.io.File;
import java.util.*;

/**
 * This demo shows  how to map a series of input to vertex and
 * how to bind the dependencies of vertices in a graph:
 *
 *
 *           download        download        download       download
 *           (file 1)         (file 2)        (file 3)       (file 4)
 *               |                |             |              |
 *               | ---------------|             | --------------|
 *                       |                                |
 *                       mapping   -----------------   mapping
 *                       (group1)         |           (group2)
 *                                        |
 *                                      analysis
 *                                      (project x)
 * @author Rao Mengnan
 * on 2019-08-09.
 */
public class ScopeMapper implements Scopes<List<InputFile>> {

    private Map<String, NodeRef> nodeRefConf;

    public ScopeMapper() {
        File conf = new File(getClass().getResource("/flow.conf").getPath());
        this.nodeRefConf = new FlowParser().parseFile(conf);
    }

    @Override
    public List<String> map(List<InputFile> params, String scope) {
        Set<String> uriSet = new HashSet<>();
        for (InputFile param : params) {
            StringBuilder sb = new StringBuilder(scope).append(":").append(param.project);
            // scope:project_id/group/file
            switch (scope) {
                case "file":
                    sb.append("/").append(param.group).append("/").append(param.file);
                    break;
                case "group":
                    sb.append("/").append(param.group);
                    break;
                default:
                    break;
            }
            uriSet.add(sb.toString());
        }
        return new ArrayList<>(uriSet);
    }

    @Override
    public boolean isInclude(String dataURI, String subDataURI, String dependenciesNodeName) {
        if (subDataURI == null) return false;
        if (dataURI.equals(subDataURI)) return false;

        String subDataScope = subDataURI.split(":")[0];
        NodeRef nodeRef = nodeRefConf.get(dependenciesNodeName);
        if (nodeRef == null || !nodeRef.getScope().equals(subDataScope)) {
            return false;
        }

        String[] sp1 = dataURI.substring(dataURI.indexOf(":")).split("/");
        String[] sp2 = subDataURI.substring(subDataScope.length()).split("/");
        for (int i = 0; i < sp1.length; i++) {
            if (!sp1[i].equals(sp2[i])) return false;
        }

        return true;
    }

}
