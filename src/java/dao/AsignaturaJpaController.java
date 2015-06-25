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
import entidades.Asignatura;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Asignaturagrado;
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
public class AsignaturaJpaController implements Serializable {

    public AsignaturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Asignatura asignatura) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (asignatura.getAsignaturagradoCollection() == null) {
            asignatura.setAsignaturagradoCollection(new ArrayList<Asignaturagrado>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Asignaturagrado> attachedAsignaturagradoCollection = new ArrayList<Asignaturagrado>();
            for (Asignaturagrado asignaturagradoCollectionAsignaturagradoToAttach : asignatura.getAsignaturagradoCollection()) {
                asignaturagradoCollectionAsignaturagradoToAttach = em.getReference(asignaturagradoCollectionAsignaturagradoToAttach.getClass(), asignaturagradoCollectionAsignaturagradoToAttach.getIdAsignaturaGrado());
                attachedAsignaturagradoCollection.add(asignaturagradoCollectionAsignaturagradoToAttach);
            }
            asignatura.setAsignaturagradoCollection(attachedAsignaturagradoCollection);
            em.persist(asignatura);
            for (Asignaturagrado asignaturagradoCollectionAsignaturagrado : asignatura.getAsignaturagradoCollection()) {
                Asignatura oldAsignaturaOfAsignaturagradoCollectionAsignaturagrado = asignaturagradoCollectionAsignaturagrado.getAsignatura();
                asignaturagradoCollectionAsignaturagrado.setAsignatura(asignatura);
                asignaturagradoCollectionAsignaturagrado = em.merge(asignaturagradoCollectionAsignaturagrado);
                if (oldAsignaturaOfAsignaturagradoCollectionAsignaturagrado != null) {
                    oldAsignaturaOfAsignaturagradoCollectionAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionAsignaturagrado);
                    oldAsignaturaOfAsignaturagradoCollectionAsignaturagrado = em.merge(oldAsignaturaOfAsignaturagradoCollectionAsignaturagrado);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAsignatura(asignatura.getIdAsignatura()) != null) {
                throw new PreexistingEntityException("Asignatura " + asignatura + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Asignatura asignatura) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Asignatura persistentAsignatura = em.find(Asignatura.class, asignatura.getIdAsignatura());
            Collection<Asignaturagrado> asignaturagradoCollectionOld = persistentAsignatura.getAsignaturagradoCollection();
            Collection<Asignaturagrado> asignaturagradoCollectionNew = asignatura.getAsignaturagradoCollection();
            List<String> illegalOrphanMessages = null;
            for (Asignaturagrado asignaturagradoCollectionOldAsignaturagrado : asignaturagradoCollectionOld) {
                if (!asignaturagradoCollectionNew.contains(asignaturagradoCollectionOldAsignaturagrado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Asignaturagrado " + asignaturagradoCollectionOldAsignaturagrado + " since its asignatura field is not nullable.");
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
            asignatura.setAsignaturagradoCollection(asignaturagradoCollectionNew);
            asignatura = em.merge(asignatura);
            for (Asignaturagrado asignaturagradoCollectionNewAsignaturagrado : asignaturagradoCollectionNew) {
                if (!asignaturagradoCollectionOld.contains(asignaturagradoCollectionNewAsignaturagrado)) {
                    Asignatura oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado = asignaturagradoCollectionNewAsignaturagrado.getAsignatura();
                    asignaturagradoCollectionNewAsignaturagrado.setAsignatura(asignatura);
                    asignaturagradoCollectionNewAsignaturagrado = em.merge(asignaturagradoCollectionNewAsignaturagrado);
                    if (oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado != null && !oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado.equals(asignatura)) {
                        oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionNewAsignaturagrado);
                        oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado = em.merge(oldAsignaturaOfAsignaturagradoCollectionNewAsignaturagrado);
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
                String id = asignatura.getIdAsignatura();
                if (findAsignatura(id) == null) {
                    throw new NonexistentEntityException("The asignatura with id " + id + " no longer exists.");
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
            Asignatura asignatura;
            try {
                asignatura = em.getReference(Asignatura.class, id);
                asignatura.getIdAsignatura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The asignatura with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Asignaturagrado> asignaturagradoCollectionOrphanCheck = asignatura.getAsignaturagradoCollection();
            for (Asignaturagrado asignaturagradoCollectionOrphanCheckAsignaturagrado : asignaturagradoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Asignatura (" + asignatura + ") cannot be destroyed since the Asignaturagrado " + asignaturagradoCollectionOrphanCheckAsignaturagrado + " in its asignaturagradoCollection field has a non-nullable asignatura field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(asignatura);
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

    public List<Asignatura> findAsignaturaEntities() {
        return findAsignaturaEntities(true, -1, -1);
    }

    public List<Asignatura> findAsignaturaEntities(int maxResults, int firstResult) {
        return findAsignaturaEntities(false, maxResults, firstResult);
    }

    private List<Asignatura> findAsignaturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Asignatura.class));
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

    public Asignatura findAsignatura(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Asignatura.class, id);
        } finally {
            em.close();
        }
    }

    public int getAsignaturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Asignatura> rt = cq.from(Asignatura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
