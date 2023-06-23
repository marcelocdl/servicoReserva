package br.com.poli.so.model;

public class Reserva {

    private int numPoltrona;
    private boolean reservado;
    private String nomePessoa;
    private String dataReserva;

    public Reserva(int numPoltrona, boolean reservado, String nomePessoa, String dataReserva) {
        this.numPoltrona = numPoltrona;
        this.reservado = reservado;
        this.nomePessoa = nomePessoa;
        this.dataReserva = dataReserva;
    }

    public int getNumPoltrona() {
        return numPoltrona;
    }

    public void setNumPoltrona(int numPoltrona) {
        this.numPoltrona = numPoltrona;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }
}
