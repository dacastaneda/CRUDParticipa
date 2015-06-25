/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dacastanedah
 */
@Entity
@Table(name = "actividadacalificar")
@NamedQueries({
    @NamedQuery(name = "Actividadacalificar.findAll", query = "SELECT a FROM Actividadacalificar a"),
    @NamedQuery(name = "Actividadacalificar.findByIdActividadCalificada", query = "SELECT a FROM Actividadacalificar a WHERE a.idActividadCalificada = :idActividadCalificada"),
    @NamedQuery(name = "Actividadacalificar.findByTipo", query = "SELECT a FROM Actividadacalificar a WHERE a.tipo = :tipo"),
    @NamedQuery(name = "Actividadacalificar.findByFechaInicial", query = "SELECT a FROM Actividadacalificar a WHERE a.fechaInicial = :fechaInicial"),
    @NamedQuery(name = "Actividadacalificar.findByFechaFinal", query = "SELECT a FROM Actividadacalificar a WHERE a.fechaFinal = :fechaFinal")})
public class Actividadacalificar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "idActividadCalificada")
    private String idActividadCalificada;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "Tipo")
    private String tipo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaInicial")
    @Temporal(TemporalType.DATE)
    private Date fechaInicial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaFinal")
    @Temporal(TemporalType.DATE)
    private Date fechaFinal;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "actividadacalificar")
    private Notas notas;
    @JoinColumn(name = "idLogro", referencedColumnName = "idLogro")
    @ManyToOne(optional = false)
    private Logro idLogro;
    @JoinColumn(name = "idperiodoCalificable", referencedColumnName = "idperiodoCalificable")
    @ManyToOne(optional = false)
    private Periodocalificable idperiodoCalificable;

    public Actividadacalificar() {
    }

    public Actividadacalificar(String idActividadCalificada) {
        this.idActividadCalificada = idActividadCalificada;
    }

    public Actividadacalificar(String idActividadCalificada, String tipo, Date fechaInicial, Date fechaFinal) {
        this.idActividadCalificada = idActividadCalificada;
        this.tipo = tipo;
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
    }

    public String getIdActividadCalificada() {
        return idActividadCalificada;
    }

    public void setIdActividadCalificada(String idActividadCalificada) {
        this.idActividadCalificada = idActividadCalificada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Notas getNotas() {
        return notas;
    }

    public void setNotas(Notas notas) {
        this.notas = notas;
    }

    public Logro getIdLogro() {
        return idLogro;
    }

    public void setIdLogro(Logro idLogro) {
        this.idLogro = idLogro;
    }

    public Periodocalificable getIdperiodoCalificable() {
        return idperiodoCalificable;
    }

    public void setIdperiodoCalificable(Periodocalificable idperiodoCalificable) {
        this.idperiodoCalificable = idperiodoCalificable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idActividadCalificada != null ? idActividadCalificada.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Actividadacalificar)) {
            return false;
        }
        Actividadacalificar other = (Actividadacalificar) object;
        if ((this.idActividadCalificada == null && other.idActividadCalificada != null) || (this.idActividadCalificada != null && !this.idActividadCalificada.equals(other.idActividadCalificada))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Actividadacalificar[ idActividadCalificada=" + idActividadCalificada + " ]";
    }
    
}
