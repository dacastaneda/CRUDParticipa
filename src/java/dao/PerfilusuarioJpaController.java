/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Perfil;
import entidades.Perfilusuario;
import entidades.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class PerfilusuarioJpaController implements Serializable {

    public PerfilusuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Perfilusuario perfilusuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Perfil idPerfil = perfilusuario.getIdPerfil();
            if (idPerfil != null) {
                idPerfil = em.getReference(idPerfil.getClass(), idPerfil.getIdPerfil());
                perfilusuario.setIdPerfil(idPerfil);
            }
            Usuario idPersona = perfilusuario.getIdPersona();
            if (idPersona != null) {
                idPersona = em.getReference(idPersona.getClass(), idPersona.getIdPersona());
                perfilusuario.setIdPersona(idPersona);
            }
            em.persist(perfilusuario);
            if (idPerfil != null) {
                idPerfil.getPerfilusuarioCollection().add(perfilusuario);
                idPerfil = em.merge(idPerfil);
            }
            if (idPersona != null) {
                idPersona.getPerfilusuarioCollection().add(perfilusuario);
                idPersona = em.merge(idPersona);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPerfilusuario(perfilusuario.getIdPerfilUsuario()) != null) {
                throw new PreexistingEntityException("Perfilusuario " + perfilusuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Perfilusuario perfilusuario) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Perfilusuario persistentPerfilusuario = em.find(Perfilusuario.class, perfilusuario.getIdPerfilUsuario());
            Perfil idPerfilOld = persistentPerfilusuario.getIdPerfil();
            Perfil idPerfilNew = perfilusuario.getIdPerfil();
            Usuario idPersonaOld = persistentPerfilusuario.getIdPersona();
            Usuario idPersonaNew = perfilusuario.getIdPersona();
            if (idPerfilNew != null) {
                idPerfilNew = em.getReference(idPerfilNew.getClass(), idPerfilNew.getIdPerfil());
                perfilusuario.setIdPerfil(idPerfilNew);
            }
            if (idPersonaNew != null) {
                idPersonaNew = em.getReference(idPersonaNew.getClass(), idPersonaNew.getIdPersona());
                perfilusuario.setIdPersona(idPersonaNew);
            }
            perfilusuario = em.merge(perfilusuario);
            if (idPerfilOld != null && !idPerfilOld.equals(idPerfilNew)) {
                idPerfilOld.getPerfilusuarioCollection().remove(perfilusuario);
                idPerfilOld = em.merge(idPerfilOld);
            }
            if (idPerfilNew != null && !idPerfilNew.equals(idPerfilOld)) {
                idPerfilNew.getPerfilusuarioCollection().add(perfilusuario);
                idPerfilNew = em.merge(idPerfilNew);
            }
            if (idPersonaOld != null && !idPersonaOld.equals(idPersonaNew)) {
                idPersonaOld.getPerfilusuarioCollection().remove(perfilusuario);
                idPersonaOld = em.merge(idPersonaOld);
            }
            if (idPersonaNew != null && !idPersonaNew.equals(idPersonaOld)) {
                idPersonaNew.getPerfilusuarioCollection().add(perfilusuario);
                idPersonaNew = em.merge(idPersonaNew);
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
                Integer id = perfilusuario.getIdPerfilUsuario();
                if (findPerfilusuario(id) == null) {
                    throw new NonexistentEntityException("The perfilusuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Perfilusuario perfilusuario;
            try {
                perfilusuario = em.getReference(Perfilusuario.class, id);
                perfilusuario.getIdPerfilUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The perfilusuario with id " + id + " no longer exists.", enfe);
            }
            Perfil idPerfil = perfilusuario.getIdPerfil();
            if (idPerfil != null) {
                idPerfil.getPerfilusuarioCollection().remove(perfilusuario);
                idPerfil = em.merge(idPerfil);
            }
            Usuario idPersona = perfilusuario.getIdPersona();
            if (idPersona != null) {
                idPersona.getPerfilusuarioCollection().remove(perfilusuario);
                idPersona = em.merge(idPersona);
            }
            em.remove(perfilusuario);
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

    public List<Perfilusuario> findPerfilusuarioEntities() {
        return findPerfilusuarioEntities(true, -1, -1);
    }

    public List<Perfilusuario> findPerfilusuarioEntities(int maxResults, int firstResult) {
        return findPerfilusuarioEntities(false, maxResults, firstResult);
    }

    private List<Perfilusuario> findPerfilusuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Perfilusuario.class));
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

    public Perfilusuario findPerfilusuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Perfilusuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getPerfilusuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Perfilusuario> rt = cq.from(Perfilusuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
