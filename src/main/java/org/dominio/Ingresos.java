package org.dominio;

import java.math.BigDecimal;
import java.sql.Date;

public class Ingresos {
    private int id;
    private int userId;
    private BigDecimal monto;
    private String fuente;
    private Date fecha;
    private String descripcion;

    public Ingresos() {
    }

    public Ingresos(int id, int userId, BigDecimal monto, String fuente, Date fecha, String descripcion) {
        this.id = id;
        this.userId = userId;
        this.monto = monto;
        this.fuente = fuente;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Ingresos{" +
                "id=" + id +
                ", userId=" + userId +
                ", monto=" + monto +
                ", fuente='" + fuente + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
