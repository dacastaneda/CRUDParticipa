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
@Table(name = "cursoestudiante")
@NamedQueries({
    @NamedQuery(name = "Cursoestudiante.findAll", query = "SELECT c FROM Cursoestudiante c"),
    @NamedQuery(name = "Cursoestudiante.findByIdGradoUsuario", query = "SELECT c FROM Cursoestudiante c WHERE c.idGradoUsuario = :idGradoUsuario")})
public class Cursoestudiante implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "idGradoUsuario")
    private String idGradoUsuario;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idEstudiante")
    @ManyToOne(optional = false)
    private Estudiante idUsuario;
    @JoinColumn(name = "idgrado", referencedColumnName = "idGrado")
    @ManyToOne(optional = false)
    private Grado idgrado;

    public Cursoestudiante() {
    }

    public Cursoestudiante(String idGradoUsuario) {
        this.idGradoUsuario = idGradoUsuario;
    }

    public String getIdGradoUsuario() {
        return idGradoUsuario;
    }

    public void setIdGradoUsuario(String idGradoUsuario) {
        this.idGradoUsuario = idGradoUsuario;
    }

    public Estudiante getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Estudiante idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Grado getIdgrado() {
        return idgrado;
    }

    public void setIdgrado(Grado idgrado) {
        this.idgrado = idgrado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGradoUsuario != null ? idGradoUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cursoestudiante)) {
            return false;
        }
        Cursoestudiante other = (Cursoestudiante) object;
        if ((this.idGradoUsuario == null && other.idGradoUsuario != null) || (this.idGradoUsuario != null && !this.idGradoUsuario.equals(other.idGradoUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Cursoestudiante[ idGradoUsuario=" + idGradoUsuario + " ]";
    }
    
}
