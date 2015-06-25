/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.sessions;

import entidades.Asignaturagrado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dacastanedah
 */
@Stateless
public class AsignaturagradoFacade extends AbstractFacade<Asignaturagrado> {
    @PersistenceContext(unitName = "CRUDParticipaJSFPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AsignaturagradoFacade() {
        super(Asignaturagrado.class);
    }
    
}
