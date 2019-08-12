package cn.suniper.flowon.dag;

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

        String scope = dataURI.split(":")[0];
        String subset;
        switch (scope) {
            case "project":
                subset = "group";
                break;
            case "group":
                subset = "file";
                break;
            default:
                subset = null;
        }
        if (subset == null || !subDataURI.startsWith(subset)) return false;


        String[] sp1 = dataURI.substring(scope.length()).split("/");
        String[] sp2 = subDataURI.substring(subset.length()).split("/");
        for (int i = 0; i < sp1.length; i++) {
            if (!sp1[i].equals(sp2[i])) return false;
        }

        return true;
    }

}
