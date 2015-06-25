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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dacastanedah
 */
@Entity
@Table(name = "docente")
@NamedQueries({
    @NamedQuery(name = "Docente.findAll", query = "SELECT d FROM Docente d"),
    @NamedQuery(name = "Docente.findByIdDocente", query = "SELECT d FROM Docente d WHERE d.idDocente = :idDocente"),
    @NamedQuery(name = "Docente.findByTituloProfesional", query = "SELECT d FROM Docente d WHERE d.tituloProfesional = :tituloProfesional")})
public class Docente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idDocente")
    private Integer idDocente;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "tituloProfesional")
    private String tituloProfesional;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDocente")
    private Collection<Asignaturagrado> asignaturagradoCollection;
    @JoinColumn(name = "idDocente", referencedColumnName = "idPersona", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public Docente() {
    }

    public Docente(Integer idDocente) {
        this.idDocente = idDocente;
    }

    public Docente(Integer idDocente, String tituloProfesional) {
        this.idDocente = idDocente;
        this.tituloProfesional = tituloProfesional;
    }

    public Integer getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Integer idDocente) {
        this.idDocente = idDocente;
    }

    public String getTituloProfesional() {
        return tituloProfesional;
    }

    public void setTituloProfesional(String tituloProfesional) {
        this.tituloProfesional = tituloProfesional;
    }

    public Collection<Asignaturagrado> getAsignaturagradoCollection() {
        return asignaturagradoCollection;
    }

    public void setAsignaturagradoCollection(Collection<Asignaturagrado> asignaturagradoCollection) {
        this.asignaturagradoCollection = asignaturagradoCollection;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDocente != null ? idDocente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Docente)) {
            return false;
        }
        Docente other = (Docente) object;
        if ((this.idDocente == null && other.idDocente != null) || (this.idDocente != null && !this.idDocente.equals(other.idDocente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Docente[ idDocente=" + idDocente + " ]";
    }
    
}
