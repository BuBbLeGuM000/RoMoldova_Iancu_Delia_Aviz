package ro.uaic.feaa.psi.sgsm.model.repository;

import ro.uaic.feaa.psi.sgsm.model.entities.*;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Repository pentru entități de tip Document și sub-clasele acesteia (Aviz, Comanda etc.).
 * Extinde AbstractRepository și adaugă metode specifice documentelor:
 * - saveDocument(): INSERT sau UPDATE în funcție de prezența ID-ului
 * - findComenziByClient(): interogări pe bază de client
 * - findAvizeByComanda(): interogări pe bază de comandă
 * Design Pattern: Repository Pattern
 * - Oferă o interfață high-level pentru operații cu documente
 * - Ascunde detaliile JPA/SQL de clienți
 * - Suportă polimorfism: acceptă orice Document (Aviz, Comanda, FacturaDeAvans)
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public class DocumentRepository extends AbstractRepository {

	/**
	 * Salvează un document (INSERT sau UPDATE după caz).
	 * Logică internă:
	 * - Dacă document.getId() == null → este document NOU → INSERT (create)
	 * - Dacă document.getId() != null → este document EXISTENT → UPDATE (update)
	 * Aceasta metoda delegă spre create() sau update() din super-clasă.
	 * IMPORTANT: Această metodă NU gestionează tranzacția!
	 * Clientul (Controller) trebuie să apeleze:
	 * <pre>
	 *   repo.beginTransaction();
	 *   try {
	 *       repo.saveDocument(doc);
	 *       repo.commitTransaction();
	 *   } catch (Exception e) {
	 *       repo.rollbackTransaction();
	 *   }
	 * </pre>
	 *
	 * @param document documentul de salvat (Aviz, Comanda, FacturaDeAvans etc.)
	 * @return documentul salvat cu ID alocat (în caz de INSERT)
	 */
	public Document saveDocument(Document document) {
		if (document.getId() == null) {
			// Obiect NOU - nu are ID alocat
			document = (Document) this.create(document);
		} else {
			// Obiect EXISTENT - UPDATE
			document = (Document) this.update(document);
		}
		return document;
	}

	/**
	 * Găsește toate comenzile unui anumit client.
	 * Query JPA-QL:
	 * SELECT c FROM Comanda c WHERE c.client.id = :idClient
	 * Utilizare în Controller:
	 * <pre>
	 *   List<Comanda> comenzi = docRepo.findComenziByClient(123L);
	 *   for (Comanda c : comenzi) {
	 *       System.out.println(c.getNumărDocument() + ": " + c.getCantitate());
	 *   }
	 * </pre>
	 *
	 * @param idClient ID-ul clientului
	 * @return listă de comenzi ale clientului (poate fi goală dacă nu are comenzi)
	 */
	public List<Comanda> findComenziByClient(Long idClient) {
		Query query = em.createQuery(
				"SELECT c FROM Comanda c WHERE c.client.id = :idClient ORDER BY c.dataDocument DESC"
		);
		query.setParameter("idClient", idClient);

		@SuppressWarnings("unchecked")
		List<Comanda> result = query.getResultList();
		return result;
	}

	/**
	 * Găsește toate avizele unei anumite comenzi.
	 * Query JPA-QL:
	 * SELECT a FROM Aviz a WHERE a.comanda.id = :idComanda
	 * Utilizare:
	 * <pre>
	 *   List<Aviz> avize = docRepo.findAvizeByComanda(456L);
	 *   Double totalLivrat = 0.0;
	 *   for (Aviz av : avize) {
	 *       totalLivrat += av.getNet();
	 *   }
	 * </pre>
	 * @param idComanda ID-ul comenzii
	 * @return listă de avize emise pe baza acestei comenzi
	 */
	public List<Aviz> findAvizeByComanda(Long idComanda) {
		Query query = em.createQuery(
				"SELECT a FROM Aviz a WHERE a.comanda.id = :idComanda ORDER BY a.dataExpedierii DESC"
		);
		query.setParameter("idComanda", idComanda);

		@SuppressWarnings("unchecked")
		List<Aviz> result = query.getResultList();
		return result;
	}

	/**
	 * Găsește un aviz după ID.
	 *
	 * @param idAviz ID-ul avizului
	 * @return avizul găsit sau null dacă nu există
	 */
	public Aviz findAvizById(Long idAviz) {
		return em.find(Aviz.class, idAviz);
	}

	/**
	 * Găsește o comandă după ID.
	 *
	 * @param idComanda ID-ul comenzii
	 * @return comanda găsită sau null dacă nu există
	 */
	public Comanda findComandaById(Long idComanda) {
		return em.find(Comanda.class, idComanda);
	}

	/**
	 * Găsește toate comenzile din sistem.
	 *
	 * ATENȚIE: Pentru baze mari, USE case optimizat (pagination) este recomandat.
	 *
	 * @return listă de toate comenzile
	 */
	public List<Comanda> findToateComenzi() {
		Query query = em.createQuery("SELECT c FROM Comanda c ORDER BY c.dataDocument DESC");
		@SuppressWarnings("unchecked")
		List<Comanda> result = query.getResultList();
		return result;
	}

	/**
	 * Găsește toate avizele din sistem.
	 *
	 * @return listă de toate avizele
	 */
	public List<Aviz> findToateAvize() {
		Query query = em.createQuery("SELECT a FROM Aviz a ORDER BY a.dataExpedierii DESC");
		@SuppressWarnings("unchecked")
		List<Aviz> result = query.getResultList();
		return result;
	}
	public List<Aviz> findAvizeByPeriod(Date dataPornire, Date dataSfarsit) {
		Query query = em.createQuery("SELECT a FROM Aviz a WHERE a.dataExpedierii BETWEEN :start AND :end ORDER BY a.dataExpedierii DESC");
		query.setParameter("start", dataPornire);
		query.setParameter("end", dataSfarsit);
		return query.getResultList();
	}
}