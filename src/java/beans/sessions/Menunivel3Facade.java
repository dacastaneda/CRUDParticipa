/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.sessions;

import entidades.Menunivel3;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dacastanedah
 */
@Stateless
public class Menunivel3Facade extends AbstractFacade<Menunivel3> {
    @PersistenceContext(unitName = "CRUDParticipaJSFPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Menunivel3Facade() {
        super(Menunivel3.class);
    }
    
}
