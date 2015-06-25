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
import entidades.Periodocalificable;
import entidades.Periodolectivo;
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
public class PeriodolectivoJpaController implements Serializable {

    public PeriodolectivoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Periodolectivo periodolectivo) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (periodolectivo.getPeriodocalificableCollection() == null) {
            periodolectivo.setPeriodocalificableCollection(new ArrayList<Periodocalificable>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Periodocalificable> attachedPeriodocalificableCollection = new ArrayList<Periodocalificable>();
            for (Periodocalificable periodocalificableCollectionPeriodocalificableToAttach : periodolectivo.getPeriodocalificableCollection()) {
                periodocalificableCollectionPeriodocalificableToAttach = em.getReference(periodocalificableCollectionPeriodocalificableToAttach.getClass(), periodocalificableCollectionPeriodocalificableToAttach.getIdperiodoCalificable());
                attachedPeriodocalificableCollection.add(periodocalificableCollectionPeriodocalificableToAttach);
            }
            periodolectivo.setPeriodocalificableCollection(attachedPeriodocalificableCollection);
            em.persist(periodolectivo);
            for (Periodocalificable periodocalificableCollectionPeriodocalificable : periodolectivo.getPeriodocalificableCollection()) {
                Periodolectivo oldIdPeriodoLectivoOfPeriodocalificableCollectionPeriodocalificable = periodocalificableCollectionPeriodocalificable.getIdPeriodoLectivo();
                periodocalificableCollectionPeriodocalificable.setIdPeriodoLectivo(periodolectivo);
                periodocalificableCollectionPeriodocalificable = em.merge(periodocalificableCollectionPeriodocalificable);
                if (oldIdPeriodoLectivoOfPeriodocalificableCollectionPeriodocalificable != null) {
                    oldIdPeriodoLectivoOfPeriodocalificableCollectionPeriodocalificable.getPeriodocalificableCollection().remove(periodocalificableCollectionPeriodocalificable);
                    oldIdPeriodoLectivoOfPeriodocalificableCollectionPeriodocalificable = em.merge(oldIdPeriodoLectivoOfPeriodocalificableCollectionPeriodocalificable);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPeriodolectivo(periodolectivo.getIdPeriodoLectivo()) != null) {
                throw new PreexistingEntityException("Periodolectivo " + periodolectivo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Periodolectivo periodolectivo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Periodolectivo persistentPeriodolectivo = em.find(Periodolectivo.class, periodolectivo.getIdPeriodoLectivo());
            Collection<Periodocalificable> periodocalificableCollectionOld = persistentPeriodolectivo.getPeriodocalificableCollection();
            Collection<Periodocalificable> periodocalificableCollectionNew = periodolectivo.getPeriodocalificableCollection();
            List<String> illegalOrphanMessages = null;
            for (Periodocalificable periodocalificableCollectionOldPeriodocalificable : periodocalificableCollectionOld) {
                if (!periodocalificableCollectionNew.contains(periodocalificableCollectionOldPeriodocalificable)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Periodocalificable " + periodocalificableCollectionOldPeriodocalificable + " since its idPeriodoLectivo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Periodocalificable> attachedPeriodocalificableCollectionNew = new ArrayList<Periodocalificable>();
            for (Periodocalificable periodocalificableCollectionNewPeriodocalificableToAttach : periodocalificableCollectionNew) {
                periodocalificableCollectionNewPeriodocalificableToAttach = em.getReference(periodocalificableCollectionNewPeriodocalificableToAttach.getClass(), periodocalificableCollectionNewPeriodocalificableToAttach.getIdperiodoCalificable());
                attachedPeriodocalificableCollectionNew.add(periodocalificableCollectionNewPeriodocalificableToAttach);
            }
            periodocalificableCollectionNew = attachedPeriodocalificableCollectionNew;
            periodolectivo.setPeriodocalificableCollection(periodocalificableCollectionNew);
            periodolectivo = em.merge(periodolectivo);
            for (Periodocalificable periodocalificableCollectionNewPeriodocalificable : periodocalificableCollectionNew) {
                if (!periodocalificableCollectionOld.contains(periodocalificableCollectionNewPeriodocalificable)) {
                    Periodolectivo oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable = periodocalificableCollectionNewPeriodocalificable.getIdPeriodoLectivo();
                    periodocalificableCollectionNewPeriodocalificable.setIdPeriodoLectivo(periodolectivo);
                    periodocalificableCollectionNewPeriodocalificable = em.merge(periodocalificableCollectionNewPeriodocalificable);
                    if (oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable != null && !oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable.equals(periodolectivo)) {
                        oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable.getPeriodocalificableCollection().remove(periodocalificableCollectionNewPeriodocalificable);
                        oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable = em.merge(oldIdPeriodoLectivoOfPeriodocalificableCollectionNewPeriodocalificable);
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
                String id = periodolectivo.getIdPeriodoLectivo();
                if (findPeriodolectivo(id) == null) {
                    throw new NonexistentEntityException("The periodolectivo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Periodolectivo periodolectivo;
            try {
                periodolectivo = em.getReference(Periodolectivo.class, id);
                periodolectivo.getIdPeriodoLectivo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The periodolectivo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Periodocalificable> periodocalificableCollectionOrphanCheck = periodolectivo.getPeriodocalificableCollection();
            for (Periodocalificable periodocalificableCollectionOrphanCheckPeriodocalificable : periodocalificableCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Periodolectivo (" + periodolectivo + ") cannot be destroyed since the Periodocalificable " + periodocalificableCollectionOrphanCheckPeriodocalificable + " in its periodocalificableCollection field has a non-nullable idPeriodoLectivo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(periodolectivo);
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

    public List<Periodolectivo> findPeriodolectivoEntities() {
        return findPeriodolectivoEntities(true, -1, -1);
    }

    public List<Periodolectivo> findPeriodolectivoEntities(int maxResults, int firstResult) {
        return findPeriodolectivoEntities(false, maxResults, firstResult);
    }

    private List<Periodolectivo> findPeriodolectivoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Periodolectivo.class));
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

    public Periodolectivo findPeriodolectivo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Periodolectivo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeriodolectivoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Periodolectivo> rt = cq.from(Periodolectivo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
