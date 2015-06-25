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
@Table(name = "menunivel1")
@NamedQueries({
    @NamedQuery(name = "Menunivel1.findAll", query = "SELECT m FROM Menunivel1 m"),
    @NamedQuery(name = "Menunivel1.findByIdMenuNivel1", query = "SELECT m FROM Menunivel1 m WHERE m.idMenuNivel1 = :idMenuNivel1"),
    @NamedQuery(name = "Menunivel1.findByNombreMenuNivel1", query = "SELECT m FROM Menunivel1 m WHERE m.nombreMenuNivel1 = :nombreMenuNivel1")})
public class Menunivel1 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idMenuNivel1")
    private Integer idMenuNivel1;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombreMenuNivel1")
    private String nombreMenuNivel1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMenuNivel1")
    private Collection<Menunivel2> menunivel2Collection;

    public Menunivel1() {
    }

    public Menunivel1(Integer idMenuNivel1) {
        this.idMenuNivel1 = idMenuNivel1;
    }

    public Menunivel1(Integer idMenuNivel1, String nombreMenuNivel1) {
        this.idMenuNivel1 = idMenuNivel1;
        this.nombreMenuNivel1 = nombreMenuNivel1;
    }

    public Integer getIdMenuNivel1() {
        return idMenuNivel1;
    }

    public void setIdMenuNivel1(Integer idMenuNivel1) {
        this.idMenuNivel1 = idMenuNivel1;
    }

    public String getNombreMenuNivel1() {
        return nombreMenuNivel1;
    }

    public void setNombreMenuNivel1(String nombreMenuNivel1) {
        this.nombreMenuNivel1 = nombreMenuNivel1;
    }

    public Collection<Menunivel2> getMenunivel2Collection() {
        return menunivel2Collection;
    }

    public void setMenunivel2Collection(Collection<Menunivel2> menunivel2Collection) {
        this.menunivel2Collection = menunivel2Collection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenuNivel1 != null ? idMenuNivel1.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menunivel1)) {
            return false;
        }
        Menunivel1 other = (Menunivel1) object;
        if ((this.idMenuNivel1 == null && other.idMenuNivel1 != null) || (this.idMenuNivel1 != null && !this.idMenuNivel1.equals(other.idMenuNivel1))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Menunivel1[ idMenuNivel1=" + idMenuNivel1 + " ]";
    }
    
}
