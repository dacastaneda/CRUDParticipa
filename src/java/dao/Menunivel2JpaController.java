/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Menunivel1;
import entidades.Menunivel2;
import entidades.Menunivel3;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class Menunivel2JpaController implements Serializable {

    public Menunivel2JpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Menunivel2 menunivel2) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (menunivel2.getMenunivel3Collection() == null) {
            menunivel2.setMenunivel3Collection(new ArrayList<Menunivel3>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel1 idMenuNivel1 = menunivel2.getIdMenuNivel1();
            if (idMenuNivel1 != null) {
                idMenuNivel1 = em.getReference(idMenuNivel1.getClass(), idMenuNivel1.getIdMenuNivel1());
                menunivel2.setIdMenuNivel1(idMenuNivel1);
            }
            Collection<Menunivel3> attachedMenunivel3Collection = new ArrayList<Menunivel3>();
            for (Menunivel3 menunivel3CollectionMenunivel3ToAttach : menunivel2.getMenunivel3Collection()) {
                menunivel3CollectionMenunivel3ToAttach = em.getReference(menunivel3CollectionMenunivel3ToAttach.getClass(), menunivel3CollectionMenunivel3ToAttach.getIdMenuNivel3());
                attachedMenunivel3Collection.add(menunivel3CollectionMenunivel3ToAttach);
            }
            menunivel2.setMenunivel3Collection(attachedMenunivel3Collection);
            em.persist(menunivel2);
            if (idMenuNivel1 != null) {
                idMenuNivel1.getMenunivel2Collection().add(menunivel2);
                idMenuNivel1 = em.merge(idMenuNivel1);
            }
            for (Menunivel3 menunivel3CollectionMenunivel3 : menunivel2.getMenunivel3Collection()) {
                Menunivel2 oldIdMenuNivel2OfMenunivel3CollectionMenunivel3 = menunivel3CollectionMenunivel3.getIdMenuNivel2();
                menunivel3CollectionMenunivel3.setIdMenuNivel2(menunivel2);
                menunivel3CollectionMenunivel3 = em.merge(menunivel3CollectionMenunivel3);
                if (oldIdMenuNivel2OfMenunivel3CollectionMenunivel3 != null) {
                    oldIdMenuNivel2OfMenunivel3CollectionMenunivel3.getMenunivel3Collection().remove(menunivel3CollectionMenunivel3);
                    oldIdMenuNivel2OfMenunivel3CollectionMenunivel3 = em.merge(oldIdMenuNivel2OfMenunivel3CollectionMenunivel3);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMenunivel2(menunivel2.getIdMenuNivel2()) != null) {
                throw new PreexistingEntityException("Menunivel2 " + menunivel2 + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Menunivel2 menunivel2) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel2 persistentMenunivel2 = em.find(Menunivel2.class, menunivel2.getIdMenuNivel2());
            Menunivel1 idMenuNivel1Old = persistentMenunivel2.getIdMenuNivel1();
            Menunivel1 idMenuNivel1New = menunivel2.getIdMenuNivel1();
            Collection<Menunivel3> menunivel3CollectionOld = persistentMenunivel2.getMenunivel3Collection();
            Collection<Menunivel3> menunivel3CollectionNew = menunivel2.getMenunivel3Collection();
            List<String> illegalOrphanMessages = null;
            for (Menunivel3 menunivel3CollectionOldMenunivel3 : menunivel3CollectionOld) {
                if (!menunivel3CollectionNew.contains(menunivel3CollectionOldMenunivel3)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Menunivel3 " + menunivel3CollectionOldMenunivel3 + " since its idMenuNivel2 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idMenuNivel1New != null) {
                idMenuNivel1New = em.getReference(idMenuNivel1New.getClass(), idMenuNivel1New.getIdMenuNivel1());
                menunivel2.setIdMenuNivel1(idMenuNivel1New);
            }
            Collection<Menunivel3> attachedMenunivel3CollectionNew = new ArrayList<Menunivel3>();
            for (Menunivel3 menunivel3CollectionNewMenunivel3ToAttach : menunivel3CollectionNew) {
                menunivel3CollectionNewMenunivel3ToAttach = em.getReference(menunivel3CollectionNewMenunivel3ToAttach.getClass(), menunivel3CollectionNewMenunivel3ToAttach.getIdMenuNivel3());
                attachedMenunivel3CollectionNew.add(menunivel3CollectionNewMenunivel3ToAttach);
            }
            menunivel3CollectionNew = attachedMenunivel3CollectionNew;
            menunivel2.setMenunivel3Collection(menunivel3CollectionNew);
            menunivel2 = em.merge(menunivel2);
            if (idMenuNivel1Old != null && !idMenuNivel1Old.equals(idMenuNivel1New)) {
                idMenuNivel1Old.getMenunivel2Collection().remove(menunivel2);
                idMenuNivel1Old = em.merge(idMenuNivel1Old);
            }
            if (idMenuNivel1New != null && !idMenuNivel1New.equals(idMenuNivel1Old)) {
                idMenuNivel1New.getMenunivel2Collection().add(menunivel2);
                idMenuNivel1New = em.merge(idMenuNivel1New);
            }
            for (Menunivel3 menunivel3CollectionNewMenunivel3 : menunivel3CollectionNew) {
                if (!menunivel3CollectionOld.contains(menunivel3CollectionNewMenunivel3)) {
                    Menunivel2 oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3 = menunivel3CollectionNewMenunivel3.getIdMenuNivel2();
                    menunivel3CollectionNewMenunivel3.setIdMenuNivel2(menunivel2);
                    menunivel3CollectionNewMenunivel3 = em.merge(menunivel3CollectionNewMenunivel3);
                    if (oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3 != null && !oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3.equals(menunivel2)) {
                        oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3.getMenunivel3Collection().remove(menunivel3CollectionNewMenunivel3);
                        oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3 = em.merge(oldIdMenuNivel2OfMenunivel3CollectionNewMenunivel3);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = menunivel2.getIdMenuNivel2();
                if (findMenunivel2(id) == null) {
                    throw new NonexistentEntityException("The menunivel2 with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel2 menunivel2;
            try {
                menunivel2 = em.getReference(Menunivel2.class, id);
                menunivel2.getIdMenuNivel2();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The menunivel2 with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Menunivel3> menunivel3CollectionOrphanCheck = menunivel2.getMenunivel3Collection();
            for (Menunivel3 menunivel3CollectionOrphanCheckMenunivel3 : menunivel3CollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Menunivel2 (" + menunivel2 + ") cannot be destroyed since the Menunivel3 " + menunivel3CollectionOrphanCheckMenunivel3 + " in its menunivel3Collection field has a non-nullable idMenuNivel2 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Menunivel1 idMenuNivel1 = menunivel2.getIdMenuNivel1();
            if (idMenuNivel1 != null) {
                idMenuNivel1.getMenunivel2Collection().remove(menunivel2);
                idMenuNivel1 = em.merge(idMenuNivel1);
            }
            em.remove(menunivel2);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Menunivel2> findMenunivel2Entities() {
        return findMenunivel2Entities(true, -1, -1);
    }

    public List<Menunivel2> findMenunivel2Entities(int maxResults, int firstResult) {
        return findMenunivel2Entities(false, maxResults, firstResult);
    }

    private List<Menunivel2> findMenunivel2Entities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Menunivel2.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Menunivel2 findMenunivel2(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Menunivel2.class, id);
        } finally {
            em.close();
        }
    }

    public int getMenunivel2Count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Menunivel2> rt = cq.from(Menunivel2.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
