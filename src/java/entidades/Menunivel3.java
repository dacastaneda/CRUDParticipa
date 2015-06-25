/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dacastanedah
 */
@Entity
@Table(name = "menunivel3")
@NamedQueries({
    @NamedQuery(name = "Menunivel3.findAll", query = "SELECT m FROM Menunivel3 m"),
    @NamedQuery(name = "Menunivel3.findByIdMenuNivel3", query = "SELECT m FROM Menunivel3 m WHERE m.idMenuNivel3 = :idMenuNivel3"),
    @NamedQuery(name = "Menunivel3.findByNombreMenuNivel3", query = "SELECT m FROM Menunivel3 m WHERE m.nombreMenuNivel3 = :nombreMenuNivel3")})
public class Menunivel3 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMenuNivel3")
    private Integer idMenuNivel3;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombreMenuNivel3")
    private String nombreMenuNivel3;
    @JoinColumn(name = "idMenuNivel2", referencedColumnName = "idMenuNivel2")
    @ManyToOne(optional = false)
    private Menunivel2 idMenuNivel2;
    @JoinColumn(name = "idPerfil", referencedColumnName = "idPerfil")
    @ManyToOne(optional = false)
    private Perfil idPerfil;

    public Menunivel3() {
    }

    public Menunivel3(Integer idMenuNivel3) {
        this.idMenuNivel3 = idMenuNivel3;
    }

    public Menunivel3(Integer idMenuNivel3, String nombreMenuNivel3) {
        this.idMenuNivel3 = idMenuNivel3;
        this.nombreMenuNivel3 = nombreMenuNivel3;
    }

    public Integer getIdMenuNivel3() {
        return idMenuNivel3;
    }

    public void setIdMenuNivel3(Integer idMenuNivel3) {
        this.idMenuNivel3 = idMenuNivel3;
    }

    public String getNombreMenuNivel3() {
        return nombreMenuNivel3;
    }

    public void setNombreMenuNivel3(String nombreMenuNivel3) {
        this.nombreMenuNivel3 = nombreMenuNivel3;
    }

    public Menunivel2 getIdMenuNivel2() {
        return idMenuNivel2;
    }

    public void setIdMenuNivel2(Menunivel2 idMenuNivel2) {
        this.idMenuNivel2 = idMenuNivel2;
    }

    public Perfil getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Perfil idPerfil) {
        this.idPerfil = idPerfil;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenuNivel3 != null ? idMenuNivel3.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Menunivel3)) {
            return false;
        }
        Menunivel3 other = (Menunivel3) object;
        if ((this.idMenuNivel3 == null && other.idMenuNivel3 != null) || (this.idMenuNivel3 != null && !this.idMenuNivel3.equals(other.idMenuNivel3))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Menunivel3[ idMenuNivel3=" + idMenuNivel3 + " ]";
    }
    
}
