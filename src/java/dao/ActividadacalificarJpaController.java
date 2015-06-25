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
import entidades.Actividadacalificar;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Notas;
import entidades.Logro;
import entidades.Periodocalificable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class ActividadacalificarJpaController implements Serializable {

    public ActividadacalificarJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Actividadacalificar actividadacalificar) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Notas notas = actividadacalificar.getNotas();
            if (notas != null) {
                notas = em.getReference(notas.getClass(), notas.getIdActividadCalificada());
                actividadacalificar.setNotas(notas);
            }
            Logro idLogro = actividadacalificar.getIdLogro();
            if (idLogro != null) {
                idLogro = em.getReference(idLogro.getClass(), idLogro.getIdLogro());
                actividadacalificar.setIdLogro(idLogro);
            }
            Periodocalificable idperiodoCalificable = actividadacalificar.getIdperiodoCalificable();
            if (idperiodoCalificable != null) {
                idperiodoCalificable = em.getReference(idperiodoCalificable.getClass(), idperiodoCalificable.getIdperiodoCalificable());
                actividadacalificar.setIdperiodoCalificable(idperiodoCalificable);
            }
            em.persist(actividadacalificar);
            if (notas != null) {
                Actividadacalificar oldActividadacalificarOfNotas = notas.getActividadacalificar();
                if (oldActividadacalificarOfNotas != null) {
                    oldActividadacalificarOfNotas.setNotas(null);
                    oldActividadacalificarOfNotas = em.merge(oldActividadacalificarOfNotas);
                }
                notas.setActividadacalificar(actividadacalificar);
                notas = em.merge(notas);
            }
            if (idLogro != null) {
                idLogro.getActividadacalificarCollection().add(actividadacalificar);
                idLogro = em.merge(idLogro);
            }
            if (idperiodoCalificable != null) {
                idperiodoCalificable.getActividadacalificarCollection().add(actividadacalificar);
                idperiodoCalificable = em.merge(idperiodoCalificable);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findActividadacalificar(actividadacalificar.getIdActividadCalificada()) != null) {
                throw new PreexistingEntityException("Actividadacalificar " + actividadacalificar + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Actividadacalificar actividadacalificar) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actividadacalificar persistentActividadacalificar = em.find(Actividadacalificar.class, actividadacalificar.getIdActividadCalificada());
            Notas notasOld = persistentActividadacalificar.getNotas();
            Notas notasNew = actividadacalificar.getNotas();
            Logro idLogroOld = persistentActividadacalificar.getIdLogro();
            Logro idLogroNew = actividadacalificar.getIdLogro();
            Periodocalificable idperiodoCalificableOld = persistentActividadacalificar.getIdperiodoCalificable();
            Periodocalificable idperiodoCalificableNew = actividadacalificar.getIdperiodoCalificable();
            List<String> illegalOrphanMessages = null;
            if (notasOld != null && !notasOld.equals(notasNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Notas " + notasOld + " since its actividadacalificar field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (notasNew != null) {
                notasNew = em.getReference(notasNew.getClass(), notasNew.getIdActividadCalificada());
                actividadacalificar.setNotas(notasNew);
            }
            if (idLogroNew != null) {
                idLogroNew = em.getReference(idLogroNew.getClass(), idLogroNew.getIdLogro());
                actividadacalificar.setIdLogro(idLogroNew);
            }
            if (idperiodoCalificableNew != null) {
                idperiodoCalificableNew = em.getReference(idperiodoCalificableNew.getClass(), idperiodoCalificableNew.getIdperiodoCalificable());
                actividadacalificar.setIdperiodoCalificable(idperiodoCalificableNew);
            }
            actividadacalificar = em.merge(actividadacalificar);
            if (notasNew != null && !notasNew.equals(notasOld)) {
                Actividadacalificar oldActividadacalificarOfNotas = notasNew.getActividadacalificar();
                if (oldActividadacalificarOfNotas != null) {
                    oldActividadacalificarOfNotas.setNotas(null);
                    oldActividadacalificarOfNotas = em.merge(oldActividadacalificarOfNotas);
                }
                notasNew.setActividadacalificar(actividadacalificar);
                notasNew = em.merge(notasNew);
            }
            if (idLogroOld != null && !idLogroOld.equals(idLogroNew)) {
                idLogroOld.getActividadacalificarCollection().remove(actividadacalificar);
                idLogroOld = em.merge(idLogroOld);
            }
            if (idLogroNew != null && !idLogroNew.equals(idLogroOld)) {
                idLogroNew.getActividadacalificarCollection().add(actividadacalificar);
                idLogroNew = em.merge(idLogroNew);
            }
            if (idperiodoCalificableOld != null && !idperiodoCalificableOld.equals(idperiodoCalificableNew)) {
                idperiodoCalificableOld.getActividadacalificarCollection().remove(actividadacalificar);
                idperiodoCalificableOld = em.merge(idperiodoCalificableOld);
            }
            if (idperiodoCalificableNew != null && !idperiodoCalificableNew.equals(idperiodoCalificableOld)) {
                idperiodoCalificableNew.getActividadacalificarCollection().add(actividadacalificar);
                idperiodoCalificableNew = em.merge(idperiodoCalificableNew);
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
                String id = actividadacalificar.getIdActividadCalificada();
                if (findActividadacalificar(id) == null) {
                    throw new NonexistentEntityException("The actividadacalificar with id " + id + " no longer exists.");
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
            Actividadacalificar actividadacalificar;
            try {
                actividadacalificar = em.getReference(Actividadacalificar.class, id);
                actividadacalificar.getIdActividadCalificada();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The actividadacalificar with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Notas notasOrphanCheck = actividadacalificar.getNotas();
            if (notasOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Actividadacalificar (" + actividadacalificar + ") cannot be destroyed since the Notas " + notasOrphanCheck + " in its notas field has a non-nullable actividadacalificar field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Logro idLogro = actividadacalificar.getIdLogro();
            if (idLogro != null) {
                idLogro.getActividadacalificarCollection().remove(actividadacalificar);
                idLogro = em.merge(idLogro);
            }
            Periodocalificable idperiodoCalificable = actividadacalificar.getIdperiodoCalificable();
            if (idperiodoCalificable != null) {
                idperiodoCalificable.getActividadacalificarCollection().remove(actividadacalificar);
                idperiodoCalificable = em.merge(idperiodoCalificable);
            }
            em.remove(actividadacalificar);
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

    public List<Actividadacalificar> findActividadacalificarEntities() {
        return findActividadacalificarEntities(true, -1, -1);
    }

    public List<Actividadacalificar> findActividadacalificarEntities(int maxResults, int firstResult) {
        return findActividadacalificarEntities(false, maxResults, firstResult);
    }

    private List<Actividadacalificar> findActividadacalificarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Actividadacalificar.class));
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

    public Actividadacalificar findActividadacalificar(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Actividadacalificar.class, id);
        } finally {
            em.close();
        }
    }

    public int getActividadacalificarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Actividadacalificar> rt = cq.from(Actividadacalificar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
