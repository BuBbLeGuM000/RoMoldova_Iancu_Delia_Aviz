package ro.uaic.feaa.psi.sgsm.model.repository;
import ro.uaic.feaa.psi.sgsm.model.entities.*;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Implementeaza Repository Pattern pentru abstractizarea operatiunilor CRUD asupra documentelor.
 */
@SuppressWarnings("unchecked")
public class DocumentRepository extends AbstractRepository {

	/** Persista documentul (INSERT sau UPDATE) folosind EntityManager. */
	public Document saveDocument(Document document) {
		return document.getId() == null ? (Document) this.create(document) : (Document) this.update(document);
	}

	public List<Comanda> findComenziByClient(Long idClient) {
		return em.createQuery("SELECT c FROM Comanda c WHERE c.client.id = :idClient ORDER BY c.dataDocument DESC")
				.setParameter("idClient", idClient).getResultList();
	}

	public List<Aviz> findAvizeByComanda(Long idComanda) {
		return em.createQuery("SELECT a FROM Aviz a WHERE a.comanda.id = :idComanda ORDER BY a.dataExpedierii DESC")
				.setParameter("idComanda", idComanda).getResultList();
	}

	public Aviz findAvizById(Long idAviz) { return em.find(Aviz.class, idAviz); }
	public Comanda findComandaById(Long idComanda) { return em.find(Comanda.class, idComanda); }
	public List<Comanda> findToateComenzi() { return em.createQuery("SELECT c FROM Comanda c ORDER BY c.dataDocument DESC").getResultList(); }
	public List<Aviz> findToateAvize() { return em.createQuery("SELECT a FROM Aviz a ORDER BY a.dataExpedierii DESC").getResultList(); }

	public List<Aviz> findAvizeByPeriod(Date dataPornire, Date dataSfarsit) {
		return em.createQuery("SELECT a FROM Aviz a WHERE a.dataExpedierii BETWEEN :start AND :end ORDER BY a.dataExpedierii DESC")
				.setParameter("start", dataPornire).setParameter("end", dataSfarsit).getResultList();
	}
}