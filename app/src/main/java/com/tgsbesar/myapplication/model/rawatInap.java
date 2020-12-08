package com.tgsbesar.myapplication.model;

import java.io.Serializable;
import java.util.Calendar;

public class rawatInap implements Serializable {
    private Profile Profile;
    private String kelas;
    private Calendar tanggal_masuk;
    private int no_pendaftaran;

    public rawatInap(Profile Profile, String kelas, Calendar tanggal_masuk, int no_pendaftaran){
        this.Profile = Profile;
        this.kelas = kelas;
        this.tanggal_masuk = tanggal_masuk;
        this.no_pendaftaran=no_pendaftaran;

    }

    public Profile getProfile() {
        return Profile;
    }

    public void setProfile(Profile profile) {
        this.Profile = profile;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public Calendar getTanggal_masuk() {
        return tanggal_masuk;
    }

    public void setTanggal_masuk(Calendar tanggal_masuk) {
        this.tanggal_masuk = tanggal_masuk;
    }

    public int getNo_pendaftaran() {
        return no_pendaftaran;
    }

    public void setNo_pendaftaran(int no_pendaftaran) {
        this.no_pendaftaran = no_pendaftaran;
    }
}
