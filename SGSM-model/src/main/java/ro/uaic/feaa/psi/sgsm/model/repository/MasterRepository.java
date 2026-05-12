package ro.uaic.feaa.psi.sgsm.model.repository;
import ro.uaic.feaa.psi.sgsm.model.entities.*;
import java.util.List;

/**
 * Implementeaza Repository Pattern pentru gestionarea nomenclatoarelor (Date de referinta).
 */
@SuppressWarnings("unchecked")
public class MasterRepository extends AbstractRepository {

	public Client addClient(Client client) { return (Client) this.create(client); }
	public Client updateClient(Client client) { return (Client) this.update(client); }
	public Client findClientById(Long idClient) { return em.find(Client.class, idClient); }
	public void deleteClient(Client client) { this.delete(client); }
	public List<Client> findToatiClientii() { return em.createQuery("SELECT c FROM Client c ORDER BY c.numeClient ASC").getResultList(); }

	public Client findClientByCui(String cui) {
		List<Client> result = em.createQuery("SELECT c FROM Client c WHERE c.cuiCif = :cui").setParameter("cui", cui).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	public Produs addProdus(Produs produs) { return (Produs) this.create(produs); }
	public Produs updateProdus(Produs produs) { return (Produs) this.update(produs); }
	public Produs findProdusById(Long idProdus) { return em.find(Produs.class, idProdus); }
	public void deleteProdus(Produs produs) { this.delete(produs); }
	public List<Produs> findToateProdusele() { return em.createQuery("SELECT p FROM Produs p ORDER BY p.denumire ASC").getResultList(); }

	public Produs findProdusByCod(String cod) {
		List<Produs> result = em.createQuery("SELECT p FROM Produs p WHERE p.codProdu = :cod").setParameter("cod", cod).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	public List<BunMaterial> findBunuriMaterialeAll() { return em.createQuery("SELECT b FROM BunMaterial b", BunMaterial.class).getResultList(); }
	public void addBunMaterial(BunMaterial bun) { this.create(bun); }

	public List<Furnizor> findFurnizoriAll() { return em.createQuery("SELECT f FROM Furnizor f", Furnizor.class).getResultList(); }
	public List<Furnizor> findFurnizoriAllLight() { return em.createQuery("SELECT new ro.uaic.feaa.psi.sgsm.model.entities.Furnizor(f.id, f.cod, f.nume, f.adresa, f.CUI, f.banca, f.contBancar) FROM Furnizor f", Furnizor.class).getResultList(); }
	public void addFurnizor(Furnizor f) { this.create(f); }
	public Furnizor findFurnizorById(Long idFurnizor) { return em.find(Furnizor.class, idFurnizor); }

	public List<Localitate> findLocalitatiAll() { return em.createQuery("SELECT l FROM Localitate l", Localitate.class).getResultList(); }
	public void addLocalitate(Localitate l) { this.create(l); }
	public void deleteLocalitate(Localitate l) { this.delete(l); }

	public List<Gestiune> findGestiuniAll() { return em.createQuery("SELECT g FROM Gestiune g", Gestiune.class).getResultList(); }
	public void addGestiune(Gestiune g) { this.create(g); }
}