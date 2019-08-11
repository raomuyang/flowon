package cn.suniper.flowon;

import com.typesafe.config.Optional;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public class NodeRef {
    @Optional
    private String name;
    @Optional
    private String dependencies;
    @Optional
    private String scope;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "NodeRef{" +
                "name='" + name + '\'' +
                ", dependencies='" + dependencies + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
