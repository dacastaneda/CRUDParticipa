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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author dacastanedah
 */
@Entity
@Table(name = "notas")
@NamedQueries({
    @NamedQuery(name = "Notas.findAll", query = "SELECT n FROM Notas n"),
    @NamedQuery(name = "Notas.findByIdActividadCalificada", query = "SELECT n FROM Notas n WHERE n.idActividadCalificada = :idActividadCalificada"),
    @NamedQuery(name = "Notas.findByIdEstudiante", query = "SELECT n FROM Notas n WHERE n.idEstudiante = :idEstudiante"),
    @NamedQuery(name = "Notas.findByNota", query = "SELECT n FROM Notas n WHERE n.nota = :nota")})
public class Notas implements Serializable {
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
    @Column(name = "idEstudiante")
    private String idEstudiante;
    @Basic(optional = false)
    @NotNull
    @Column(name = "nota")
    private int nota;
    @JoinColumn(name = "idActividadCalificada", referencedColumnName = "idActividadCalificada", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Actividadacalificar actividadacalificar;

    public Notas() {
    }

    public Notas(String idActividadCalificada) {
        this.idActividadCalificada = idActividadCalificada;
    }

    public Notas(String idActividadCalificada, String idEstudiante, int nota) {
        this.idActividadCalificada = idActividadCalificada;
        this.idEstudiante = idEstudiante;
        this.nota = nota;
    }

    public String getIdActividadCalificada() {
        return idActividadCalificada;
    }

    public void setIdActividadCalificada(String idActividadCalificada) {
        this.idActividadCalificada = idActividadCalificada;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public Actividadacalificar getActividadacalificar() {
        return actividadacalificar;
    }

    public void setActividadacalificar(Actividadacalificar actividadacalificar) {
        this.actividadacalificar = actividadacalificar;
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
        if (!(object instanceof Notas)) {
            return false;
        }
        Notas other = (Notas) object;
        if ((this.idActividadCalificada == null && other.idActividadCalificada != null) || (this.idActividadCalificada != null && !this.idActividadCalificada.equals(other.idActividadCalificada))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Notas[ idActividadCalificada=" + idActividadCalificada + " ]";
    }
    
}
