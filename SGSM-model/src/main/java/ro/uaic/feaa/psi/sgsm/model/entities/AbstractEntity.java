package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Superclasă abstractă pentru toate entitățile persistente.
 *
 * Conține atributele comune: ID (cheie primară), versiune (Optimistic Locking),
 * date de creare/modificare și audit user.
 *
 * Utilizează strategia @MappedSuperclass - atributele sunt mapate direct în tabelele
 * subclaselor fără a crea o tabelă separată.
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = -4803471783122679780L;

    /**
     * Identificator unic (PRIMARY KEY).
     * Generat automat de baza de date folosind secvență.
     * NU trebuie modificat după crearea obiecului.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    /**
     * Versiune pentru Optimistic Locking.
     * Incrementată automat la fiecare UPDATE.
     * Previne conflictele de concurență în aplicații multi-user.
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * Utilizatorul care a creat înregistrarea.
     */
    @Column(name = "created_by_user", length = 100)
    private String createdByUser;

    /**
     * Utilizatorul care a modificat înregistrarea ultima oară.
     */
    @Column(name = "updated_by_user", length = 100)
    private String updatedByUser;

    /**
     * Tip entitate (pentru implementarea moștenirii cu SingleTable strategy).
     * Stocat pentru debugging și audit.
     */
    @Column(name = "entity_type", length = 100)
    private String entity_type = this.getClass().getSimpleName();

    /**
     * Data și ora creării înregistrării.
     * Setată automat la INSERT.
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "date_created", nullable = false)
    private Date dateCreated = Calendar.getInstance().getTime();

    /**
     * Data și ora ultimei modificări.
     * Actualizată automat la UPDATE.
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "date_updated", nullable = false)
    private Date dateUpdated = Calendar.getInstance().getTime();

    /**
     * Constructor implicit (OBLIGATORIU pentru JPA).
     */
    public AbstractEntity() {
        super();
    }

    /**
     * Constructor cu ID.
     * Utilizat rareori - ID-ul se generează automat.
     *
     * @param id identificatorul entității
     */
    public AbstractEntity(Long id) {
        this();
        this.id = id;
    }

    /**
     * Implementare corectă a equals() bazată pe ID.
     *
     * Reguli:
     * - Dacă ID este null (obiect nou, nesalvat), folosesc equals default
     * - Dacă ID este setat, compar doar pe baza ID-ului
     * - Doi obiecte cu același ID sunt considerați egali
     *
     * IMPORTANT: Această implementare este critică pentru colecțiile Set și operațiile
     * de comparație în baza de date.
     *
     * @param obj obiectul de comparat
     * @return true dacă obiectele sunt egale
     */
    @Override
    public boolean equals(Object obj) {
        if (id == null)
            return super.equals(obj);
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractEntity other = (AbstractEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    /**
     * Implementare corectă a hashCode() bazată pe ID.
     *
     * Reguli:
     * - Dacă ID este null (obiect nou), folosesc hashCode default
     * - Dacă ID este setat, bazez hash-ul pe ID
     * - INVARIANT: equals(x, y) && x.hashCode() == y.hashCode()
     *
     * IMPORTANT: Această implementare este critică pentru colecțiile HashMap/HashSet.
     *
     * @return codul hash al obiectului
     */
    @Override
    public int hashCode() {
        if (id == null)
            return super.hashCode();
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}