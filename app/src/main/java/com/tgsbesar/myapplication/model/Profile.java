package com.tgsbesar.myapplication.model;

public class Profile {
    public String nama_lengkap;
    public String alamat;
    public String noTelp;
    public String gender;
    public int umur;

    public Profile(String nama_lengkap, String alamat, String noTelp, String gender, int umur){
        this.nama_lengkap = nama_lengkap;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.gender = gender;
        this.umur = umur;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }
}
