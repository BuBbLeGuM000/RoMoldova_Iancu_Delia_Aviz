package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitate Aviz - reprezentează un Aviz de Însoțire a Mărfii (Aviz de Expeditie).
 *
 * Document care însoțește livrarea de ciment de la ROMOLDOVA SRL către client.
 * Datele de pe aviz provin din:
 * 1. Comandă (clientul, produsul)
 * 2. Tichetul de Cântar (greutatea brută, tara, neta)
 * 3. Date de transport (delegat, nr auto, etc.)
 *
 * Moștenește de la Document și conține:
 * - data_expedierii: data la care s-a expediat
 * - brut, tara, net: date de cântărire
 * - relații cu Comanda și Produs
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@Entity
@Table(name = "aviz")
public class Aviz extends Document {

    /**
     * Data și ora expedierii (livrării).
     * Preluată de pe Tichetul de Cântar.
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "data_expedierii")
    private Date dataExpedierii;

    /**
     * Greutatea brută a autocamionului (în tone).
     * Preluată de pe Tichetul de Cântar - cântărire la intrare.
     * Exemplu: 40.00 tone
     */
    @Column(name = "brut", precision = 12, scale = 3)
    private Double brut;

    /**
     * Tara autocamionului (greutatea gol în tone).
     * Preluată de pe Tichetul de Cântar - cântărire la ieșire.
     * Exemplu: 15.00 tone
     */
    @Column(name = "tara", precision = 12, scale = 3)
    private Double tara;

    /**
     * Cantitatea netă livrată (în tone).
     * Calculată automat: net = brut - tara
     * Exemplu: 40.00 - 15.00 = 25.00 tone
     *
     * CALCUL AUTOMATIC: nu se introduce manual, se calculează din brut și tara.
     */
    @Column(name = "net", precision = 12, scale = 3)
    private Double net;

    /**
     * Referință la Comanda (Many-to-One).
     * Un aviz corespunde unei comenzi (sau poate fi fără comandă în cazuri speciale).
     * O comandă poate genera mai multe avize (livrări parțiale).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_comanda", referencedColumnName = "id")
    private Comanda comanda;

    /**
     * Referință la Produs (Many-to-One).
     * Avizul este pentru un produs specific (ciment).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produs", referencedColumnName = "id")
    private Produs produs;

    /**
     * Constructor implicit (OBLIGATORIU pentru JPA).
     */
    public Aviz() {
        super();
    }

    /**
     * Constructor cu ID.
     *
     * @param id identificatorul avizului
     */
    public Aviz(Long id) {
        super(id);
    }

    /**
     * Constructor complet.
     *
     * @param numarDocument numărul avizului (ex: AV-2025-0312)
     * @param dataExpedierii data expedierii
     * @param brut greutate brută
     * @param tara greutate tara
     * @param comanda comanda asociată
     * @param produs produsul livrat
     */
    public Aviz(String numarDocument, Date dataExpedierii, Double brut, Double tara,
                Comanda comanda, Produs produs) {
        super();
        this.setNumarDocument(numarDocument);
        this.dataExpedierii = dataExpedierii;
        this.brut = brut;
        this.tara = tara;
        this.comanda = comanda;
        this.produs = produs;

        // Calcul automat net
        if (brut != null && tara != null) {
            this.net = brut - tara;
        }
    }

    /**
     * Metodă ajutătoare: recalculează cantitatea netă pe baza brut și tara.
     * IMPORTANT: se apelează OBLIGATORIU după setarea brut și tara.
     *
     * Formulă: net = brut - tara
     */
    public void recalculeazaCantitateNeta() {
        if (this.brut != null && this.tara != null) {
            this.net = this.brut - this.tara;
        }
    }

    /**
     * Metodă ajutătoare: calculează valoarea totală a livrării.
     * Formulă: valoare = net * pret_unitar
     *
     * @return valoarea totală (în lei)
     */
    public Double calculeazaValoare() {
        if (this.net != null && this.produs != null && this.produs.getPretUnitar() != null) {
            return this.net * this.produs.getPretUnitar();
        }
        return 0.0;
    }

    // ================= GETTERS & SETTERS =================

    public Date getDataExpedierii() {
        return dataExpedierii;
    }

    public void setDataExpedierii(Date dataExpedierii) {
        this.dataExpedierii = dataExpedierii;
    }

    public Double getBrut() {
        return brut;
    }

    public void setBrut(Double brut) {
        this.brut = brut;
        // Recalculează net la schimbarea brut
        this.recalculeazaCantitateNeta();
    }

    public Double getTara() {
        return tara;
    }

    public void setTara(Double tara) {
        this.tara = tara;
        // Recalculează net la schimbarea tara
        this.recalculeazaCantitateNeta();
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }
}