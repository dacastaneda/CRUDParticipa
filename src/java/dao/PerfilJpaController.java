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
import entidades.Menunivel3;
import entidades.Perfil;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Perfilusuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class PerfilJpaController implements Serializable {

    public PerfilJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Perfil perfil) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (perfil.getMenunivel3Collection() == null) {
            perfil.setMenunivel3Collection(new ArrayList<Menunivel3>());
        }
        if (perfil.getPerfilusuarioCollection() == null) {
            perfil.setPerfilusuarioCollection(new ArrayList<Perfilusuario>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Menunivel3> attachedMenunivel3Collection = new ArrayList<Menunivel3>();
            for (Menunivel3 menunivel3CollectionMenunivel3ToAttach : perfil.getMenunivel3Collection()) {
                menunivel3CollectionMenunivel3ToAttach = em.getReference(menunivel3CollectionMenunivel3ToAttach.getClass(), menunivel3CollectionMenunivel3ToAttach.getIdMenuNivel3());
                attachedMenunivel3Collection.add(menunivel3CollectionMenunivel3ToAttach);
            }
            perfil.setMenunivel3Collection(attachedMenunivel3Collection);
            Collection<Perfilusuario> attachedPerfilusuarioCollection = new ArrayList<Perfilusuario>();
            for (Perfilusuario perfilusuarioCollectionPerfilusuarioToAttach : perfil.getPerfilusuarioCollection()) {
                perfilusuarioCollectionPerfilusuarioToAttach = em.getReference(perfilusuarioCollectionPerfilusuarioToAttach.getClass(), perfilusuarioCollectionPerfilusuarioToAttach.getIdPerfilUsuario());
                attachedPerfilusuarioCollection.add(perfilusuarioCollectionPerfilusuarioToAttach);
            }
            perfil.setPerfilusuarioCollection(attachedPerfilusuarioCollection);
            em.persist(perfil);
            for (Menunivel3 menunivel3CollectionMenunivel3 : perfil.getMenunivel3Collection()) {
                Perfil oldIdPerfilOfMenunivel3CollectionMenunivel3 = menunivel3CollectionMenunivel3.getIdPerfil();
                menunivel3CollectionMenunivel3.setIdPerfil(perfil);
                menunivel3CollectionMenunivel3 = em.merge(menunivel3CollectionMenunivel3);
                if (oldIdPerfilOfMenunivel3CollectionMenunivel3 != null) {
                    oldIdPerfilOfMenunivel3CollectionMenunivel3.getMenunivel3Collection().remove(menunivel3CollectionMenunivel3);
                    oldIdPerfilOfMenunivel3CollectionMenunivel3 = em.merge(oldIdPerfilOfMenunivel3CollectionMenunivel3);
                }
            }
            for (Perfilusuario perfilusuarioCollectionPerfilusuario : perfil.getPerfilusuarioCollection()) {
                Perfil oldIdPerfilOfPerfilusuarioCollectionPerfilusuario = perfilusuarioCollectionPerfilusuario.getIdPerfil();
                perfilusuarioCollectionPerfilusuario.setIdPerfil(perfil);
                perfilusuarioCollectionPerfilusuario = em.merge(perfilusuarioCollectionPerfilusuario);
                if (oldIdPerfilOfPerfilusuarioCollectionPerfilusuario != null) {
                    oldIdPerfilOfPerfilusuarioCollectionPerfilusuario.getPerfilusuarioCollection().remove(perfilusuarioCollectionPerfilusuario);
                    oldIdPerfilOfPerfilusuarioCollectionPerfilusuario = em.merge(oldIdPerfilOfPerfilusuarioCollectionPerfilusuario);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPerfil(perfil.getIdPerfil()) != null) {
                throw new PreexistingEntityException("Perfil " + perfil + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Perfil perfil) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Perfil persistentPerfil = em.find(Perfil.class, perfil.getIdPerfil());
            Collection<Menunivel3> menunivel3CollectionOld = persistentPerfil.getMenunivel3Collection();
            Collection<Menunivel3> menunivel3CollectionNew = perfil.getMenunivel3Collection();
            Collection<Perfilusuario> perfilusuarioCollectionOld = persistentPerfil.getPerfilusuarioCollection();
            Collection<Perfilusuario> perfilusuarioCollectionNew = perfil.getPerfilusuarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Menunivel3 menunivel3CollectionOldMenunivel3 : menunivel3CollectionOld) {
                if (!menunivel3CollectionNew.contains(menunivel3CollectionOldMenunivel3)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Menunivel3 " + menunivel3CollectionOldMenunivel3 + " since its idPerfil field is not nullable.");
                }
            }
            for (Perfilusuario perfilusuarioCollectionOldPerfilusuario : perfilusuarioCollectionOld) {
                if (!perfilusuarioCollectionNew.contains(perfilusuarioCollectionOldPerfilusuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Perfilusuario " + perfilusuarioCollectionOldPerfilusuario + " since its idPerfil field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Menunivel3> attachedMenunivel3CollectionNew = new ArrayList<Menunivel3>();
            for (Menunivel3 menunivel3CollectionNewMenunivel3ToAttach : menunivel3CollectionNew) {
                menunivel3CollectionNewMenunivel3ToAttach = em.getReference(menunivel3CollectionNewMenunivel3ToAttach.getClass(), menunivel3CollectionNewMenunivel3ToAttach.getIdMenuNivel3());
                attachedMenunivel3CollectionNew.add(menunivel3CollectionNewMenunivel3ToAttach);
            }
            menunivel3CollectionNew = attachedMenunivel3CollectionNew;
            perfil.setMenunivel3Collection(menunivel3CollectionNew);
            Collection<Perfilusuario> attachedPerfilusuarioCollectionNew = new ArrayList<Perfilusuario>();
            for (Perfilusuario perfilusuarioCollectionNewPerfilusuarioToAttach : perfilusuarioCollectionNew) {
                perfilusuarioCollectionNewPerfilusuarioToAttach = em.getReference(perfilusuarioCollectionNewPerfilusuarioToAttach.getClass(), perfilusuarioCollectionNewPerfilusuarioToAttach.getIdPerfilUsuario());
                attachedPerfilusuarioCollectionNew.add(perfilusuarioCollectionNewPerfilusuarioToAttach);
            }
            perfilusuarioCollectionNew = attachedPerfilusuarioCollectionNew;
            perfil.setPerfilusuarioCollection(perfilusuarioCollectionNew);
            perfil = em.merge(perfil);
            for (Menunivel3 menunivel3CollectionNewMenunivel3 : menunivel3CollectionNew) {
                if (!menunivel3CollectionOld.contains(menunivel3CollectionNewMenunivel3)) {
                    Perfil oldIdPerfilOfMenunivel3CollectionNewMenunivel3 = menunivel3CollectionNewMenunivel3.getIdPerfil();
                    menunivel3CollectionNewMenunivel3.setIdPerfil(perfil);
                    menunivel3CollectionNewMenunivel3 = em.merge(menunivel3CollectionNewMenunivel3);
                    if (oldIdPerfilOfMenunivel3CollectionNewMenunivel3 != null && !oldIdPerfilOfMenunivel3CollectionNewMenunivel3.equals(perfil)) {
                        oldIdPerfilOfMenunivel3CollectionNewMenunivel3.getMenunivel3Collection().remove(menunivel3CollectionNewMenunivel3);
                        oldIdPerfilOfMenunivel3CollectionNewMenunivel3 = em.merge(oldIdPerfilOfMenunivel3CollectionNewMenunivel3);
                    }
                }
            }
            for (Perfilusuario perfilusuarioCollectionNewPerfilusuario : perfilusuarioCollectionNew) {
                if (!perfilusuarioCollectionOld.contains(perfilusuarioCollectionNewPerfilusuario)) {
                    Perfil oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario = perfilusuarioCollectionNewPerfilusuario.getIdPerfil();
                    perfilusuarioCollectionNewPerfilusuario.setIdPerfil(perfil);
                    perfilusuarioCollectionNewPerfilusuario = em.merge(perfilusuarioCollectionNewPerfilusuario);
                    if (oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario != null && !oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario.equals(perfil)) {
                        oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario.getPerfilusuarioCollection().remove(perfilusuarioCollectionNewPerfilusuario);
                        oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario = em.merge(oldIdPerfilOfPerfilusuarioCollectionNewPerfilusuario);
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
                Integer id = perfil.getIdPerfil();
                if (findPerfil(id) == null) {
                    throw new NonexistentEntityException("The perfil with id " + id + " no longer exists.");
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
            Perfil perfil;
            try {
                perfil = em.getReference(Perfil.class, id);
                perfil.getIdPerfil();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The perfil with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Menunivel3> menunivel3CollectionOrphanCheck = perfil.getMenunivel3Collection();
            for (Menunivel3 menunivel3CollectionOrphanCheckMenunivel3 : menunivel3CollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Perfil (" + perfil + ") cannot be destroyed since the Menunivel3 " + menunivel3CollectionOrphanCheckMenunivel3 + " in its menunivel3Collection field has a non-nullable idPerfil field.");
            }
            Collection<Perfilusuario> perfilusuarioCollectionOrphanCheck = perfil.getPerfilusuarioCollection();
            for (Perfilusuario perfilusuarioCollectionOrphanCheckPerfilusuario : perfilusuarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Perfil (" + perfil + ") cannot be destroyed since the Perfilusuario " + perfilusuarioCollectionOrphanCheckPerfilusuario + " in its perfilusuarioCollection field has a non-nullable idPerfil field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(perfil);
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

    public List<Perfil> findPerfilEntities() {
        return findPerfilEntities(true, -1, -1);
    }

    public List<Perfil> findPerfilEntities(int maxResults, int firstResult) {
        return findPerfilEntities(false, maxResults, firstResult);
    }

    private List<Perfil> findPerfilEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Perfil.class));
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

    public Perfil findPerfil(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Perfil.class, id);
        } finally {
            em.close();
        }
    }

    public int getPerfilCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Perfil> rt = cq.from(Perfil.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
