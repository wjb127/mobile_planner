package org.techtown.planner01;

public class Note {
    int _id;
    int diff;
    int impo;
    String contents;
    String createDateStr;


    public Note(int _id, int diff, int impo,String contents, String createDateStr) {
        this._id = _id;
        this.diff = diff;
        this.impo = impo;
        this.contents = contents;
        this.createDateStr = createDateStr;

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }



    public int getDiff() { return diff; }

    public void setDiff(int diff) { this.diff = diff; }

    public int getImpo() { return impo; }

    public void setImpo(int impo) { this.impo = impo; }



    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }
}