package model;

public class Ilceler {
    private String ad;
    private String resim;
    private String sehirId;

    public Ilceler() {
    }

    public Ilceler(String ad, String resim, String sehirId) {
        this.ad = ad;
        this.resim = resim;
        this.sehirId = sehirId;
    }

    public Ilceler(String toString, String toString1) {
    }


    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getSehirId() {
        return sehirId;
    }

    public void setSehirId(String sehirId) {
        this.sehirId = sehirId;
    }
}
