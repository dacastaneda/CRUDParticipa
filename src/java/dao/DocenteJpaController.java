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
import entidades.Asignaturagrado;
import entidades.Docente;
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
public class DocenteJpaController implements Serializable {

    public DocenteJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Docente docente) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (docente.getAsignaturagradoCollection() == null) {
            docente.setAsignaturagradoCollection(new ArrayList<Asignaturagrado>());
        }
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = docente.getUsuario();
        if (usuarioOrphanCheck != null) {
            Docente oldDocenteOfUsuario = usuarioOrphanCheck.getDocente();
            if (oldDocenteOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type Docente whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario = docente.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdPersona());
                docente.setUsuario(usuario);
            }
            Collection<Asignaturagrado> attachedAsignaturagradoCollection = new ArrayList<Asignaturagrado>();
            for (Asignaturagrado asignaturagradoCollectionAsignaturagradoToAttach : docente.getAsignaturagradoCollection()) {
                asignaturagradoCollectionAsignaturagradoToAttach = em.getReference(asignaturagradoCollectionAsignaturagradoToAttach.getClass(), asignaturagradoCollectionAsignaturagradoToAttach.getIdAsignaturaGrado());
                attachedAsignaturagradoCollection.add(asignaturagradoCollectionAsignaturagradoToAttach);
            }
            docente.setAsignaturagradoCollection(attachedAsignaturagradoCollection);
            em.persist(docente);
            if (usuario != null) {
                usuario.setDocente(docente);
                usuario = em.merge(usuario);
            }
            for (Asignaturagrado asignaturagradoCollectionAsignaturagrado : docente.getAsignaturagradoCollection()) {
                Docente oldIdDocenteOfAsignaturagradoCollectionAsignaturagrado = asignaturagradoCollectionAsignaturagrado.getIdDocente();
                asignaturagradoCollectionAsignaturagrado.setIdDocente(docente);
                asignaturagradoCollectionAsignaturagrado = em.merge(asignaturagradoCollectionAsignaturagrado);
                if (oldIdDocenteOfAsignaturagradoCollectionAsignaturagrado != null) {
                    oldIdDocenteOfAsignaturagradoCollectionAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionAsignaturagrado);
                    oldIdDocenteOfAsignaturagradoCollectionAsignaturagrado = em.merge(oldIdDocenteOfAsignaturagradoCollectionAsignaturagrado);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDocente(docente.getIdDocente()) != null) {
                throw new PreexistingEntityException("Docente " + docente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Docente docente) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Docente persistentDocente = em.find(Docente.class, docente.getIdDocente());
            Usuario usuarioOld = persistentDocente.getUsuario();
            Usuario usuarioNew = docente.getUsuario();
            Collection<Asignaturagrado> asignaturagradoCollectionOld = persistentDocente.getAsignaturagradoCollection();
            Collection<Asignaturagrado> asignaturagradoCollectionNew = docente.getAsignaturagradoCollection();
            List<String> illegalOrphanMessages = null;
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                Docente oldDocenteOfUsuario = usuarioNew.getDocente();
                if (oldDocenteOfUsuario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type Docente whose usuario column cannot be null. Please make another selection for the usuario field.");
                }
            }
            for (Asignaturagrado asignaturagradoCollectionOldAsignaturagrado : asignaturagradoCollectionOld) {
                if (!asignaturagradoCollectionNew.contains(asignaturagradoCollectionOldAsignaturagrado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Asignaturagrado " + asignaturagradoCollectionOldAsignaturagrado + " since its idDocente field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdPersona());
                docente.setUsuario(usuarioNew);
            }
            Collection<Asignaturagrado> attachedAsignaturagradoCollectionNew = new ArrayList<Asignaturagrado>();
            for (Asignaturagrado asignaturagradoCollectionNewAsignaturagradoToAttach : asignaturagradoCollectionNew) {
                asignaturagradoCollectionNewAsignaturagradoToAttach = em.getReference(asignaturagradoCollectionNewAsignaturagradoToAttach.getClass(), asignaturagradoCollectionNewAsignaturagradoToAttach.getIdAsignaturaGrado());
                attachedAsignaturagradoCollectionNew.add(asignaturagradoCollectionNewAsignaturagradoToAttach);
            }
            asignaturagradoCollectionNew = attachedAsignaturagradoCollectionNew;
            docente.setAsignaturagradoCollection(asignaturagradoCollectionNew);
            docente = em.merge(docente);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setDocente(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setDocente(docente);
                usuarioNew = em.merge(usuarioNew);
            }
            for (Asignaturagrado asignaturagradoCollectionNewAsignaturagrado : asignaturagradoCollectionNew) {
                if (!asignaturagradoCollectionOld.contains(asignaturagradoCollectionNewAsignaturagrado)) {
                    Docente oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado = asignaturagradoCollectionNewAsignaturagrado.getIdDocente();
                    asignaturagradoCollectionNewAsignaturagrado.setIdDocente(docente);
                    asignaturagradoCollectionNewAsignaturagrado = em.merge(asignaturagradoCollectionNewAsignaturagrado);
                    if (oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado != null && !oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado.equals(docente)) {
                        oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado.getAsignaturagradoCollection().remove(asignaturagradoCollectionNewAsignaturagrado);
                        oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado = em.merge(oldIdDocenteOfAsignaturagradoCollectionNewAsignaturagrado);
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
                Integer id = docente.getIdDocente();
                if (findDocente(id) == null) {
                    throw new NonexistentEntityException("The docente with id " + id + " no longer exists.");
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
            Docente docente;
            try {
                docente = em.getReference(Docente.class, id);
                docente.getIdDocente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The docente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Asignaturagrado> asignaturagradoCollectionOrphanCheck = docente.getAsignaturagradoCollection();
            for (Asignaturagrado asignaturagradoCollectionOrphanCheckAsignaturagrado : asignaturagradoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Docente (" + docente + ") cannot be destroyed since the Asignaturagrado " + asignaturagradoCollectionOrphanCheckAsignaturagrado + " in its asignaturagradoCollection field has a non-nullable idDocente field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario usuario = docente.getUsuario();
            if (usuario != null) {
                usuario.setDocente(null);
                usuario = em.merge(usuario);
            }
            em.remove(docente);
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

    public List<Docente> findDocenteEntities() {
        return findDocenteEntities(true, -1, -1);
    }

    public List<Docente> findDocenteEntities(int maxResults, int firstResult) {
        return findDocenteEntities(false, maxResults, firstResult);
    }

    private List<Docente> findDocenteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Docente.class));
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

    public Docente findDocente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Docente.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocenteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Docente> rt = cq.from(Docente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
