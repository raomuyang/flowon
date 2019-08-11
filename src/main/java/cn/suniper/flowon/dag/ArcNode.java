package cn.suniper.flowon.dag;

/**
 * 边表节点
 * @author Rao Mengnan
 * on 2019-08-07.
 */
public class ArcNode {
    private int adjVex;
    private ArcNode nextArc;

    public ArcNode(int adjVex, ArcNode nextArc) {
        this.adjVex = adjVex;
        this.nextArc = nextArc;
    }

    public int getAdjVex() {
        return adjVex;
    }

    public void setAdjVex(int adjVex) {
        this.adjVex = adjVex;
    }

    public ArcNode getNextArc() {
        return nextArc;
    }

    public void setNextArc(ArcNode nextArc) {
        this.nextArc = nextArc;
    }

    @Override
    public String toString() {
        return "ArcNode{" +
                "adjVex=" + adjVex +
                '}';
    }
}
