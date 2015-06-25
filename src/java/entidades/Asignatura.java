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
@Table(name = "asignatura")
@NamedQueries({
    @NamedQuery(name = "Asignatura.findAll", query = "SELECT a FROM Asignatura a"),
    @NamedQuery(name = "Asignatura.findByIdAsignatura", query = "SELECT a FROM Asignatura a WHERE a.idAsignatura = :idAsignatura"),
    @NamedQuery(name = "Asignatura.findByNombreAsignatura", query = "SELECT a FROM Asignatura a WHERE a.nombreAsignatura = :nombreAsignatura")})
public class Asignatura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "idAsignatura")
    private String idAsignatura;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombreAsignatura")
    private String nombreAsignatura;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asignatura")
    private Collection<Asignaturagrado> asignaturagradoCollection;

    public Asignatura() {
    }

    public Asignatura(String idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public Asignatura(String idAsignatura, String nombreAsignatura) {
        this.idAsignatura = idAsignatura;
        this.nombreAsignatura = nombreAsignatura;
    }

    public String getIdAsignatura() {
        return idAsignatura;
    }

    public void setIdAsignatura(String idAsignatura) {
        this.idAsignatura = idAsignatura;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }

    public Collection<Asignaturagrado> getAsignaturagradoCollection() {
        return asignaturagradoCollection;
    }

    public void setAsignaturagradoCollection(Collection<Asignaturagrado> asignaturagradoCollection) {
        this.asignaturagradoCollection = asignaturagradoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAsignatura != null ? idAsignatura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asignatura)) {
            return false;
        }
        Asignatura other = (Asignatura) object;
        if ((this.idAsignatura == null && other.idAsignatura != null) || (this.idAsignatura != null && !this.idAsignatura.equals(other.idAsignatura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Asignatura[ idAsignatura=" + idAsignatura + " ]";
    }
    
}
