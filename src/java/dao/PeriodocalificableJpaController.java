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
import entidades.Periodolectivo;
import entidades.Actividadacalificar;
import entidades.Periodocalificable;
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
public class PeriodocalificableJpaController implements Serializable {

    public PeriodocalificableJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Periodocalificable periodocalificable) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (periodocalificable.getActividadacalificarCollection() == null) {
            periodocalificable.setActividadacalificarCollection(new ArrayList<Actividadacalificar>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Periodolectivo idPeriodoLectivo = periodocalificable.getIdPeriodoLectivo();
            if (idPeriodoLectivo != null) {
                idPeriodoLectivo = em.getReference(idPeriodoLectivo.getClass(), idPeriodoLectivo.getIdPeriodoLectivo());
                periodocalificable.setIdPeriodoLectivo(idPeriodoLectivo);
            }
            Collection<Actividadacalificar> attachedActividadacalificarCollection = new ArrayList<Actividadacalificar>();
            for (Actividadacalificar actividadacalificarCollectionActividadacalificarToAttach : periodocalificable.getActividadacalificarCollection()) {
                actividadacalificarCollectionActividadacalificarToAttach = em.getReference(actividadacalificarCollectionActividadacalificarToAttach.getClass(), actividadacalificarCollectionActividadacalificarToAttach.getIdActividadCalificada());
                attachedActividadacalificarCollection.add(actividadacalificarCollectionActividadacalificarToAttach);
            }
            periodocalificable.setActividadacalificarCollection(attachedActividadacalificarCollection);
            em.persist(periodocalificable);
            if (idPeriodoLectivo != null) {
                idPeriodoLectivo.getPeriodocalificableCollection().add(periodocalificable);
                idPeriodoLectivo = em.merge(idPeriodoLectivo);
            }
            for (Actividadacalificar actividadacalificarCollectionActividadacalificar : periodocalificable.getActividadacalificarCollection()) {
                Periodocalificable oldIdperiodoCalificableOfActividadacalificarCollectionActividadacalificar = actividadacalificarCollectionActividadacalificar.getIdperiodoCalificable();
                actividadacalificarCollectionActividadacalificar.setIdperiodoCalificable(periodocalificable);
                actividadacalificarCollectionActividadacalificar = em.merge(actividadacalificarCollectionActividadacalificar);
                if (oldIdperiodoCalificableOfActividadacalificarCollectionActividadacalificar != null) {
                    oldIdperiodoCalificableOfActividadacalificarCollectionActividadacalificar.getActividadacalificarCollection().remove(actividadacalificarCollectionActividadacalificar);
                    oldIdperiodoCalificableOfActividadacalificarCollectionActividadacalificar = em.merge(oldIdperiodoCalificableOfActividadacalificarCollectionActividadacalificar);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPeriodocalificable(periodocalificable.getIdperiodoCalificable()) != null) {
                throw new PreexistingEntityException("Periodocalificable " + periodocalificable + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Periodocalificable periodocalificable) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Periodocalificable persistentPeriodocalificable = em.find(Periodocalificable.class, periodocalificable.getIdperiodoCalificable());
            Periodolectivo idPeriodoLectivoOld = persistentPeriodocalificable.getIdPeriodoLectivo();
            Periodolectivo idPeriodoLectivoNew = periodocalificable.getIdPeriodoLectivo();
            Collection<Actividadacalificar> actividadacalificarCollectionOld = persistentPeriodocalificable.getActividadacalificarCollection();
            Collection<Actividadacalificar> actividadacalificarCollectionNew = periodocalificable.getActividadacalificarCollection();
            List<String> illegalOrphanMessages = null;
            for (Actividadacalificar actividadacalificarCollectionOldActividadacalificar : actividadacalificarCollectionOld) {
                if (!actividadacalificarCollectionNew.contains(actividadacalificarCollectionOldActividadacalificar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Actividadacalificar " + actividadacalificarCollectionOldActividadacalificar + " since its idperiodoCalificable field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idPeriodoLectivoNew != null) {
                idPeriodoLectivoNew = em.getReference(idPeriodoLectivoNew.getClass(), idPeriodoLectivoNew.getIdPeriodoLectivo());
                periodocalificable.setIdPeriodoLectivo(idPeriodoLectivoNew);
            }
            Collection<Actividadacalificar> attachedActividadacalificarCollectionNew = new ArrayList<Actividadacalificar>();
            for (Actividadacalificar actividadacalificarCollectionNewActividadacalificarToAttach : actividadacalificarCollectionNew) {
                actividadacalificarCollectionNewActividadacalificarToAttach = em.getReference(actividadacalificarCollectionNewActividadacalificarToAttach.getClass(), actividadacalificarCollectionNewActividadacalificarToAttach.getIdActividadCalificada());
                attachedActividadacalificarCollectionNew.add(actividadacalificarCollectionNewActividadacalificarToAttach);
            }
            actividadacalificarCollectionNew = attachedActividadacalificarCollectionNew;
            periodocalificable.setActividadacalificarCollection(actividadacalificarCollectionNew);
            periodocalificable = em.merge(periodocalificable);
            if (idPeriodoLectivoOld != null && !idPeriodoLectivoOld.equals(idPeriodoLectivoNew)) {
                idPeriodoLectivoOld.getPeriodocalificableCollection().remove(periodocalificable);
                idPeriodoLectivoOld = em.merge(idPeriodoLectivoOld);
            }
            if (idPeriodoLectivoNew != null && !idPeriodoLectivoNew.equals(idPeriodoLectivoOld)) {
                idPeriodoLectivoNew.getPeriodocalificableCollection().add(periodocalificable);
                idPeriodoLectivoNew = em.merge(idPeriodoLectivoNew);
            }
            for (Actividadacalificar actividadacalificarCollectionNewActividadacalificar : actividadacalificarCollectionNew) {
                if (!actividadacalificarCollectionOld.contains(actividadacalificarCollectionNewActividadacalificar)) {
                    Periodocalificable oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar = actividadacalificarCollectionNewActividadacalificar.getIdperiodoCalificable();
                    actividadacalificarCollectionNewActividadacalificar.setIdperiodoCalificable(periodocalificable);
                    actividadacalificarCollectionNewActividadacalificar = em.merge(actividadacalificarCollectionNewActividadacalificar);
                    if (oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar != null && !oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar.equals(periodocalificable)) {
                        oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar.getActividadacalificarCollection().remove(actividadacalificarCollectionNewActividadacalificar);
                        oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar = em.merge(oldIdperiodoCalificableOfActividadacalificarCollectionNewActividadacalificar);
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
                String id = periodocalificable.getIdperiodoCalificable();
                if (findPeriodocalificable(id) == null) {
                    throw new NonexistentEntityException("The periodocalificable with id " + id + " no longer exists.");
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
            Periodocalificable periodocalificable;
            try {
                periodocalificable = em.getReference(Periodocalificable.class, id);
                periodocalificable.getIdperiodoCalificable();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The periodocalificable with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Actividadacalificar> actividadacalificarCollectionOrphanCheck = periodocalificable.getActividadacalificarCollection();
            for (Actividadacalificar actividadacalificarCollectionOrphanCheckActividadacalificar : actividadacalificarCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Periodocalificable (" + periodocalificable + ") cannot be destroyed since the Actividadacalificar " + actividadacalificarCollectionOrphanCheckActividadacalificar + " in its actividadacalificarCollection field has a non-nullable idperiodoCalificable field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Periodolectivo idPeriodoLectivo = periodocalificable.getIdPeriodoLectivo();
            if (idPeriodoLectivo != null) {
                idPeriodoLectivo.getPeriodocalificableCollection().remove(periodocalificable);
                idPeriodoLectivo = em.merge(idPeriodoLectivo);
            }
            em.remove(periodocalificable);
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

    public List<Periodocalificable> findPeriodocalificableEntities() {
        return findPeriodocalificableEntities(true, -1, -1);
    }

    public List<Periodocalificable> findPeriodocalificableEntities(int maxResults, int firstResult) {
        return findPeriodocalificableEntities(false, maxResults, firstResult);
    }

    private List<Periodocalificable> findPeriodocalificableEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Periodocalificable.class));
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

    public Periodocalificable findPeriodocalificable(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Periodocalificable.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeriodocalificableCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Periodocalificable> rt = cq.from(Periodocalificable.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
