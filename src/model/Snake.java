package src.model;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    int curDrct;
    List<Node> body;

    /**
     * initial direction: right
     * initial position: left top
     */
    public Snake() {
        body = new LinkedList<>();
    }

    public int getCurDrct() {
        return curDrct;
    }

    public void setCurDrct(int curDrct) {
        this.curDrct = curDrct;
    }

    public List<Node> getBody() {
        return body;
    }

    public void setBody(List<Node> body) {
        this.body = body;
    }

    public int size() {
        return body.size();
    }

    public Node getHead() {
        return body.get(0);
    }
}