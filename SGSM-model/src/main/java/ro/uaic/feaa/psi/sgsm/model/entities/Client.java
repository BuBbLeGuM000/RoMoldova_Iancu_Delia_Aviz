package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;

/**
 * Entitate Client - reprezentă un client al firmei ROMOLDOVA SRL.
 *
 * Conține informații despre clienți (distribuitori, firme de construcție, șantiere industriale).
 *
 * Atribute principale:
 * - id_client: identificator unic
 * - nume_client: denumirea societății comerciale
 * - cui_cif: Cod de Identificare Fiscală (CIF) - unic
 * - adresa: sediul social al clientului
 * - limita_credit: creditul maxim acordat clientului
 * - sold_curent: soldul datorat în prezent
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@Entity
@Table(name = "client", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cui_cif", name = "uk_client_cui")
})
public class Client extends AbstractEntity {

    /**
     * Denumirea clientului.
     * Exemplu: "SC Construct SRL", "Agregate Nord SA"
     */
    @Column(name = "nume_client", length = 160, nullable = false)
    private String numeClient;

    /**
     * Codul de Identificare Fiscală (CIF).
     * Unic pe fiecare client - nu pot exista doi clienți cu același CIF.
     * Format: RO XXXXXXXXXX
     */
    @Column(name = "cui_cif", length = 15, nullable = false, unique = true)
    private String cuiCif;

    /**
     * Adresa de sediu a clientului.
     * Utilizată pentru livrări și corespondență.
     */
    @Column(name = "adresa", length = 250)
    private String adresa;

    /**
     * Limita de credit acordată clientului.
     * Valoare maximă pe care poate cumpăra în credit.
     * Exemplu: 50000.00 lei
     */
    @Column(name = "limita_credit", precision = 12, scale = 2)
    private Double limitaCredit;

    /**
     * Soldul curent al clientului.
     * Suma datorată la un moment dat.
     * Se actualizează la fiecare factură și încasare.
     */
    @Column(name = "sold_curent", precision = 12, scale = 2)
    private Double soldCurent;

    /**
     * Constructor implicit (OBLIGATORIU pentru JPA).
     */
    public Client() {
        super();
    }

    /**
     * Constructor cu ID.
     *
     * @param id identificatorul clientului
     */
    public Client(Long id) {
        super(id);
    }

    /**
     * Constructor complet.
     *
     * @param numeClient  denumirea clientului
     * @param cuiCif      codul fiscal
     * @param adresa      adresa de sediu
     * @param limitaCredit limita de credit
     */
    public Client(String numeClient, String cuiCif, String adresa, Double limitaCredit) {
        super();
        this.numeClient = numeClient;
        this.cuiCif = cuiCif;
        this.adresa = adresa;
        this.limitaCredit = limitaCredit;
        this.soldCurent = 0.0;
    }

    // ================= GETTERS & SETTERS =================

    public String getNumeClient() {
        return numeClient;
    }

    public void setNumeClient(String numeClient) {
        this.numeClient = numeClient;
    }

    public String getCuiCif() {
        return cuiCif;
    }

    public void setCuiCif(String cuiCif) {
        this.cuiCif = cuiCif;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Double getLimitaCredit() {
        return limitaCredit;
    }

    public void setLimitaCredit(Double limitaCredit) {
        this.limitaCredit = limitaCredit;
    }

    public Double getSoldCurent() {
        return soldCurent;
    }

    public void setSoldCurent(Double soldCurent) {
        this.soldCurent = soldCurent;
    }
}