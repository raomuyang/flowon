package cn.suniper.flowon.dag;

/**
 * @author Rao Mengnan
 * on 2019-08-09.
 */
public class InputFile {
    String file;
    String group;
    String project;

    public void setFile(String file) {
        this.file = file;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getFile() {
        return file;
    }

    public String getGroup() {
        return group;
    }

    public String getProject() {
        return project;
    }

    @Override
    public String toString() {
        return "InputFile{" +
                "file='" + file + '\'' +
                ", group='" + group + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}