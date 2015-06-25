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
import entidades.Componente;
import entidades.Actividadacalificar;
import entidades.Logro;
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
public class LogroJpaController implements Serializable {

    public LogroJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Logro logro) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (logro.getActividadacalificarCollection() == null) {
            logro.setActividadacalificarCollection(new ArrayList<Actividadacalificar>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Componente idComponente = logro.getIdComponente();
            if (idComponente != null) {
                idComponente = em.getReference(idComponente.getClass(), idComponente.getIdComponente());
                logro.setIdComponente(idComponente);
            }
            Collection<Actividadacalificar> attachedActividadacalificarCollection = new ArrayList<Actividadacalificar>();
            for (Actividadacalificar actividadacalificarCollectionActividadacalificarToAttach : logro.getActividadacalificarCollection()) {
                actividadacalificarCollectionActividadacalificarToAttach = em.getReference(actividadacalificarCollectionActividadacalificarToAttach.getClass(), actividadacalificarCollectionActividadacalificarToAttach.getIdActividadCalificada());
                attachedActividadacalificarCollection.add(actividadacalificarCollectionActividadacalificarToAttach);
            }
            logro.setActividadacalificarCollection(attachedActividadacalificarCollection);
            em.persist(logro);
            if (idComponente != null) {
                idComponente.getLogroCollection().add(logro);
                idComponente = em.merge(idComponente);
            }
            for (Actividadacalificar actividadacalificarCollectionActividadacalificar : logro.getActividadacalificarCollection()) {
                Logro oldIdLogroOfActividadacalificarCollectionActividadacalificar = actividadacalificarCollectionActividadacalificar.getIdLogro();
                actividadacalificarCollectionActividadacalificar.setIdLogro(logro);
                actividadacalificarCollectionActividadacalificar = em.merge(actividadacalificarCollectionActividadacalificar);
                if (oldIdLogroOfActividadacalificarCollectionActividadacalificar != null) {
                    oldIdLogroOfActividadacalificarCollectionActividadacalificar.getActividadacalificarCollection().remove(actividadacalificarCollectionActividadacalificar);
                    oldIdLogroOfActividadacalificarCollectionActividadacalificar = em.merge(oldIdLogroOfActividadacalificarCollectionActividadacalificar);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findLogro(logro.getIdLogro()) != null) {
                throw new PreexistingEntityException("Logro " + logro + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Logro logro) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Logro persistentLogro = em.find(Logro.class, logro.getIdLogro());
            Componente idComponenteOld = persistentLogro.getIdComponente();
            Componente idComponenteNew = logro.getIdComponente();
            Collection<Actividadacalificar> actividadacalificarCollectionOld = persistentLogro.getActividadacalificarCollection();
            Collection<Actividadacalificar> actividadacalificarCollectionNew = logro.getActividadacalificarCollection();
            List<String> illegalOrphanMessages = null;
            for (Actividadacalificar actividadacalificarCollectionOldActividadacalificar : actividadacalificarCollectionOld) {
                if (!actividadacalificarCollectionNew.contains(actividadacalificarCollectionOldActividadacalificar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Actividadacalificar " + actividadacalificarCollectionOldActividadacalificar + " since its idLogro field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idComponenteNew != null) {
                idComponenteNew = em.getReference(idComponenteNew.getClass(), idComponenteNew.getIdComponente());
                logro.setIdComponente(idComponenteNew);
            }
            Collection<Actividadacalificar> attachedActividadacalificarCollectionNew = new ArrayList<Actividadacalificar>();
            for (Actividadacalificar actividadacalificarCollectionNewActividadacalificarToAttach : actividadacalificarCollectionNew) {
                actividadacalificarCollectionNewActividadacalificarToAttach = em.getReference(actividadacalificarCollectionNewActividadacalificarToAttach.getClass(), actividadacalificarCollectionNewActividadacalificarToAttach.getIdActividadCalificada());
                attachedActividadacalificarCollectionNew.add(actividadacalificarCollectionNewActividadacalificarToAttach);
            }
            actividadacalificarCollectionNew = attachedActividadacalificarCollectionNew;
            logro.setActividadacalificarCollection(actividadacalificarCollectionNew);
            logro = em.merge(logro);
            if (idComponenteOld != null && !idComponenteOld.equals(idComponenteNew)) {
                idComponenteOld.getLogroCollection().remove(logro);
                idComponenteOld = em.merge(idComponenteOld);
            }
            if (idComponenteNew != null && !idComponenteNew.equals(idComponenteOld)) {
                idComponenteNew.getLogroCollection().add(logro);
                idComponenteNew = em.merge(idComponenteNew);
            }
            for (Actividadacalificar actividadacalificarCollectionNewActividadacalificar : actividadacalificarCollectionNew) {
                if (!actividadacalificarCollectionOld.contains(actividadacalificarCollectionNewActividadacalificar)) {
                    Logro oldIdLogroOfActividadacalificarCollectionNewActividadacalificar = actividadacalificarCollectionNewActividadacalificar.getIdLogro();
                    actividadacalificarCollectionNewActividadacalificar.setIdLogro(logro);
                    actividadacalificarCollectionNewActividadacalificar = em.merge(actividadacalificarCollectionNewActividadacalificar);
                    if (oldIdLogroOfActividadacalificarCollectionNewActividadacalificar != null && !oldIdLogroOfActividadacalificarCollectionNewActividadacalificar.equals(logro)) {
                        oldIdLogroOfActividadacalificarCollectionNewActividadacalificar.getActividadacalificarCollection().remove(actividadacalificarCollectionNewActividadacalificar);
                        oldIdLogroOfActividadacalificarCollectionNewActividadacalificar = em.merge(oldIdLogroOfActividadacalificarCollectionNewActividadacalificar);
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
                String id = logro.getIdLogro();
                if (findLogro(id) == null) {
                    throw new NonexistentEntityException("The logro with id " + id + " no longer exists.");
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
            Logro logro;
            try {
                logro = em.getReference(Logro.class, id);
                logro.getIdLogro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The logro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Actividadacalificar> actividadacalificarCollectionOrphanCheck = logro.getActividadacalificarCollection();
            for (Actividadacalificar actividadacalificarCollectionOrphanCheckActividadacalificar : actividadacalificarCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Logro (" + logro + ") cannot be destroyed since the Actividadacalificar " + actividadacalificarCollectionOrphanCheckActividadacalificar + " in its actividadacalificarCollection field has a non-nullable idLogro field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Componente idComponente = logro.getIdComponente();
            if (idComponente != null) {
                idComponente.getLogroCollection().remove(logro);
                idComponente = em.merge(idComponente);
            }
            em.remove(logro);
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

    public List<Logro> findLogroEntities() {
        return findLogroEntities(true, -1, -1);
    }

    public List<Logro> findLogroEntities(int maxResults, int firstResult) {
        return findLogroEntities(false, maxResults, firstResult);
    }

    private List<Logro> findLogroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Logro.class));
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

    public Logro findLogro(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Logro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLogroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Logro> rt = cq.from(Logro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
