/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Menunivel2;
import entidades.Menunivel3;
import entidades.Perfil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author dacastanedah
 */
public class Menunivel3JpaController implements Serializable {

    public Menunivel3JpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Menunivel3 menunivel3) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel2 idMenuNivel2 = menunivel3.getIdMenuNivel2();
            if (idMenuNivel2 != null) {
                idMenuNivel2 = em.getReference(idMenuNivel2.getClass(), idMenuNivel2.getIdMenuNivel2());
                menunivel3.setIdMenuNivel2(idMenuNivel2);
            }
            Perfil idPerfil = menunivel3.getIdPerfil();
            if (idPerfil != null) {
                idPerfil = em.getReference(idPerfil.getClass(), idPerfil.getIdPerfil());
                menunivel3.setIdPerfil(idPerfil);
            }
            em.persist(menunivel3);
            if (idMenuNivel2 != null) {
                idMenuNivel2.getMenunivel3Collection().add(menunivel3);
                idMenuNivel2 = em.merge(idMenuNivel2);
            }
            if (idPerfil != null) {
                idPerfil.getMenunivel3Collection().add(menunivel3);
                idPerfil = em.merge(idPerfil);
            }
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

    public void edit(Menunivel3 menunivel3) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Menunivel3 persistentMenunivel3 = em.find(Menunivel3.class, menunivel3.getIdMenuNivel3());
            Menunivel2 idMenuNivel2Old = persistentMenunivel3.getIdMenuNivel2();
            Menunivel2 idMenuNivel2New = menunivel3.getIdMenuNivel2();
            Perfil idPerfilOld = persistentMenunivel3.getIdPerfil();
            Perfil idPerfilNew = menunivel3.getIdPerfil();
            if (idMenuNivel2New != null) {
                idMenuNivel2New = em.getReference(idMenuNivel2New.getClass(), idMenuNivel2New.getIdMenuNivel2());
                menunivel3.setIdMenuNivel2(idMenuNivel2New);
            }
            if (idPerfilNew != null) {
                idPerfilNew = em.getReference(idPerfilNew.getClass(), idPerfilNew.getIdPerfil());
                menunivel3.setIdPerfil(idPerfilNew);
            }
            menunivel3 = em.merge(menunivel3);
            if (idMenuNivel2Old != null && !idMenuNivel2Old.equals(idMenuNivel2New)) {
                idMenuNivel2Old.getMenunivel3Collection().remove(menunivel3);
                idMenuNivel2Old = em.merge(idMenuNivel2Old);
            }
            if (idMenuNivel2New != null && !idMenuNivel2New.equals(idMenuNivel2Old)) {
                idMenuNivel2New.getMenunivel3Collection().add(menunivel3);
                idMenuNivel2New = em.merge(idMenuNivel2New);
            }
            if (idPerfilOld != null && !idPerfilOld.equals(idPerfilNew)) {
                idPerfilOld.getMenunivel3Collection().remove(menunivel3);
                idPerfilOld = em.merge(idPerfilOld);
            }
            if (idPerfilNew != null && !idPerfilNew.equals(idPerfilOld)) {
                idPerfilNew.getMenunivel3Collection().add(menunivel3);
                idPerfilNew = em.merge(idPerfilNew);
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
                Integer id = menunivel3.getIdMenuNivel3();
                if (findMenunivel3(id) == null) {
                    throw new NonexistentEntityException("The menunivel3 with id " + id + " no longer exists.");
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
            Menunivel3 menunivel3;
            try {
                menunivel3 = em.getReference(Menunivel3.class, id);
                menunivel3.getIdMenuNivel3();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The menunivel3 with id " + id + " no longer exists.", enfe);
            }
            Menunivel2 idMenuNivel2 = menunivel3.getIdMenuNivel2();
            if (idMenuNivel2 != null) {
                idMenuNivel2.getMenunivel3Collection().remove(menunivel3);
                idMenuNivel2 = em.merge(idMenuNivel2);
            }
            Perfil idPerfil = menunivel3.getIdPerfil();
            if (idPerfil != null) {
                idPerfil.getMenunivel3Collection().remove(menunivel3);
                idPerfil = em.merge(idPerfil);
            }
            em.remove(menunivel3);
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

    public List<Menunivel3> findMenunivel3Entities() {
        return findMenunivel3Entities(true, -1, -1);
    }

    public List<Menunivel3> findMenunivel3Entities(int maxResults, int firstResult) {
        return findMenunivel3Entities(false, maxResults, firstResult);
    }

    private List<Menunivel3> findMenunivel3Entities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Menunivel3.class));
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

    public Menunivel3 findMenunivel3(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Menunivel3.class, id);
        } finally {
            em.close();
        }
    }

    public int getMenunivel3Count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Menunivel3> rt = cq.from(Menunivel3.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
