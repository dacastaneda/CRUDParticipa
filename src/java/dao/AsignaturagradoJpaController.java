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
import entidades.Asignatura;
import entidades.Asignaturagrado;
import entidades.Docente;
import entidades.Grado;
import entidades.Componente;
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
public class AsignaturagradoJpaController implements Serializable {

    public AsignaturagradoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Asignaturagrado asignaturagrado) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (asignaturagrado.getComponenteCollection() == null) {
            asignaturagrado.setComponenteCollection(new ArrayList<Componente>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Asignatura asignatura = asignaturagrado.getAsignatura();
            if (asignatura != null) {
                asignatura = em.getReference(asignatura.getClass(), asignatura.getIdAsignatura());
                asignaturagrado.setAsignatura(asignatura);
            }
            Docente idDocente = asignaturagrado.getIdDocente();
            if (idDocente != null) {
                idDocente = em.getReference(idDocente.getClass(), idDocente.getIdDocente());
                asignaturagrado.setIdDocente(idDocente);
            }
            Grado grado = asignaturagrado.getGrado();
            if (grado != null) {
                grado = em.getReference(grado.getClass(), grado.getIdGrado());
                asignaturagrado.setGrado(grado);
            }
            Collection<Componente> attachedComponenteCollection = new ArrayList<Componente>();
            for (Componente componenteCollectionComponenteToAttach : asignaturagrado.getComponenteCollection()) {
                componenteCollectionComponenteToAttach = em.getReference(componenteCollectionComponenteToAttach.getClass(), componenteCollectionComponenteToAttach.getIdComponente());
                attachedComponenteCollection.add(componenteCollectionComponenteToAttach);
            }
            asignaturagrado.setComponenteCollection(attachedComponenteCollection);
            em.persist(asignaturagrado);
            if (asignatura != null) {
                asignatura.getAsignaturagradoCollection().add(asignaturagrado);
                asignatura = em.merge(asignatura);
            }
            if (idDocente != null) {
                idDocente.getAsignaturagradoCollection().add(asignaturagrado);
                idDocente = em.merge(idDocente);
            }
            if (grado != null) {
                grado.getAsignaturagradoCollection().add(asignaturagrado);
                grado = em.merge(grado);
            }
            for (Componente componenteCollectionComponente : asignaturagrado.getComponenteCollection()) {
                Asignaturagrado oldAsignaturaGradoOfComponenteCollectionComponente = componenteCollectionComponente.getAsignaturaGrado();
                componenteCollectionComponente.setAsignaturaGrado(asignaturagrado);
                componenteCollectionComponente = em.merge(componenteCollectionComponente);
                if (oldAsignaturaGradoOfComponenteCollectionComponente != null) {
                    oldAsignaturaGradoOfComponenteCollectionComponente.getComponenteCollection().remove(componenteCollectionComponente);
                    oldAsignaturaGradoOfComponenteCollectionComponente = em.merge(oldAsignaturaGradoOfComponenteCollectionComponente);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAsignaturagrado(asignaturagrado.getIdAsignaturaGrado()) != null) {
                throw new PreexistingEntityException("Asignaturagrado " + asignaturagrado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Asignaturagrado asignaturagrado) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Asignaturagrado persistentAsignaturagrado = em.find(Asignaturagrado.class, asignaturagrado.getIdAsignaturaGrado());
            Asignatura asignaturaOld = persistentAsignaturagrado.getAsignatura();
            Asignatura asignaturaNew = asignaturagrado.getAsignatura();
            Docente idDocenteOld = persistentAsignaturagrado.getIdDocente();
            Docente idDocenteNew = asignaturagrado.getIdDocente();
            Grado gradoOld = persistentAsignaturagrado.getGrado();
            Grado gradoNew = asignaturagrado.getGrado();
            Collection<Componente> componenteCollectionOld = persistentAsignaturagrado.getComponenteCollection();
            Collection<Componente> componenteCollectionNew = asignaturagrado.getComponenteCollection();
            List<String> illegalOrphanMessages = null;
            for (Componente componenteCollectionOldComponente : componenteCollectionOld) {
                if (!componenteCollectionNew.contains(componenteCollectionOldComponente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Componente " + componenteCollectionOldComponente + " since its asignaturaGrado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (asignaturaNew != null) {
                asignaturaNew = em.getReference(asignaturaNew.getClass(), asignaturaNew.getIdAsignatura());
                asignaturagrado.setAsignatura(asignaturaNew);
            }
            if (idDocenteNew != null) {
                idDocenteNew = em.getReference(idDocenteNew.getClass(), idDocenteNew.getIdDocente());
                asignaturagrado.setIdDocente(idDocenteNew);
            }
            if (gradoNew != null) {
                gradoNew = em.getReference(gradoNew.getClass(), gradoNew.getIdGrado());
                asignaturagrado.setGrado(gradoNew);
            }
            Collection<Componente> attachedComponenteCollectionNew = new ArrayList<Componente>();
            for (Componente componenteCollectionNewComponenteToAttach : componenteCollectionNew) {
                componenteCollectionNewComponenteToAttach = em.getReference(componenteCollectionNewComponenteToAttach.getClass(), componenteCollectionNewComponenteToAttach.getIdComponente());
                attachedComponenteCollectionNew.add(componenteCollectionNewComponenteToAttach);
            }
            componenteCollectionNew = attachedComponenteCollectionNew;
            asignaturagrado.setComponenteCollection(componenteCollectionNew);
            asignaturagrado = em.merge(asignaturagrado);
            if (asignaturaOld != null && !asignaturaOld.equals(asignaturaNew)) {
                asignaturaOld.getAsignaturagradoCollection().remove(asignaturagrado);
                asignaturaOld = em.merge(asignaturaOld);
            }
            if (asignaturaNew != null && !asignaturaNew.equals(asignaturaOld)) {
                asignaturaNew.getAsignaturagradoCollection().add(asignaturagrado);
                asignaturaNew = em.merge(asignaturaNew);
            }
            if (idDocenteOld != null && !idDocenteOld.equals(idDocenteNew)) {
                idDocenteOld.getAsignaturagradoCollection().remove(asignaturagrado);
                idDocenteOld = em.merge(idDocenteOld);
            }
            if (idDocenteNew != null && !idDocenteNew.equals(idDocenteOld)) {
                idDocenteNew.getAsignaturagradoCollection().add(asignaturagrado);
                idDocenteNew = em.merge(idDocenteNew);
            }
            if (gradoOld != null && !gradoOld.equals(gradoNew)) {
                gradoOld.getAsignaturagradoCollection().remove(asignaturagrado);
                gradoOld = em.merge(gradoOld);
            }
            if (gradoNew != null && !gradoNew.equals(gradoOld)) {
                gradoNew.getAsignaturagradoCollection().add(asignaturagrado);
                gradoNew = em.merge(gradoNew);
            }
            for (Componente componenteCollectionNewComponente : componenteCollectionNew) {
                if (!componenteCollectionOld.contains(componenteCollectionNewComponente)) {
                    Asignaturagrado oldAsignaturaGradoOfComponenteCollectionNewComponente = componenteCollectionNewComponente.getAsignaturaGrado();
                    componenteCollectionNewComponente.setAsignaturaGrado(asignaturagrado);
                    componenteCollectionNewComponente = em.merge(componenteCollectionNewComponente);
                    if (oldAsignaturaGradoOfComponenteCollectionNewComponente != null && !oldAsignaturaGradoOfComponenteCollectionNewComponente.equals(asignaturagrado)) {
                        oldAsignaturaGradoOfComponenteCollectionNewComponente.getComponenteCollection().remove(componenteCollectionNewComponente);
                        oldAsignaturaGradoOfComponenteCollectionNewComponente = em.merge(oldAsignaturaGradoOfComponenteCollectionNewComponente);
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
                String id = asignaturagrado.getIdAsignaturaGrado();
                if (findAsignaturagrado(id) == null) {
                    throw new NonexistentEntityException("The asignaturagrado with id " + id + " no longer exists.");
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
            Asignaturagrado asignaturagrado;
            try {
                asignaturagrado = em.getReference(Asignaturagrado.class, id);
                asignaturagrado.getIdAsignaturaGrado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The asignaturagrado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Componente> componenteCollectionOrphanCheck = asignaturagrado.getComponenteCollection();
            for (Componente componenteCollectionOrphanCheckComponente : componenteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Asignaturagrado (" + asignaturagrado + ") cannot be destroyed since the Componente " + componenteCollectionOrphanCheckComponente + " in its componenteCollection field has a non-nullable asignaturaGrado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Asignatura asignatura = asignaturagrado.getAsignatura();
            if (asignatura != null) {
                asignatura.getAsignaturagradoCollection().remove(asignaturagrado);
                asignatura = em.merge(asignatura);
            }
            Docente idDocente = asignaturagrado.getIdDocente();
            if (idDocente != null) {
                idDocente.getAsignaturagradoCollection().remove(asignaturagrado);
                idDocente = em.merge(idDocente);
            }
            Grado grado = asignaturagrado.getGrado();
            if (grado != null) {
                grado.getAsignaturagradoCollection().remove(asignaturagrado);
                grado = em.merge(grado);
            }
            em.remove(asignaturagrado);
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

    public List<Asignaturagrado> findAsignaturagradoEntities() {
        return findAsignaturagradoEntities(true, -1, -1);
    }

    public List<Asignaturagrado> findAsignaturagradoEntities(int maxResults, int firstResult) {
        return findAsignaturagradoEntities(false, maxResults, firstResult);
    }

    private List<Asignaturagrado> findAsignaturagradoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Asignaturagrado.class));
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

    public Asignaturagrado findAsignaturagrado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Asignaturagrado.class, id);
        } finally {
            em.close();
        }
    }

    public int getAsignaturagradoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Asignaturagrado> rt = cq.from(Asignaturagrado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
