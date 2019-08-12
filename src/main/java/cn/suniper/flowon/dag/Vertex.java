package cn.suniper.flowon.dag;

/**
 * 顶点表节点
 * @author Rao Mengnan
 * on 2019-08-07.
 */
public class Vertex {
    private int index;
    private String action;
    // scope字段描述了bindDataURI可以绑定数据的最大集合
    private String scope;
    private String dependenciesNodeName;
    private String bindDataURI;
    private ArcNode firstArc;

    public ArcNode getFirstArc() {
        return firstArc;
    }

    public void setFirstArc(ArcNode firstArc) {
        this.firstArc = firstArc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDependenciesNodeName() {
        return dependenciesNodeName;
    }

    public void setDependenciesNodeName(String dependenciesNodeName) {
        this.dependenciesNodeName = dependenciesNodeName;
    }

    public String getBindDataURI() {
        return bindDataURI;
    }

    public void setBindDataURI(String bindDataURI) {
        this.bindDataURI = bindDataURI;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "index=" + index +
                ", action='" + action + '\'' +
                ", scope='" + scope + '\'' +
                ", dependencies='" + dependenciesNodeName + '\'' +
                ", bindDataURI='" + bindDataURI + '\'' +
                ", firstArc=" + firstArc +
                '}';
    }
}
