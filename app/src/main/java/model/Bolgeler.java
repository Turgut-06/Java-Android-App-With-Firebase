package model;

public class Bolgeler {
    private String ad;
    private String resim;

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

    public Bolgeler(String ad, String resim) {
        this.ad = ad;
        this.resim = resim;
    }

    public Bolgeler() {
    }
}
