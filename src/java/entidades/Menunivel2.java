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
@Table(name = "menunivel2")
@NamedQueries({
    @NamedQuery(name = "Menunivel2.findAll", query = "SELECT m FROM Menunivel2 m"),
    @NamedQuery(name = "Menunivel2.findByIdMenuNivel2", query = "SELECT m FROM Menunivel2 m WHERE m.idMenuNivel2 = :idMenuNivel2"),
    @NamedQuery(name = "Menunivel2.findByNombreMenuNivel2", query = "SELECT m FROM Menunivel2 m WHERE m.nombreMenuNivel2 = :nombreMenuNivel2")})
public class Menunivel2 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idMenuNivel2")
    private Integer idMenuNivel2;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombreMenuNivel2")
    private String nombreMenuNivel2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMenuNivel2")
    private Collection<Menunivel3> menunivel3Collection;
    @JoinColumn(name = "idMenuNivel1", referencedColumnName = "idMenuNivel1")
    @ManyToOne(optional = false)
    private Menunivel1 idMenuNivel1;

    public Menunivel2() {
    }

    public Menunivel2(Integer idMenuNivel2) {
        this.idMenuNivel2 = idMenuNivel2;
    }

    public Menunivel2(Integer idMenuNivel2, String nombreMenuNivel2) {
        this.idMenuNivel2 = idMenuNivel2;
        this.nombreMenuNivel2 = nombreMenuNivel2;
    }

    public Integer getIdMenuNivel2() {
        return idMenuNivel2;
    }

    public void setIdMenuNivel2(Integer idMenuNivel2) {
        this.idMenuNivel2 = idMenuNivel2;
    }

    public String getNombreMenuNivel2() {
        return nombreMenuNivel2;
    }

    public void setNombreMenuNivel2(String nombreMenuNivel2) {
        this.nombreMenuNivel2 = nombreMenuNivel2;
    }

    public Collection<Menunivel3> getMenunivel3Collection() {
        return menunivel3Collection;
    }

    public void setMenunivel3Collection(Collection<Menunivel3> menunivel3Collection) {
        this.menunivel3Collection = menunivel3Collection;
    }

    public Menunivel1 getIdMenuNivel1() {
        return idMenuNivel1;
    }

    public void setIdMenuNivel1(Menunivel1 idMenuNivel1) {
        this.idMenuNivel1 = idMenuNivel1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenuNivel2 != null ? idMenuNivel2.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menunivel2)) {
            return false;
        }
        Menunivel2 other = (Menunivel2) object;
        if ((this.idMenuNivel2 == null && other.idMenuNivel2 != null) || (this.idMenuNivel2 != null && !this.idMenuNivel2.equals(other.idMenuNivel2))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Menunivel2[ idMenuNivel2=" + idMenuNivel2 + " ]";
    }
    
}
