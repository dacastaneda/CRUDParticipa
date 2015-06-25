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
import entidades.Actividadacalificar;
import entidades.Notas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class NotasJpaController implements Serializable {

    public NotasJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Notas notas) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Actividadacalificar actividadacalificarOrphanCheck = notas.getActividadacalificar();
        if (actividadacalificarOrphanCheck != null) {
            Notas oldNotasOfActividadacalificar = actividadacalificarOrphanCheck.getNotas();
            if (oldNotasOfActividadacalificar != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Actividadacalificar " + actividadacalificarOrphanCheck + " already has an item of type Notas whose actividadacalificar column cannot be null. Please make another selection for the actividadacalificar field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actividadacalificar actividadacalificar = notas.getActividadacalificar();
            if (actividadacalificar != null) {
                actividadacalificar = em.getReference(actividadacalificar.getClass(), actividadacalificar.getIdActividadCalificada());
                notas.setActividadacalificar(actividadacalificar);
            }
            em.persist(notas);
            if (actividadacalificar != null) {
                actividadacalificar.setNotas(notas);
                actividadacalificar = em.merge(actividadacalificar);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findNotas(notas.getIdActividadCalificada()) != null) {
                throw new PreexistingEntityException("Notas " + notas + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Notas notas) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Notas persistentNotas = em.find(Notas.class, notas.getIdActividadCalificada());
            Actividadacalificar actividadacalificarOld = persistentNotas.getActividadacalificar();
            Actividadacalificar actividadacalificarNew = notas.getActividadacalificar();
            List<String> illegalOrphanMessages = null;
            if (actividadacalificarNew != null && !actividadacalificarNew.equals(actividadacalificarOld)) {
                Notas oldNotasOfActividadacalificar = actividadacalificarNew.getNotas();
                if (oldNotasOfActividadacalificar != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Actividadacalificar " + actividadacalificarNew + " already has an item of type Notas whose actividadacalificar column cannot be null. Please make another selection for the actividadacalificar field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (actividadacalificarNew != null) {
                actividadacalificarNew = em.getReference(actividadacalificarNew.getClass(), actividadacalificarNew.getIdActividadCalificada());
                notas.setActividadacalificar(actividadacalificarNew);
            }
            notas = em.merge(notas);
            if (actividadacalificarOld != null && !actividadacalificarOld.equals(actividadacalificarNew)) {
                actividadacalificarOld.setNotas(null);
                actividadacalificarOld = em.merge(actividadacalificarOld);
            }
            if (actividadacalificarNew != null && !actividadacalificarNew.equals(actividadacalificarOld)) {
                actividadacalificarNew.setNotas(notas);
                actividadacalificarNew = em.merge(actividadacalificarNew);
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
                String id = notas.getIdActividadCalificada();
                if (findNotas(id) == null) {
                    throw new NonexistentEntityException("The notas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Notas notas;
            try {
                notas = em.getReference(Notas.class, id);
                notas.getIdActividadCalificada();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The notas with id " + id + " no longer exists.", enfe);
            }
            Actividadacalificar actividadacalificar = notas.getActividadacalificar();
            if (actividadacalificar != null) {
                actividadacalificar.setNotas(null);
                actividadacalificar = em.merge(actividadacalificar);
            }
            em.remove(notas);
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

    public List<Notas> findNotasEntities() {
        return findNotasEntities(true, -1, -1);
    }

    public List<Notas> findNotasEntities(int maxResults, int firstResult) {
        return findNotasEntities(false, maxResults, firstResult);
    }

    private List<Notas> findNotasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Notas.class));
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

    public Notas findNotas(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Notas.class, id);
        } finally {
            em.close();
        }
    }

    public int getNotasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Notas> rt = cq.from(Notas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
