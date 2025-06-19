package org.dominio;

import java.math.BigDecimal;
import java.sql.Date;

public class Gasto {

    private int id;
    private int userId;
    private BigDecimal monto;
    private String categoria;
    private Date fecha;
    private String descripcion;

    // ✅ Constructor vacío
    public Gasto() {
    }

    // ✅ Constructor con todos los campos (el que te faltaba)
    public Gasto(int id, int userId, BigDecimal monto, String categoria, Date fecha, String descripcion) {
        this.id = id;
        this.userId = userId;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // ✅ Getters y Setters

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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
        return "Gasto{" +
                "id=" + id +
                ", userId=" + userId +
                ", monto=" + monto +
                ", categoria='" + categoria + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
