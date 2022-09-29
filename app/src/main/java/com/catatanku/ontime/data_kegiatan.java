package com.catatanku.ontime;

public class data_kegiatan {
    private String titledoes;
    private String datedoes;
    private String descdoes;
    private String infodoes;
    private String placedoes;
    private String barangdoes;
    private String UID;
    private String key;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getBarangdoes() {
        return barangdoes;
    }

    public void setBarangdoes(String barangdoes) {
        this.barangdoes = barangdoes;
    }

    public String getPlacedoes() {
        return placedoes;
    }

    public void setPlacedoes(String placedoes) {
        this.placedoes = placedoes;
    }

    public String getInfodoes() {
        return infodoes;
    }

    public void setInfodoes(String infodoes) {
        this.infodoes = infodoes;
    }

    public String getTitledoes() {
        return titledoes;
    }

    public void setTitledoes(String titledoes) {
        this.titledoes = titledoes;
    }

    public String getDatedoes() {
        return datedoes;
    }

    public void setDatedoes(String datedoes) {
        this.datedoes = datedoes;
    }

    public String getDescdoes() {
        return descdoes;
    }

    public void setDescdoes(String descdoes) {
        this.descdoes = descdoes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public data_kegiatan() {
    }


    public data_kegiatan(String titledoes, String datedoes, String descdoes, String infodoes, String placedoes, String barangdoes, String UID) {
        this.titledoes = titledoes;
        this.datedoes = datedoes;
        this.descdoes = descdoes;
        this.infodoes = infodoes;
        this.placedoes = placedoes;
        this.barangdoes = barangdoes;
        this.UID = UID;


    }
}


