package com.example.projectchat;

public class outMessage {
    private String tmp_to = "tmpto";
    private String tmp_body = "tmpbody";
    private String tmp_type = "tmptype";
    private String idstanzatmp = "idstanzatmp";
    private String idtmpbody = "idtmpbody";

    public outMessage(String tmp_to, String tmp_body, String idtmpbody, String tmp_type, String idstanzatmp) {
        this.tmp_to = tmp_to;
        this.tmp_body = tmp_body;
        this.tmp_type = tmp_type;
        this.idstanzatmp = idstanzatmp;
        this.idtmpbody = idtmpbody;
    }

    public String getto() {
        return this.tmp_to;
    }

    public String getTmp_body() {
        return this.tmp_body;
    }

    public String getTmp_type() {
        return this.tmp_type;
    }

    public String getIdstanzatmp() {
        return this.idstanzatmp;
    }

    public String getIdtmpbody() {
        return this.idtmpbody;
    }

}

