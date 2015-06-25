/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Docente;
import entidades.Estudiante;
import java.util.ArrayList;
import java.util.Collection;
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
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws RollbackFailureException, Exception {
        if (usuario.getEstudianteCollection() == null) {
            usuario.setEstudianteCollection(new ArrayList<Estudiante>());
        }
        if (usuario.getPerfilusuarioCollection() == null) {
            usuario.setPerfilusuarioCollection(new ArrayList<Perfilusuario>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Docente docente = usuario.getDocente();
            if (docente != null) {
                docente = em.getReference(docente.getClass(), docente.getIdDocente());
                usuario.setDocente(docente);
            }
            Collection<Estudiante> attachedEstudianteCollection = new ArrayList<Estudiante>();
            for (Estudiante estudianteCollectionEstudianteToAttach : usuario.getEstudianteCollection()) {
                estudianteCollectionEstudianteToAttach = em.getReference(estudianteCollectionEstudianteToAttach.getClass(), estudianteCollectionEstudianteToAttach.getIdEstudiante());
                attachedEstudianteCollection.add(estudianteCollectionEstudianteToAttach);
            }
            usuario.setEstudianteCollection(attachedEstudianteCollection);
            Collection<Perfilusuario> attachedPerfilusuarioCollection = new ArrayList<Perfilusuario>();
            for (Perfilusuario perfilusuarioCollectionPerfilusuarioToAttach : usuario.getPerfilusuarioCollection()) {
                perfilusuarioCollectionPerfilusuarioToAttach = em.getReference(perfilusuarioCollectionPerfilusuarioToAttach.getClass(), perfilusuarioCollectionPerfilusuarioToAttach.getIdPerfilUsuario());
                attachedPerfilusuarioCollection.add(perfilusuarioCollectionPerfilusuarioToAttach);
            }
            usuario.setPerfilusuarioCollection(attachedPerfilusuarioCollection);
            em.persist(usuario);
            if (docente != null) {
                Usuario oldUsuarioOfDocente = docente.getUsuario();
                if (oldUsuarioOfDocente != null) {
                    oldUsuarioOfDocente.setDocente(null);
                    oldUsuarioOfDocente = em.merge(oldUsuarioOfDocente);
                }
                docente.setUsuario(usuario);
                docente = em.merge(docente);
            }
            for (Estudiante estudianteCollectionEstudiante : usuario.getEstudianteCollection()) {
                Usuario oldIdUsuarioOfEstudianteCollectionEstudiante = estudianteCollectionEstudiante.getIdUsuario();
                estudianteCollectionEstudiante.setIdUsuario(usuario);
                estudianteCollectionEstudiante = em.merge(estudianteCollectionEstudiante);
                if (oldIdUsuarioOfEstudianteCollectionEstudiante != null) {
                    oldIdUsuarioOfEstudianteCollectionEstudiante.getEstudianteCollection().remove(estudianteCollectionEstudiante);
                    oldIdUsuarioOfEstudianteCollectionEstudiante = em.merge(oldIdUsuarioOfEstudianteCollectionEstudiante);
                }
            }
            for (Perfilusuario perfilusuarioCollectionPerfilusuario : usuario.getPerfilusuarioCollection()) {
                Usuario oldIdPersonaOfPerfilusuarioCollectionPerfilusuario = perfilusuarioCollectionPerfilusuario.getIdPersona();
                perfilusuarioCollectionPerfilusuario.setIdPersona(usuario);
                perfilusuarioCollectionPerfilusuario = em.merge(perfilusuarioCollectionPerfilusuario);
                if (oldIdPersonaOfPerfilusuarioCollectionPerfilusuario != null) {
                    oldIdPersonaOfPerfilusuarioCollectionPerfilusuario.getPerfilusuarioCollection().remove(perfilusuarioCollectionPerfilusuario);
                    oldIdPersonaOfPerfilusuarioCollectionPerfilusuario = em.merge(oldIdPersonaOfPerfilusuarioCollectionPerfilusuario);
                }
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

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdPersona());
            Docente docenteOld = persistentUsuario.getDocente();
            Docente docenteNew = usuario.getDocente();
            Collection<Estudiante> estudianteCollectionOld = persistentUsuario.getEstudianteCollection();
            Collection<Estudiante> estudianteCollectionNew = usuario.getEstudianteCollection();
            Collection<Perfilusuario> perfilusuarioCollectionOld = persistentUsuario.getPerfilusuarioCollection();
            Collection<Perfilusuario> perfilusuarioCollectionNew = usuario.getPerfilusuarioCollection();
            List<String> illegalOrphanMessages = null;
            if (docenteOld != null && !docenteOld.equals(docenteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Docente " + docenteOld + " since its usuario field is not nullable.");
            }
            for (Estudiante estudianteCollectionOldEstudiante : estudianteCollectionOld) {
                if (!estudianteCollectionNew.contains(estudianteCollectionOldEstudiante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Estudiante " + estudianteCollectionOldEstudiante + " since its idUsuario field is not nullable.");
                }
            }
            for (Perfilusuario perfilusuarioCollectionOldPerfilusuario : perfilusuarioCollectionOld) {
                if (!perfilusuarioCollectionNew.contains(perfilusuarioCollectionOldPerfilusuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Perfilusuario " + perfilusuarioCollectionOldPerfilusuario + " since its idPersona field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (docenteNew != null) {
                docenteNew = em.getReference(docenteNew.getClass(), docenteNew.getIdDocente());
                usuario.setDocente(docenteNew);
            }
            Collection<Estudiante> attachedEstudianteCollectionNew = new ArrayList<Estudiante>();
            for (Estudiante estudianteCollectionNewEstudianteToAttach : estudianteCollectionNew) {
                estudianteCollectionNewEstudianteToAttach = em.getReference(estudianteCollectionNewEstudianteToAttach.getClass(), estudianteCollectionNewEstudianteToAttach.getIdEstudiante());
                attachedEstudianteCollectionNew.add(estudianteCollectionNewEstudianteToAttach);
            }
            estudianteCollectionNew = attachedEstudianteCollectionNew;
            usuario.setEstudianteCollection(estudianteCollectionNew);
            Collection<Perfilusuario> attachedPerfilusuarioCollectionNew = new ArrayList<Perfilusuario>();
            for (Perfilusuario perfilusuarioCollectionNewPerfilusuarioToAttach : perfilusuarioCollectionNew) {
                perfilusuarioCollectionNewPerfilusuarioToAttach = em.getReference(perfilusuarioCollectionNewPerfilusuarioToAttach.getClass(), perfilusuarioCollectionNewPerfilusuarioToAttach.getIdPerfilUsuario());
                attachedPerfilusuarioCollectionNew.add(perfilusuarioCollectionNewPerfilusuarioToAttach);
            }
            perfilusuarioCollectionNew = attachedPerfilusuarioCollectionNew;
            usuario.setPerfilusuarioCollection(perfilusuarioCollectionNew);
            usuario = em.merge(usuario);
            if (docenteNew != null && !docenteNew.equals(docenteOld)) {
                Usuario oldUsuarioOfDocente = docenteNew.getUsuario();
                if (oldUsuarioOfDocente != null) {
                    oldUsuarioOfDocente.setDocente(null);
                    oldUsuarioOfDocente = em.merge(oldUsuarioOfDocente);
                }
                docenteNew.setUsuario(usuario);
                docenteNew = em.merge(docenteNew);
            }
            for (Estudiante estudianteCollectionNewEstudiante : estudianteCollectionNew) {
                if (!estudianteCollectionOld.contains(estudianteCollectionNewEstudiante)) {
                    Usuario oldIdUsuarioOfEstudianteCollectionNewEstudiante = estudianteCollectionNewEstudiante.getIdUsuario();
                    estudianteCollectionNewEstudiante.setIdUsuario(usuario);
                    estudianteCollectionNewEstudiante = em.merge(estudianteCollectionNewEstudiante);
                    if (oldIdUsuarioOfEstudianteCollectionNewEstudiante != null && !oldIdUsuarioOfEstudianteCollectionNewEstudiante.equals(usuario)) {
                        oldIdUsuarioOfEstudianteCollectionNewEstudiante.getEstudianteCollection().remove(estudianteCollectionNewEstudiante);
                        oldIdUsuarioOfEstudianteCollectionNewEstudiante = em.merge(oldIdUsuarioOfEstudianteCollectionNewEstudiante);
                    }
                }
            }
            for (Perfilusuario perfilusuarioCollectionNewPerfilusuario : perfilusuarioCollectionNew) {
                if (!perfilusuarioCollectionOld.contains(perfilusuarioCollectionNewPerfilusuario)) {
                    Usuario oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario = perfilusuarioCollectionNewPerfilusuario.getIdPersona();
                    perfilusuarioCollectionNewPerfilusuario.setIdPersona(usuario);
                    perfilusuarioCollectionNewPerfilusuario = em.merge(perfilusuarioCollectionNewPerfilusuario);
                    if (oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario != null && !oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario.equals(usuario)) {
                        oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario.getPerfilusuarioCollection().remove(perfilusuarioCollectionNewPerfilusuario);
                        oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario = em.merge(oldIdPersonaOfPerfilusuarioCollectionNewPerfilusuario);
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
                Integer id = usuario.getIdPersona();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdPersona();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Docente docenteOrphanCheck = usuario.getDocente();
            if (docenteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Docente " + docenteOrphanCheck + " in its docente field has a non-nullable usuario field.");
            }
            Collection<Estudiante> estudianteCollectionOrphanCheck = usuario.getEstudianteCollection();
            for (Estudiante estudianteCollectionOrphanCheckEstudiante : estudianteCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Estudiante " + estudianteCollectionOrphanCheckEstudiante + " in its estudianteCollection field has a non-nullable idUsuario field.");
            }
            Collection<Perfilusuario> perfilusuarioCollectionOrphanCheck = usuario.getPerfilusuarioCollection();
            for (Perfilusuario perfilusuarioCollectionOrphanCheckPerfilusuario : perfilusuarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Perfilusuario " + perfilusuarioCollectionOrphanCheckPerfilusuario + " in its perfilusuarioCollection field has a non-nullable idPersona field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
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

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
