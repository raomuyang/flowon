package cn.suniper.flowon;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public enum VertexStatusEnum {

    PASSED("passed"),
    REACHABLE("reachable"),
    UNREACHABLE("unreachable"),
    BLOCKED("blocked");

    private String status;
    VertexStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
