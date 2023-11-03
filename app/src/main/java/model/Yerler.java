package model;

public class Yerler {
    private String yerAdi;
    private String tanıtıcıMetin;
    private String Konum;
    private String izlemeLinki;
    private String resim;
    private String ilceId;

    public Yerler() {
    }

    public Yerler(String yerAdi, String tanıtıcıMetin, String konum, String izlemeLinki, String resim, String ilceId) {
        this.yerAdi = yerAdi;
        this.tanıtıcıMetin = tanıtıcıMetin;
        Konum = konum;
        this.izlemeLinki = izlemeLinki;
        this.resim = resim;
        this.ilceId = ilceId;
    }

    public String getYerAdi() {
        return yerAdi;
    }

    public void setYerAdi(String yerAdi) {
        this.yerAdi = yerAdi;
    }

    public String getTanıtıcıMetin() {
        return tanıtıcıMetin;
    }

    public void setTanıtıcıMetin(String tanıtıcıMetin) {
        this.tanıtıcıMetin = tanıtıcıMetin;
    }

    public String getKonum() {
        return Konum;
    }

    public void setKonum(String konum) {
        Konum = konum;
    }

    public String getIzlemeLinki() {
        return izlemeLinki;
    }

    public void setIzlemeLinki(String izlemeLinki) {
        this.izlemeLinki = izlemeLinki;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getIlceId() {
        return ilceId;
    }

    public void setIlceId(String ilceId) {
        this.ilceId = ilceId;
    }
}
