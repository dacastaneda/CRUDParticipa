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
import entidades.Menunivel1;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Menunivel2;
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
public class Menunivel1JpaController implements Serializable {

    public Menunivel1JpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Menunivel1 menunivel1) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (menunivel1.getMenunivel2Collection() == null) {
            menunivel1.setMenunivel2Collection(new ArrayList<Menunivel2>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Menunivel2> attachedMenunivel2Collection = new ArrayList<Menunivel2>();
            for (Menunivel2 menunivel2CollectionMenunivel2ToAttach : menunivel1.getMenunivel2Collection()) {
                menunivel2CollectionMenunivel2ToAttach = em.getReference(menunivel2CollectionMenunivel2ToAttach.getClass(), menunivel2CollectionMenunivel2ToAttach.getIdMenuNivel2());
                attachedMenunivel2Collection.add(menunivel2CollectionMenunivel2ToAttach);
            }
            menunivel1.setMenunivel2Collection(attachedMenunivel2Collection);
            em.persist(menunivel1);
            for (Menunivel2 menunivel2CollectionMenunivel2 : menunivel1.getMenunivel2Collection()) {
                Menunivel1 oldIdMenuNivel1OfMenunivel2CollectionMenunivel2 = menunivel2CollectionMenunivel2.getIdMenuNivel1();
                menunivel2CollectionMenunivel2.setIdMenuNivel1(menunivel1);
                menunivel2CollectionMenunivel2 = em.merge(menunivel2CollectionMenunivel2);
                if (oldIdMenuNivel1OfMenunivel2CollectionMenunivel2 != null) {
                    oldIdMenuNivel1OfMenunivel2CollectionMenunivel2.getMenunivel2Collection().remove(menunivel2CollectionMenunivel2);
                    oldIdMenuNivel1OfMenunivel2CollectionMenunivel2 = em.merge(oldIdMenuNivel1OfMenunivel2CollectionMenunivel2);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMenunivel1(menunivel1.getIdMenuNivel1()) != null) {
                throw new PreexistingEntityException("Menunivel1 " + menunivel1 + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Menunivel1 menunivel1) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel1 persistentMenunivel1 = em.find(Menunivel1.class, menunivel1.getIdMenuNivel1());
            Collection<Menunivel2> menunivel2CollectionOld = persistentMenunivel1.getMenunivel2Collection();
            Collection<Menunivel2> menunivel2CollectionNew = menunivel1.getMenunivel2Collection();
            List<String> illegalOrphanMessages = null;
            for (Menunivel2 menunivel2CollectionOldMenunivel2 : menunivel2CollectionOld) {
                if (!menunivel2CollectionNew.contains(menunivel2CollectionOldMenunivel2)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Menunivel2 " + menunivel2CollectionOldMenunivel2 + " since its idMenuNivel1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Menunivel2> attachedMenunivel2CollectionNew = new ArrayList<Menunivel2>();
            for (Menunivel2 menunivel2CollectionNewMenunivel2ToAttach : menunivel2CollectionNew) {
                menunivel2CollectionNewMenunivel2ToAttach = em.getReference(menunivel2CollectionNewMenunivel2ToAttach.getClass(), menunivel2CollectionNewMenunivel2ToAttach.getIdMenuNivel2());
                attachedMenunivel2CollectionNew.add(menunivel2CollectionNewMenunivel2ToAttach);
            }
            menunivel2CollectionNew = attachedMenunivel2CollectionNew;
            menunivel1.setMenunivel2Collection(menunivel2CollectionNew);
            menunivel1 = em.merge(menunivel1);
            for (Menunivel2 menunivel2CollectionNewMenunivel2 : menunivel2CollectionNew) {
                if (!menunivel2CollectionOld.contains(menunivel2CollectionNewMenunivel2)) {
                    Menunivel1 oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2 = menunivel2CollectionNewMenunivel2.getIdMenuNivel1();
                    menunivel2CollectionNewMenunivel2.setIdMenuNivel1(menunivel1);
                    menunivel2CollectionNewMenunivel2 = em.merge(menunivel2CollectionNewMenunivel2);
                    if (oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2 != null && !oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2.equals(menunivel1)) {
                        oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2.getMenunivel2Collection().remove(menunivel2CollectionNewMenunivel2);
                        oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2 = em.merge(oldIdMenuNivel1OfMenunivel2CollectionNewMenunivel2);
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
                Integer id = menunivel1.getIdMenuNivel1();
                if (findMenunivel1(id) == null) {
                    throw new NonexistentEntityException("The menunivel1 with id " + id + " no longer exists.");
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
            Menunivel1 menunivel1;
            try {
                menunivel1 = em.getReference(Menunivel1.class, id);
                menunivel1.getIdMenuNivel1();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The menunivel1 with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Menunivel2> menunivel2CollectionOrphanCheck = menunivel1.getMenunivel2Collection();
            for (Menunivel2 menunivel2CollectionOrphanCheckMenunivel2 : menunivel2CollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Menunivel1 (" + menunivel1 + ") cannot be destroyed since the Menunivel2 " + menunivel2CollectionOrphanCheckMenunivel2 + " in its menunivel2Collection field has a non-nullable idMenuNivel1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(menunivel1);
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

    public List<Menunivel1> findMenunivel1Entities() {
        return findMenunivel1Entities(true, -1, -1);
    }

    public List<Menunivel1> findMenunivel1Entities(int maxResults, int firstResult) {
        return findMenunivel1Entities(false, maxResults, firstResult);
    }

    private List<Menunivel1> findMenunivel1Entities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Menunivel1.class));
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

    public Menunivel1 findMenunivel1(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Menunivel1.class, id);
        } finally {
            em.close();
        }
    }

    public int getMenunivel1Count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Menunivel1> rt = cq.from(Menunivel1.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
