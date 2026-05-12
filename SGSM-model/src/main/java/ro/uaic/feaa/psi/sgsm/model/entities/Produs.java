package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;

/**
 * Entitate Produs - reprezentează un tip de ciment din nomenclatorul ROMOLDOVA SRL.
 *
 * Conține informații despre produsele vândute: tipuri de ciment (CEM I, CEM II), etc.
 *
 * Atribute principale:
 * - cod_produs: codul intern al produsului (CIM001, etc.)
 * - denumire: denumirea completă a produsului
 * - um: unitatea de măsură (tone)
 * - stoc_disponibil: cantitatea disponibilă în gestiune
 * - pret_unitar: prețul pe unitate de măsură
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@Entity
@Table(name = "produs", uniqueConstraints = {
        @UniqueConstraint(columnNames = "cod_produs", name = "uk_produs_cod")
})
public class Produs extends AbstractEntity {

    /**
     * Codul produsului (identificator semantic).
     * Exemplu: CIM001 pentru "Ciment Portland CEM I 42.5R"
     * Unic în baza de date.
     */
    @Column(name = "cod_produs", length = 10, nullable = false, unique = true)
    private String codProdu;

    /**
     * Denumirea produsului.
     * Exemplu: "Ciment Portland CEM I 42.5R"
     */
    @Column(name = "denumire", length = 150, nullable = false)
    private String denumire;

    /**
     * Unitatea de măsură a produsului.
     * Pentru ciment: "tone" (t)
     */
    @Column(name = "um", length = 10)
    private String um;

    /**
     * Cantitatea disponibilă în stoc (în unități de măsură).
     * Pentru ciment: tone (t)
     * Se actualizează la fiecare aviz de expeditie.
     *
     * IMPORTANT: Trebuie validat ca >= 0
     */
    @Column(name = "stoc_disponibil", precision = 12, scale = 3)
    private Double stocDisponibil;

    /**
     * Prețul unitar al produsului (în lei pe unitate de măsură).
     * Exemplu: 420.00 lei/tonă
     *
     * Prețurile pot fi negociate per client (în tabelul Contracte),
     * dar aceasta este prețul implicit.
     */
    @Column(name = "pret_unitar", precision = 10, scale = 2)
    private Double pretUnitar;

    /**
     * Constructor implicit (OBLIGATORIU pentru JPA).
     */
    public Produs() {
        super();
    }

    /**
     * Constructor cu ID.
     *
     * @param id identificatorul produsului
     */
    public Produs(Long id) {
        super(id);
    }

    /**
     * Constructor complet.
     *
     * @param codProdu cod produs
     * @param denumire denumire produs
     * @param um unitate de măsură
     * @param stocDisponibil stoc disponibil
     * @param pretUnitar preț unitar
     */
    public Produs(String codProdu, String denumire, String um, Double stocDisponibil, Double pretUnitar) {
        super();
        this.codProdu = codProdu;
        this.denumire = denumire;
        this.um = um;
        this.stocDisponibil = stocDisponibil;
        this.pretUnitar = pretUnitar;
    }

    // ================= GETTERS & SETTERS =================

    public String getCodProdu() {
        return codProdu;
    }

    public void setCodProdu(String codProdu) {
        this.codProdu = codProdu;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getUm() {
        return um;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public Double getStocDisponibil() {
        return stocDisponibil;
    }

    public void setStocDisponibil(Double stocDisponibil) {
        this.stocDisponibil = stocDisponibil;
    }

    public Double getPretUnitar() {
        return pretUnitar;
    }

    public void setPretUnitar(Double pretUnitar) {
        this.pretUnitar = pretUnitar;
    }
}