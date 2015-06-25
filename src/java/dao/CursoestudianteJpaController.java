/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Cursoestudiante;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Estudiante;
import entidades.Grado;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class CursoestudianteJpaController implements Serializable {

    public CursoestudianteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cursoestudiante cursoestudiante) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estudiante idUsuario = cursoestudiante.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdEstudiante());
                cursoestudiante.setIdUsuario(idUsuario);
            }
            Grado idgrado = cursoestudiante.getIdgrado();
            if (idgrado != null) {
                idgrado = em.getReference(idgrado.getClass(), idgrado.getIdGrado());
                cursoestudiante.setIdgrado(idgrado);
            }
            em.persist(cursoestudiante);
            if (idUsuario != null) {
                idUsuario.getCursoestudianteCollection().add(cursoestudiante);
                idUsuario = em.merge(idUsuario);
            }
            if (idgrado != null) {
                idgrado.getCursoestudianteCollection().add(cursoestudiante);
                idgrado = em.merge(idgrado);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCursoestudiante(cursoestudiante.getIdGradoUsuario()) != null) {
                throw new PreexistingEntityException("Cursoestudiante " + cursoestudiante + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cursoestudiante cursoestudiante) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cursoestudiante persistentCursoestudiante = em.find(Cursoestudiante.class, cursoestudiante.getIdGradoUsuario());
            Estudiante idUsuarioOld = persistentCursoestudiante.getIdUsuario();
            Estudiante idUsuarioNew = cursoestudiante.getIdUsuario();
            Grado idgradoOld = persistentCursoestudiante.getIdgrado();
            Grado idgradoNew = cursoestudiante.getIdgrado();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdEstudiante());
                cursoestudiante.setIdUsuario(idUsuarioNew);
            }
            if (idgradoNew != null) {
                idgradoNew = em.getReference(idgradoNew.getClass(), idgradoNew.getIdGrado());
                cursoestudiante.setIdgrado(idgradoNew);
            }
            cursoestudiante = em.merge(cursoestudiante);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getCursoestudianteCollection().remove(cursoestudiante);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getCursoestudianteCollection().add(cursoestudiante);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (idgradoOld != null && !idgradoOld.equals(idgradoNew)) {
                idgradoOld.getCursoestudianteCollection().remove(cursoestudiante);
                idgradoOld = em.merge(idgradoOld);
            }
            if (idgradoNew != null && !idgradoNew.equals(idgradoOld)) {
                idgradoNew.getCursoestudianteCollection().add(cursoestudiante);
                idgradoNew = em.merge(idgradoNew);
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
                String id = cursoestudiante.getIdGradoUsuario();
                if (findCursoestudiante(id) == null) {
                    throw new NonexistentEntityException("The cursoestudiante with id " + id + " no longer exists.");
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
            Cursoestudiante cursoestudiante;
            try {
                cursoestudiante = em.getReference(Cursoestudiante.class, id);
                cursoestudiante.getIdGradoUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cursoestudiante with id " + id + " no longer exists.", enfe);
            }
            Estudiante idUsuario = cursoestudiante.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getCursoestudianteCollection().remove(cursoestudiante);
                idUsuario = em.merge(idUsuario);
            }
            Grado idgrado = cursoestudiante.getIdgrado();
            if (idgrado != null) {
                idgrado.getCursoestudianteCollection().remove(cursoestudiante);
                idgrado = em.merge(idgrado);
            }
            em.remove(cursoestudiante);
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

    public List<Cursoestudiante> findCursoestudianteEntities() {
        return findCursoestudianteEntities(true, -1, -1);
    }

    public List<Cursoestudiante> findCursoestudianteEntities(int maxResults, int firstResult) {
        return findCursoestudianteEntities(false, maxResults, firstResult);
    }

    private List<Cursoestudiante> findCursoestudianteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cursoestudiante.class));
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

    public Cursoestudiante findCursoestudiante(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cursoestudiante.class, id);
        } finally {
            em.close();
        }
    }

    public int getCursoestudianteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cursoestudiante> rt = cq.from(Cursoestudiante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
