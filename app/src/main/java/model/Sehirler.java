package model;

public class Sehirler {
    private String ad;
    private String resim;
    private String bolgeId;

    public Sehirler() {
    }

    public Sehirler(String ad, String resim, String bolgeId) {
        this.ad = ad;
        this.resim = resim;
        this.bolgeId = bolgeId;
    }

    public Sehirler(String toString, String toString1) {
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

    public String getBolgeId() {
        return bolgeId;
    }

    public void setBolgeId(String bolgeId) {
        this.bolgeId = bolgeId;
    }
}
