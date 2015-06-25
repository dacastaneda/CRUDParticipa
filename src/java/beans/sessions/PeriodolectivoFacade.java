/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.sessions;

import entidades.Periodolectivo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dacastanedah
 */
@Stateless
public class PeriodolectivoFacade extends AbstractFacade<Periodolectivo> {
    @PersistenceContext(unitName = "CRUDParticipaJSFPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeriodolectivoFacade() {
        super(Periodolectivo.class);
    }
    
}
