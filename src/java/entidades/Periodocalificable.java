/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dacastanedah
 */
@Entity
@Table(name = "periodocalificable")
@NamedQueries({
    @NamedQuery(name = "Periodocalificable.findAll", query = "SELECT p FROM Periodocalificable p"),
    @NamedQuery(name = "Periodocalificable.findByIdperiodoCalificable", query = "SELECT p FROM Periodocalificable p WHERE p.idperiodoCalificable = :idperiodoCalificable"),
    @NamedQuery(name = "Periodocalificable.findByNombrePeriodoCalificable", query = "SELECT p FROM Periodocalificable p WHERE p.nombrePeriodoCalificable = :nombrePeriodoCalificable")})
public class Periodocalificable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4)
    @Column(name = "idperiodoCalificable")
    private String idperiodoCalificable;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombrePeriodoCalificable")
    private String nombrePeriodoCalificable;
    @JoinColumn(name = "idPeriodoLectivo", referencedColumnName = "idPeriodoLectivo")
    @ManyToOne(optional = false)
    private Periodolectivo idPeriodoLectivo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idperiodoCalificable")
    private Collection<Actividadacalificar> actividadacalificarCollection;

    public Periodocalificable() {
    }

    public Periodocalificable(String idperiodoCalificable) {
        this.idperiodoCalificable = idperiodoCalificable;
    }

    public Periodocalificable(String idperiodoCalificable, String nombrePeriodoCalificable) {
        this.idperiodoCalificable = idperiodoCalificable;
        this.nombrePeriodoCalificable = nombrePeriodoCalificable;
    }

    public String getIdperiodoCalificable() {
        return idperiodoCalificable;
    }

    public void setIdperiodoCalificable(String idperiodoCalificable) {
        this.idperiodoCalificable = idperiodoCalificable;
    }

    public String getNombrePeriodoCalificable() {
        return nombrePeriodoCalificable;
    }

    public void setNombrePeriodoCalificable(String nombrePeriodoCalificable) {
        this.nombrePeriodoCalificable = nombrePeriodoCalificable;
    }

    public Periodolectivo getIdPeriodoLectivo() {
        return idPeriodoLectivo;
    }

    public void setIdPeriodoLectivo(Periodolectivo idPeriodoLectivo) {
        this.idPeriodoLectivo = idPeriodoLectivo;
    }

    public Collection<Actividadacalificar> getActividadacalificarCollection() {
        return actividadacalificarCollection;
    }

    public void setActividadacalificarCollection(Collection<Actividadacalificar> actividadacalificarCollection) {
        this.actividadacalificarCollection = actividadacalificarCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idperiodoCalificable != null ? idperiodoCalificable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Periodocalificable)) {
            return false;
        }
        Periodocalificable other = (Periodocalificable) object;
        if ((this.idperiodoCalificable == null && other.idperiodoCalificable != null) || (this.idperiodoCalificable != null && !this.idperiodoCalificable.equals(other.idperiodoCalificable))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Periodocalificable[ idperiodoCalificable=" + idperiodoCalificable + " ]";
    }
    
}
