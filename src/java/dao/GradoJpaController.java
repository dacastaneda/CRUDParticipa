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
import entidades.Asignaturagrado;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Cursoestudiante;
import entidades.Grado;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class GradoJpaController implements Serializable {

    public GradoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Grado grado) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (grado.getAsignaturagradoCollection() == null) {
            grado.setAsignaturagradoCollection(new ArrayList<Asignaturagrado>());
        }
        if (grado.getCursoestudianteCollection() == null) {
            grado.setCursoestudianteCollection(new ArrayList<Cursoestudiante>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Asignaturagrado> attachedAsignaturagradoCollection = new ArrayList<Asignaturagrado>();
            for (Asignaturagrado asignaturagradoCollectionAsignaturagradoToAttach : grado.getAsignaturagradoCollection()) {
                asignaturagradoCollectionAsignaturagradoToAttach = em.getReference(asignaturagradoCollectionAsignaturagradoToAttach.getClass(), asignaturagradoCollectionAsignaturagradoToAttach.getIdAsignaturaGrado());
                attachedAsignaturagradoCollection.add(asignaturagradoCollectionAsignaturagradoToAttach);
            }
            grado.setAsignaturagradoCollection(attachedAsignaturagradoCollection);
            Collection<Cursoestudiante> attachedCursoestudianteCollection = new ArrayList<Cursoestudiante>();
            for (Cursoestudiante cursoestudianteCollectionCursoestudianteToAttach : grado.getCursoestudianteCollection()) {
                cursoestudianteCollectionCursoestudianteToAttach = em.getReference(cursoestudianteCollectionCursoestudianteToAttach.getClass(), cursoestudianteCollectionCursoestudianteToAttach.getIdGradoUsuario());
                attachedCursoestudianteCollection.add(cursoestudianteCollectionCursoestudianteToAttach);
            }
            grado.setCursoestudianteCollection(attachedCursoestudianteCollection);
            em.persist(grado);
            for (Asignaturagrado asignaturagradoCollectionAsignaturagrado : grado.getAsignaturagradoCollection()) {
                Grado oldGradoOfAsignaturagradoCollectionAsignaturagrado = asignaturagradoCollectionAsignaturagrado.getGrado();
                asignaturagradoCollectionAsignaturagrado.setGrado(grado);
                asignaturagradoCollectionAsignaturagrado = em.merge(asignaturagradoCollectionAsignaturagrado);
                if (oldGradoOfAsignaturagradoCollectionAsignaturagrado != null) {
                    oldGradoOfAsignaturagradoCollectionAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionAsignaturagrado);
                    oldGradoOfAsignaturagradoCollectionAsignaturagrado = em.merge(oldGradoOfAsignaturagradoCollectionAsignaturagrado);
                }
            }
            for (Cursoestudiante cursoestudianteCollectionCursoestudiante : grado.getCursoestudianteCollection()) {
                Grado oldIdgradoOfCursoestudianteCollectionCursoestudiante = cursoestudianteCollectionCursoestudiante.getIdgrado();
                cursoestudianteCollectionCursoestudiante.setIdgrado(grado);
                cursoestudianteCollectionCursoestudiante = em.merge(cursoestudianteCollectionCursoestudiante);
                if (oldIdgradoOfCursoestudianteCollectionCursoestudiante != null) {
                    oldIdgradoOfCursoestudianteCollectionCursoestudiante.getCursoestudianteCollection().remove(cursoestudianteCollectionCursoestudiante);
                    oldIdgradoOfCursoestudianteCollectionCursoestudiante = em.merge(oldIdgradoOfCursoestudianteCollectionCursoestudiante);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findGrado(grado.getIdGrado()) != null) {
                throw new PreexistingEntityException("Grado " + grado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Grado grado) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Grado persistentGrado = em.find(Grado.class, grado.getIdGrado());
            Collection<Asignaturagrado> asignaturagradoCollectionOld = persistentGrado.getAsignaturagradoCollection();
            Collection<Asignaturagrado> asignaturagradoCollectionNew = grado.getAsignaturagradoCollection();
            Collection<Cursoestudiante> cursoestudianteCollectionOld = persistentGrado.getCursoestudianteCollection();
            Collection<Cursoestudiante> cursoestudianteCollectionNew = grado.getCursoestudianteCollection();
            List<String> illegalOrphanMessages = null;
            for (Asignaturagrado asignaturagradoCollectionOldAsignaturagrado : asignaturagradoCollectionOld) {
                if (!asignaturagradoCollectionNew.contains(asignaturagradoCollectionOldAsignaturagrado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Asignaturagrado " + asignaturagradoCollectionOldAsignaturagrado + " since its grado field is not nullable.");
                }
            }
            for (Cursoestudiante cursoestudianteCollectionOldCursoestudiante : cursoestudianteCollectionOld) {
                if (!cursoestudianteCollectionNew.contains(cursoestudianteCollectionOldCursoestudiante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cursoestudiante " + cursoestudianteCollectionOldCursoestudiante + " since its idgrado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Asignaturagrado> attachedAsignaturagradoCollectionNew = new ArrayList<Asignaturagrado>();
            for (Asignaturagrado asignaturagradoCollectionNewAsignaturagradoToAttach : asignaturagradoCollectionNew) {
                asignaturagradoCollectionNewAsignaturagradoToAttach = em.getReference(asignaturagradoCollectionNewAsignaturagradoToAttach.getClass(), asignaturagradoCollectionNewAsignaturagradoToAttach.getIdAsignaturaGrado());
                attachedAsignaturagradoCollectionNew.add(asignaturagradoCollectionNewAsignaturagradoToAttach);
            }
            asignaturagradoCollectionNew = attachedAsignaturagradoCollectionNew;
            grado.setAsignaturagradoCollection(asignaturagradoCollectionNew);
            Collection<Cursoestudiante> attachedCursoestudianteCollectionNew = new ArrayList<Cursoestudiante>();
            for (Cursoestudiante cursoestudianteCollectionNewCursoestudianteToAttach : cursoestudianteCollectionNew) {
                cursoestudianteCollectionNewCursoestudianteToAttach = em.getReference(cursoestudianteCollectionNewCursoestudianteToAttach.getClass(), cursoestudianteCollectionNewCursoestudianteToAttach.getIdGradoUsuario());
                attachedCursoestudianteCollectionNew.add(cursoestudianteCollectionNewCursoestudianteToAttach);
            }
            cursoestudianteCollectionNew = attachedCursoestudianteCollectionNew;
            grado.setCursoestudianteCollection(cursoestudianteCollectionNew);
            grado = em.merge(grado);
            for (Asignaturagrado asignaturagradoCollectionNewAsignaturagrado : asignaturagradoCollectionNew) {
                if (!asignaturagradoCollectionOld.contains(asignaturagradoCollectionNewAsignaturagrado)) {
                    Grado oldGradoOfAsignaturagradoCollectionNewAsignaturagrado = asignaturagradoCollectionNewAsignaturagrado.getGrado();
                    asignaturagradoCollectionNewAsignaturagrado.setGrado(grado);
                    asignaturagradoCollectionNewAsignaturagrado = em.merge(asignaturagradoCollectionNewAsignaturagrado);
                    if (oldGradoOfAsignaturagradoCollectionNewAsignaturagrado != null && !oldGradoOfAsignaturagradoCollectionNewAsignaturagrado.equals(grado)) {
                        oldGradoOfAsignaturagradoCollectionNewAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionNewAsignaturagrado);
                        oldGradoOfAsignaturagradoCollectionNewAsignaturagrado = em.merge(oldGradoOfAsignaturagradoCollectionNewAsignaturagrado);
                    }
                }
            }
            for (Cursoestudiante cursoestudianteCollectionNewCursoestudiante : cursoestudianteCollectionNew) {
                if (!cursoestudianteCollectionOld.contains(cursoestudianteCollectionNewCursoestudiante)) {
                    Grado oldIdgradoOfCursoestudianteCollectionNewCursoestudiante = cursoestudianteCollectionNewCursoestudiante.getIdgrado();
                    cursoestudianteCollectionNewCursoestudiante.setIdgrado(grado);
                    cursoestudianteCollectionNewCursoestudiante = em.merge(cursoestudianteCollectionNewCursoestudiante);
                    if (oldIdgradoOfCursoestudianteCollectionNewCursoestudiante != null && !oldIdgradoOfCursoestudianteCollectionNewCursoestudiante.equals(grado)) {
                        oldIdgradoOfCursoestudianteCollectionNewCursoestudiante.getCursoestudianteCollection().remove(cursoestudianteCollectionNewCursoestudiante);
                        oldIdgradoOfCursoestudianteCollectionNewCursoestudiante = em.merge(oldIdgradoOfCursoestudianteCollectionNewCursoestudiante);
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
                String id = grado.getIdGrado();
                if (findGrado(id) == null) {
                    throw new NonexistentEntityException("The grado with id " + id + " no longer exists.");
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
            Grado grado;
            try {
                grado = em.getReference(Grado.class, id);
                grado.getIdGrado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Asignaturagrado> asignaturagradoCollectionOrphanCheck = grado.getAsignaturagradoCollection();
            for (Asignaturagrado asignaturagradoCollectionOrphanCheckAsignaturagrado : asignaturagradoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grado (" + grado + ") cannot be destroyed since the Asignaturagrado " + asignaturagradoCollectionOrphanCheckAsignaturagrado + " in its asignaturagradoCollection field has a non-nullable grado field.");
            }
            Collection<Cursoestudiante> cursoestudianteCollectionOrphanCheck = grado.getCursoestudianteCollection();
            for (Cursoestudiante cursoestudianteCollectionOrphanCheckCursoestudiante : cursoestudianteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grado (" + grado + ") cannot be destroyed since the Cursoestudiante " + cursoestudianteCollectionOrphanCheckCursoestudiante + " in its cursoestudianteCollection field has a non-nullable idgrado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(grado);
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

    public List<Grado> findGradoEntities() {
        return findGradoEntities(true, -1, -1);
    }

    public List<Grado> findGradoEntities(int maxResults, int firstResult) {
        return findGradoEntities(false, maxResults, firstResult);
    }

    private List<Grado> findGradoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grado.class));
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

    public Grado findGrado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grado.class, id);
        } finally {
            em.close();
        }
    }

    public int getGradoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grado> rt = cq.from(Grado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
