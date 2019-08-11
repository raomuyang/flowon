package cn.suniper.flowon;

/**
 * @author Rao Mengnan
 * on 2019-08-08.
 */
public enum AOVColorEnum {

    PASSED("green"),
    AVAILABLE("yellow"),
    BLOCKING("white"),
    BLOCKED("red");

    private String color;
    AOVColorEnum(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
