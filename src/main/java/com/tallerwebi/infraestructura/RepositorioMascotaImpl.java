package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioMascota;
import com.tallerwebi.dominio.entidades.Mascota;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class RepositorioMascotaImpl implements RepositorioMascota {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioMascotaImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long crear(Mascota mascota) {
        Long idMascotaCreada = (Long)this.sessionFactory.getCurrentSession().save(mascota);
        return idMascotaCreada;
    }

    @Override
    public Mascota obtenerPor(Long id) {
        String hql = "FROM Mascota WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("id", id);
        return (Mascota)query.getSingleResult();
    }

    @Override
    public List<Mascota> obtenerListaDeMascotas() {
        String hql = "FROM Mascota";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return query.getResultList();
    }

    @Override
    public List<Mascota> obtenerListaDeMascotasDeUnUsuario(Long idUsuario) {
        String hql = "FROM Mascota WHERE usuario_id = :usuarioID";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("usuarioID", idUsuario);
        return query.getResultList();
    }

    @Override
    public void actualizar(Mascota mascota) {
        String hql = "UPDATE Mascota SET nombre = :nombre, energia = :energia," +
                "salud = :salud, higiene = :higiene, felicidad = :felicidad, hambre = :hambre, estaVivo = :estaVivo, estaEnfermo = :estaEnfermo, ultimaAlimentacion = :ultimaAlimentacion, ultimaHigiene = :ultimaHigiene, ultimaSiesta = :ultimaSiesta, estaAbrigada = :estaAbrigada, estaDormido = :estaDormido, tipo = :tipo WHERE id = :id ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        query.setParameter("id", mascota.getId());
        query.setParameter("nombre", mascota.getNombre());
        query.setParameter("energia", mascota.getEnergia());
        query.setParameter("salud", mascota.getSalud());
        query.setParameter("higiene", mascota.getHigiene());
        query.setParameter("felicidad", mascota.getFelicidad());
        query.setParameter("hambre", mascota.getHambre());
        query.setParameter("estaVivo", mascota.getEstaVivo());
        query.setParameter("estaEnfermo", mascota.getEstaEnfermo());
        query.setParameter("ultimaHigiene", mascota.getUltimaHigiene());
        query.setParameter("ultimaAlimentacion", mascota.getUltimaAlimentacion());
        query.setParameter("ultimaSiesta", mascota.getUltimaSiesta());
        query.setParameter("estaAbrigada" , mascota.getEstaAbrigada());
        query.setParameter("estaDormido", mascota.getEstaDormido());
        query.setParameter("tipo", mascota.getTipo());
        int cantidadDeActualizaciones = query.executeUpdate();

        if(cantidadDeActualizaciones > 1){
            // rollback
            // throw new MuchosRegistrosAfectados("Actualizo mas de uno");
        }
    }
}
