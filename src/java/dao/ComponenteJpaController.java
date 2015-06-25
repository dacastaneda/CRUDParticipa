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
import entidades.Componente;
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
public class ComponenteJpaController implements Serializable {

    public ComponenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Componente componente) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (componente.getLogroCollection() == null) {
            componente.setLogroCollection(new ArrayList<Logro>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Asignaturagrado asignaturaGrado = componente.getAsignaturaGrado();
            if (asignaturaGrado != null) {
                asignaturaGrado = em.getReference(asignaturaGrado.getClass(), asignaturaGrado.getIdAsignaturaGrado());
                componente.setAsignaturaGrado(asignaturaGrado);
            }
            Collection<Logro> attachedLogroCollection = new ArrayList<Logro>();
            for (Logro logroCollectionLogroToAttach : componente.getLogroCollection()) {
                logroCollectionLogroToAttach = em.getReference(logroCollectionLogroToAttach.getClass(), logroCollectionLogroToAttach.getIdLogro());
                attachedLogroCollection.add(logroCollectionLogroToAttach);
            }
            componente.setLogroCollection(attachedLogroCollection);
            em.persist(componente);
            if (asignaturaGrado != null) {
                asignaturaGrado.getComponenteCollection().add(componente);
                asignaturaGrado = em.merge(asignaturaGrado);
            }
            for (Logro logroCollectionLogro : componente.getLogroCollection()) {
                Componente oldIdComponenteOfLogroCollectionLogro = logroCollectionLogro.getIdComponente();
                logroCollectionLogro.setIdComponente(componente);
                logroCollectionLogro = em.merge(logroCollectionLogro);
                if (oldIdComponenteOfLogroCollectionLogro != null) {
                    oldIdComponenteOfLogroCollectionLogro.getLogroCollection().remove(logroCollectionLogro);
                    oldIdComponenteOfLogroCollectionLogro = em.merge(oldIdComponenteOfLogroCollectionLogro);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findComponente(componente.getIdComponente()) != null) {
                throw new PreexistingEntityException("Componente " + componente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Componente componente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Componente persistentComponente = em.find(Componente.class, componente.getIdComponente());
            Asignaturagrado asignaturaGradoOld = persistentComponente.getAsignaturaGrado();
            Asignaturagrado asignaturaGradoNew = componente.getAsignaturaGrado();
            Collection<Logro> logroCollectionOld = persistentComponente.getLogroCollection();
            Collection<Logro> logroCollectionNew = componente.getLogroCollection();
            List<String> illegalOrphanMessages = null;
            for (Logro logroCollectionOldLogro : logroCollectionOld) {
                if (!logroCollectionNew.contains(logroCollectionOldLogro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Logro " + logroCollectionOldLogro + " since its idComponente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (asignaturaGradoNew != null) {
                asignaturaGradoNew = em.getReference(asignaturaGradoNew.getClass(), asignaturaGradoNew.getIdAsignaturaGrado());
                componente.setAsignaturaGrado(asignaturaGradoNew);
            }
            Collection<Logro> attachedLogroCollectionNew = new ArrayList<Logro>();
            for (Logro logroCollectionNewLogroToAttach : logroCollectionNew) {
                logroCollectionNewLogroToAttach = em.getReference(logroCollectionNewLogroToAttach.getClass(), logroCollectionNewLogroToAttach.getIdLogro());
                attachedLogroCollectionNew.add(logroCollectionNewLogroToAttach);
            }
            logroCollectionNew = attachedLogroCollectionNew;
            componente.setLogroCollection(logroCollectionNew);
            componente = em.merge(componente);
            if (asignaturaGradoOld != null && !asignaturaGradoOld.equals(asignaturaGradoNew)) {
                asignaturaGradoOld.getComponenteCollection().remove(componente);
                asignaturaGradoOld = em.merge(asignaturaGradoOld);
            }
            if (asignaturaGradoNew != null && !asignaturaGradoNew.equals(asignaturaGradoOld)) {
                asignaturaGradoNew.getComponenteCollection().add(componente);
                asignaturaGradoNew = em.merge(asignaturaGradoNew);
            }
            for (Logro logroCollectionNewLogro : logroCollectionNew) {
                if (!logroCollectionOld.contains(logroCollectionNewLogro)) {
                    Componente oldIdComponenteOfLogroCollectionNewLogro = logroCollectionNewLogro.getIdComponente();
                    logroCollectionNewLogro.setIdComponente(componente);
                    logroCollectionNewLogro = em.merge(logroCollectionNewLogro);
                    if (oldIdComponenteOfLogroCollectionNewLogro != null && !oldIdComponenteOfLogroCollectionNewLogro.equals(componente)) {
                        oldIdComponenteOfLogroCollectionNewLogro.getLogroCollection().remove(logroCollectionNewLogro);
                        oldIdComponenteOfLogroCollectionNewLogro = em.merge(oldIdComponenteOfLogroCollectionNewLogro);
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
                String id = componente.getIdComponente();
                if (findComponente(id) == null) {
                    throw new NonexistentEntityException("The componente with id " + id + " no longer exists.");
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
            Componente componente;
            try {
                componente = em.getReference(Componente.class, id);
                componente.getIdComponente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The componente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Logro> logroCollectionOrphanCheck = componente.getLogroCollection();
            for (Logro logroCollectionOrphanCheckLogro : logroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Componente (" + componente + ") cannot be destroyed since the Logro " + logroCollectionOrphanCheckLogro + " in its logroCollection field has a non-nullable idComponente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Asignaturagrado asignaturaGrado = componente.getAsignaturaGrado();
            if (asignaturaGrado != null) {
                asignaturaGrado.getComponenteCollection().remove(componente);
                asignaturaGrado = em.merge(asignaturaGrado);
            }
            em.remove(componente);
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

    public List<Componente> findComponenteEntities() {
        return findComponenteEntities(true, -1, -1);
    }

    public List<Componente> findComponenteEntities(int maxResults, int firstResult) {
        return findComponenteEntities(false, maxResults, firstResult);
    }

    private List<Componente> findComponenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Componente.class));
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

    public Componente findComponente(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Componente.class, id);
        } finally {
            em.close();
        }
    }

    public int getComponenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Componente> rt = cq.from(Componente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
