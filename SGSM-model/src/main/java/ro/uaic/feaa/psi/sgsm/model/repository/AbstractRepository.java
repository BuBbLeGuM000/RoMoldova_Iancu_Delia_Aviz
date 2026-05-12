package ro.uaic.feaa.psi.sgsm.model.repository;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Clasa abstractă de bază pentru toți repository-urile.
 *
 * Responsabilități:
 * 1. Gestionarea EntityManager (Singleton rudimentar cu member static)
 * 2. Implementarea operațiilor CRUD generalizate (create, update, delete)
 * 3. Gestionarea tranzacțiilor (beginTransaction, commitTransaction, rollbackTransaction)
 *
 * Design Pattern: Repository Pattern
 * - Ascunde implementarea tehnologiei ORM (JPA/Hibernate)
 * - Expune numai metodele în termeni de obiecte de domeniu
 * - Clienții (Controller, Test) nu trebuie să cunoască detaliile JPA
 *
 * Singleton pentru EntityManager:
 * - EntityManager este declarat static
 * - Se creează o singură instanță pe întreaga aplicație
 * - Toți repository-urile utilizează același EntityManager
 * - Prevenire conflicte de concurență în aplicații multi-user
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public abstract class AbstractRepository {

    /**
     * EntityManager static (Singleton).
     *
     * Inițializat o singură dată la prima utilizare a oricarui Repository.
     * Persistent unit name: "PSIPersistenceUnit" - trebuie să corespundă cu
     * cel definit în META-INF/persistence.xml
     *
     * IMPORTANT: EntityManager gestionează:
     * - Conexiunea la baza de date
     * - Tranzacțiile
     * - Caching-ul obiectelor (First-Level Cache)
     * - Executarea SQL pe baza adnotărilor JPA
     */
    protected static EntityManager em = Persistence.createEntityManagerFactory(
            "PSIPersistenceUnit"
    ).createEntityManager();

    /**
     * Getter pentru EntityManager (protejat).
     * Utilizat de metodele CRUD din clasa curentă și subclase.
     *
     * @return instanța EntityManager
     */
    protected EntityManager getEm() {
        return em;
    }

    /**
     * Setter pentru EntityManager (protejat).
     * Utilizat pentru testare (injecție de dependență).
     *
     * @param emParam EntityManager de setat
     */
    protected void setEm(EntityManager emParam) {
        em = emParam;
    }

    /**
     * Inițiază o tranzacție.
     *
     * OBLIGATORIU înainte de operații de modificare a datelor (create, update, delete).
     *
     * Exemplu de utilizare:
     * <pre>
     *   repo.beginTransaction();
     *   try {
     *       repo.create(entity1);
     *       repo.update(entity2);
     *       repo.commitTransaction();
     *   } catch (Exception e) {
     *       repo.rollbackTransaction();
     *   }
     * </pre>
     *
     * INVARIANT: Orice tranzacție trebuie finalizată cu commit() sau rollback().
     */
    public void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    /**
     * Validează și confirmă (commit) tranzacția.
     *
     * La apelul acestei metode:
     * 1. EntityManager generează SQL-urile pentru toate modificările
     * 2. SQL-urile se trimit la baza de date
     * 3. Baza de date le execută atomic
     * 4. Tranzacția se finalizează cu succes
     *
     * Dacă orice SQL eșuează, baza de date face rollback automat.
     */
    public void commitTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    /**
     * Anulează (rollback) tranzacția curentă.
     *
     * La apelul acestei metode:
     * 1. Toate modificările din tranzacție se anulează
     * 2. Baza de date revine la starea de înainte de beginTransaction()
     * 3. Nicio schimbare nu este salvată
     *
     * UTILIZARE: În caz de eroare sau validare eșuată.
     */
    public void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    /**
     * CREATE: Crează o nouă înregistrare în baza de date.
     *
     * Operație: INSERT SQL
     *
     * Precondițiu: Obiectul nu trebuie să aibă ID (id == null)
     *
     * Fluxul:
     * 1. EntityManager.persist(object) - marchează obiectul pentru INSERT
     * 2. La commitTransaction(), SQL INSERT se execută
     * 3. Baza de date alocă ID-ul automat
     * 4. Obiectul returnat conține noul ID
     *
     * @param object obiectul de inserat (de tip Object - suportă orice entitate)
     * @return obiectul cu ID alocat de baza de date
     */
    public Object create(Object object) {
        em.persist(object);
        return object;
    }

    /**
     * UPDATE: Actualizează o înregistrare existentă în baza de date.
     *
     * Operație: UPDATE SQL
     *
     * Precondițiu: Obiectul trebuie să aibă ID (id != null)
     *
     * Fluxul:
     * 1. EntityManager.merge(object) - marchează obiectul pentru UPDATE
     * 2. La commitTransaction(), SQL UPDATE se execută
     * 3. Doar atributele modificate sunt actualizate
     *
     * @param object obiectul de actualizat
     * @return obiectul actualizat (managedEntity)
     */
    public Object update(Object object) {
        Object managedEntity = em.merge(object);
        return managedEntity;
    }

    /**
     * DELETE: Șterge o înregistrare din baza de date.
     *
     * Operație: DELETE SQL
     *
     * Precondițiu: Obiectul trebuie să aibă ID (id != null)
     *
     * Fluxul:
     * 1. EntityManager.merge(object) - trebuie să aducă obiectul în context
     * 2. EntityManager.remove(managedEntity) - marchează pentru DELETE
     * 3. La commitTransaction(), SQL DELETE se execută
     *
     * ATENȚIE: Ștergerea unei entități care are referințe OneToMany cu
     * cascade=CascadeType.ALL va șterge și entități dependente!
     *
     * @param object obiectul de șters
     */
    public void delete(Object object) {
        Object managedEntity = em.merge(object);
        em.remove(managedEntity);
    }
}