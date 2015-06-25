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
import entidades.Usuario;
import entidades.Cursoestudiante;
import entidades.Estudiante;
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
public class EstudianteJpaController implements Serializable {

    public EstudianteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estudiante estudiante) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (estudiante.getCursoestudianteCollection() == null) {
            estudiante.setCursoestudianteCollection(new ArrayList<Cursoestudiante>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario idUsuario = estudiante.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdPersona());
                estudiante.setIdUsuario(idUsuario);
            }
            Collection<Cursoestudiante> attachedCursoestudianteCollection = new ArrayList<Cursoestudiante>();
            for (Cursoestudiante cursoestudianteCollectionCursoestudianteToAttach : estudiante.getCursoestudianteCollection()) {
                cursoestudianteCollectionCursoestudianteToAttach = em.getReference(cursoestudianteCollectionCursoestudianteToAttach.getClass(), cursoestudianteCollectionCursoestudianteToAttach.getIdGradoUsuario());
                attachedCursoestudianteCollection.add(cursoestudianteCollectionCursoestudianteToAttach);
            }
            estudiante.setCursoestudianteCollection(attachedCursoestudianteCollection);
            em.persist(estudiante);
            if (idUsuario != null) {
                idUsuario.getEstudianteCollection().add(estudiante);
                idUsuario = em.merge(idUsuario);
            }
            for (Cursoestudiante cursoestudianteCollectionCursoestudiante : estudiante.getCursoestudianteCollection()) {
                Estudiante oldIdUsuarioOfCursoestudianteCollectionCursoestudiante = cursoestudianteCollectionCursoestudiante.getIdUsuario();
                cursoestudianteCollectionCursoestudiante.setIdUsuario(estudiante);
                cursoestudianteCollectionCursoestudiante = em.merge(cursoestudianteCollectionCursoestudiante);
                if (oldIdUsuarioOfCursoestudianteCollectionCursoestudiante != null) {
                    oldIdUsuarioOfCursoestudianteCollectionCursoestudiante.getCursoestudianteCollection().remove(cursoestudianteCollectionCursoestudiante);
                    oldIdUsuarioOfCursoestudianteCollectionCursoestudiante = em.merge(oldIdUsuarioOfCursoestudianteCollectionCursoestudiante);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEstudiante(estudiante.getIdEstudiante()) != null) {
                throw new PreexistingEntityException("Estudiante " + estudiante + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estudiante estudiante) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estudiante persistentEstudiante = em.find(Estudiante.class, estudiante.getIdEstudiante());
            Usuario idUsuarioOld = persistentEstudiante.getIdUsuario();
            Usuario idUsuarioNew = estudiante.getIdUsuario();
            Collection<Cursoestudiante> cursoestudianteCollectionOld = persistentEstudiante.getCursoestudianteCollection();
            Collection<Cursoestudiante> cursoestudianteCollectionNew = estudiante.getCursoestudianteCollection();
            List<String> illegalOrphanMessages = null;
            for (Cursoestudiante cursoestudianteCollectionOldCursoestudiante : cursoestudianteCollectionOld) {
                if (!cursoestudianteCollectionNew.contains(cursoestudianteCollectionOldCursoestudiante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cursoestudiante " + cursoestudianteCollectionOldCursoestudiante + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdPersona());
                estudiante.setIdUsuario(idUsuarioNew);
            }
            Collection<Cursoestudiante> attachedCursoestudianteCollectionNew = new ArrayList<Cursoestudiante>();
            for (Cursoestudiante cursoestudianteCollectionNewCursoestudianteToAttach : cursoestudianteCollectionNew) {
                cursoestudianteCollectionNewCursoestudianteToAttach = em.getReference(cursoestudianteCollectionNewCursoestudianteToAttach.getClass(), cursoestudianteCollectionNewCursoestudianteToAttach.getIdGradoUsuario());
                attachedCursoestudianteCollectionNew.add(cursoestudianteCollectionNewCursoestudianteToAttach);
            }
            cursoestudianteCollectionNew = attachedCursoestudianteCollectionNew;
            estudiante.setCursoestudianteCollection(cursoestudianteCollectionNew);
            estudiante = em.merge(estudiante);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getEstudianteCollection().remove(estudiante);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getEstudianteCollection().add(estudiante);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (Cursoestudiante cursoestudianteCollectionNewCursoestudiante : cursoestudianteCollectionNew) {
                if (!cursoestudianteCollectionOld.contains(cursoestudianteCollectionNewCursoestudiante)) {
                    Estudiante oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante = cursoestudianteCollectionNewCursoestudiante.getIdUsuario();
                    cursoestudianteCollectionNewCursoestudiante.setIdUsuario(estudiante);
                    cursoestudianteCollectionNewCursoestudiante = em.merge(cursoestudianteCollectionNewCursoestudiante);
                    if (oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante != null && !oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante.equals(estudiante)) {
                        oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante.getCursoestudianteCollection().remove(cursoestudianteCollectionNewCursoestudiante);
                        oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante = em.merge(oldIdUsuarioOfCursoestudianteCollectionNewCursoestudiante);
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
                Integer id = estudiante.getIdEstudiante();
                if (findEstudiante(id) == null) {
                    throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.");
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
            Estudiante estudiante;
            try {
                estudiante = em.getReference(Estudiante.class, id);
                estudiante.getIdEstudiante();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Cursoestudiante> cursoestudianteCollectionOrphanCheck = estudiante.getCursoestudianteCollection();
            for (Cursoestudiante cursoestudianteCollectionOrphanCheckCursoestudiante : cursoestudianteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estudiante (" + estudiante + ") cannot be destroyed since the Cursoestudiante " + cursoestudianteCollectionOrphanCheckCursoestudiante + " in its cursoestudianteCollection field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = estudiante.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getEstudianteCollection().remove(estudiante);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(estudiante);
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

    public List<Estudiante> findEstudianteEntities() {
        return findEstudianteEntities(true, -1, -1);
    }

    public List<Estudiante> findEstudianteEntities(int maxResults, int firstResult) {
        return findEstudianteEntities(false, maxResults, firstResult);
    }

    private List<Estudiante> findEstudianteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estudiante.class));
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

    public Estudiante findEstudiante(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estudiante.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstudianteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estudiante> rt = cq.from(Estudiante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
