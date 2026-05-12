package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;

/**
 * Entitate Comanda - reprezentează o comandă de ciment de la un client.
 *
 * Moștenește de la Document și conține informații specifice comenzilor:
 * - cantitate: cantitatea comandată
 * - status: starea comenzii (Pending, Confirmat, Anulat, etc.)
 * - relații cu Client și Produs
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@Entity
@Table(name = "comanda")
public class Comanda extends Document {

    /**
     * Cantitatea comandată (în unități de măsură: tone pentru ciment).
     * Exemplu: 25.00 tone
     */
    @Column(name = "cantitate", precision = 12, scale = 3, nullable = false)
    private Double cantitate;

    /**
     * Statusul comenzii.
     * Valori posibile: Pending, Confirmat, PartialLivrat, CompletLivrat, Anulat
     * Default: "Pending"
     */
    @Column(name = "status", length = 50)
    private String status = "Pending";

    /**
     * Referință la Client (Many-to-One).
     * O comandă aparține unui singur client.
     * Un client poate avea mai multe comenzi.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client", referencedColumnName = "id")
    private Client client;

    /**
     * Referință la Produs (Many-to-One).
     * O comandă este pentru un singur produs (ciment).
     * Un produs poate apărea în mai multe comenzi.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produs", referencedColumnName = "id")
    private Produs produs;

    /**
     * Constructor implicit (OBLIGATORIU pentru JPA).
     */
    public Comanda() {
        super();
    }

    /**
     * Constructor cu ID.
     *
     * @param id identificatorul comenzii
     */
    public Comanda(Long id) {
        super(id);
    }

    /**
     * Constructor complet.
     *
     * @param numarDocument numărul comenzii (ex: CMD-0102)
     * @param cantitate cantitatea comandată
     * @param client clientul care a plasat comanda
     * @param produs produsul comandat
     */
    public Comanda(String numarDocument, Double cantitate, Client client, Produs produs) {
        super();
        this.setNumarDocument(numarDocument);
        this.cantitate = cantitate;
        this.client = client;
        this.produs = produs;
        this.status = "Pending";
    }

    // ================= GETTERS & SETTERS =================

    public Double getCantitate() {
        return cantitate;
    }

    public void setCantitate(Double cantitate) {
        this.cantitate = cantitate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }
}