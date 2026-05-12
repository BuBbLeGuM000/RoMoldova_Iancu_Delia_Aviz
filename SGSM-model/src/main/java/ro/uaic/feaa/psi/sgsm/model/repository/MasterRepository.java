package ro.uaic.feaa.psi.sgsm.model.repository;

import ro.uaic.feaa.psi.sgsm.model.entities.*;
import javax.persistence.Query;
import java.util.List;

/**
 * Repository pentru entități de tip "nomenclator" (master data).
 *
 * Gestionează date de bază (statice) ale sistemului:
 * - Client: înregistrări de clienți
 * - Produs: tipuri de produse (ciment)
 * - Alte nomenclatoare (Judet, Localitate)
 *
 * Diferit de DocumentRepository care gestionează documente (Aviz, Comanda, etc.),
 * MasterRepository gestionează date de referință.
 *
 * Metodele:
 * - addClient(), findClientById(), findToatiClientii()
 * - addProdus(), findProdusByCod()
 * - etc. pentru alte nomenclatoare
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public class MasterRepository extends AbstractRepository {

	// ================= CLIENT METHODS =================

	/**
	 * Adaugă un client nou în baza de date.
	 *
	 * Delegă către create() din superclasă (INSERT).
	 *
	 * Utilizare în Controller:
	 * <pre>
	 *   Client cli = new Client("SC Construct SRL", "RO 23145678", "Str. Industriei 5", 50000.0);
	 *   masterRepo.addClient(cli);
	 * </pre>
	 *
	 * @param client clientul de adaugat
	 * @return clientul cu ID alocat
	 */
	public Client addClient(Client client) {
		return (Client) this.create(client);
	}

	/**
	 * Actualizează datele unui client existent.
	 *
	 * Delegă către update() din superclasă (UPDATE).
	 *
	 * @param client clientul de actualizat
	 * @return clientul actualizat
	 */
	public Client updateClient(Client client) {
		return (Client) this.update(client);
	}

	/**
	 * Găsește un client după ID.
	 *
	 * Operație: SELECT ... WHERE id = ?
	 *
	 * Utilizare:
	 * <pre>
	 *   Client cli = masterRepo.findClientById(123L);
	 *   if (cli != null) {
	 *       System.out.println("Client: " + cli.getNumeClient());
	 *   }
	 * </pre>
	 *
	 * @param idClient ID-ul clientului
	 * @return clientul găsit sau null dacă nu există
	 */
	public Client findClientById(Long idClient) {
		return em.find(Client.class, idClient);
	}

	/**
	 * Găsește un client după CIF (Cod de Identificare Fiscală).
	 *
	 * CIF-ul este UNIC pentru fiecare client.
	 *
	 * Utilizare:
	 * <pre>
	 *   Client cli = masterRepo.findClientByCui("RO 23145678");
	 * </pre>
	 *
	 * @param cui CIF-ul clientului
	 * @return clientul găsit sau null dacă nu există
	 */
	public Client findClientByCui(String cui) {
		Query query = em.createQuery("SELECT c FROM Client c WHERE c.cuiCif = :cui");
		query.setParameter("cui", cui);

		List<Client> result = (List<Client>) query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * Găsește toți clienții din sistem.
	 *
	 * Ordonare: alfabetic după nume.
	 *
	 * ATENȚIE: Pentru baze mari, utilizați pagination (LIMIT, OFFSET în SQL).
	 *
	 * @return listă de toți clienții
	 */
	public List<Client> findToatiClientii() {
		Query query = em.createQuery("SELECT c FROM Client c ORDER BY c.numeClient ASC");
		@SuppressWarnings("unchecked")
		List<Client> result = query.getResultList();
		return result;
	}

	/**
	 * Șterge un client din sistem.
	 *
	 * ATENȚIE: Ștergerea unui client va genera erori dacă are comenzi/avize
	 * (dependențe de cheie străină). Implementați soft delete în producție.
	 *
	 * @param client clientul de șters
	 */
	public void deleteClient(Client client) {
		this.delete(client);
	}

	// ================= PRODUS METHODS =================

	/**
	 * Adaugă un produs nou (tip de ciment) în nomenclator.
	 *
	 * Utilizare:
	 * <pre>
	 *   Produs cim = new Produs("CIM001", "Ciment Portland CEM I 42.5R", "t", 100.0, 420.0);
	 *   masterRepo.addProdus(cim);
	 * </pre>
	 *
	 * @param produs produsul de adaugat
	 * @return produsul cu ID alocat
	 */
	public Produs addProdus(Produs produs) {
		return (Produs) this.create(produs);
	}

	/**
	 * Actualizează datele unui produs existent.
	 *
	 * UTILIZARE COMUNA: Actualizare preț sau stoc.
	 *
	 * @param produs produsul de actualizat
	 * @return produsul actualizat
	 */
	public Produs updateProdus(Produs produs) {
		return (Produs) this.update(produs);
	}

	/**
	 * Găsește un produs după cod (identificator semantic).
	 *
	 * Codul produsului este UNIC (ex: CIM001, CIM002).
	 *
	 * Utilizare:
	 * <pre>
	 *   Produs cim = masterRepo.findProdusByCod("CIM001");
	 *   System.out.println("Stoc: " + cim.getStocDisponibil() + " " + cim.getUm());
	 * </pre>
	 *
	 * @param cod codul produsului
	 * @return produsul găsit sau null dacă nu există
	 */
	public Produs findProdusByCod(String cod) {
		Query query = em.createQuery("SELECT p FROM Produs p WHERE p.codProdu = :cod");
		query.setParameter("cod", cod);

		List<Produs> result = (List<Produs>) query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * Găsește un produs după ID.
	 *
	 * @param idProdus ID-ul produsului
	 * @return produsul găsit sau null dacă nu există
	 */
	public Produs findProdusById(Long idProdus) {
		return em.find(Produs.class, idProdus);
	}

	/**
	 * Găsește toți produsele din nomenclator.
	 *
	 * @return listă de toate produsele
	 */
	public List<Produs> findToareProdusele() {
		Query query = em.createQuery("SELECT p FROM Produs p ORDER BY p.denumire ASC");
		@SuppressWarnings("unchecked")
		List<Produs> result = query.getResultList();
		return result;
	}

	/**
	 * Șterge un produs din nomenclator.
	 *
	 * @param produs produsul de șters
	 */
	public void deleteProdus(Produs produs) {
		this.delete(produs);
	}
	// ================= METODE PENTRU RESTUL NOMENCLATOARELOR =================

	public List<BunMaterial> findBunuriMaterialeAll() {
		return em.createQuery("SELECT b FROM BunMaterial b", BunMaterial.class).getResultList();
	}

	public void addBunMaterial(BunMaterial bun) {
		this.create(bun);
	}

	public List<Furnizor> findFurnizoriAll() {
		return em.createQuery("SELECT f FROM Furnizor f", Furnizor.class).getResultList();
	}

	public List<Furnizor> findFurnizoriAllLight() {
		// Necesită un constructor specific în clasa Furnizor
		return em.createQuery("SELECT new ro.uaic.feaa.psi.sgsm.model.entities.Furnizor(f.id, f.cod, f.nume, f.adresa, f.CUI, f.banca, f.contBancar) FROM Furnizor f", Furnizor.class).getResultList();
	}

	public void addFurnizor(Furnizor f) {
		this.create(f);
	}

	public List<Localitate> findLocalitatiAll() {
		return em.createQuery("SELECT l FROM Localitate l", Localitate.class).getResultList();
	}

	public void addLocalitate(Localitate l) {
		this.create(l);
	}

	public void deleteLocalitate(Localitate l) {
		this.delete(l);
	}

	public List<Gestiune> findGestiuniAll() {
		return em.createQuery("SELECT g FROM Gestiune g", Gestiune.class).getResultList();
	}

	public void addGestiune(Gestiune g) {
		this.create(g);
	}
}