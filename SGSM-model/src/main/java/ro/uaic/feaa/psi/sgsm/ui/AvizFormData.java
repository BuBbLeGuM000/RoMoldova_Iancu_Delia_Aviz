package ro.uaic.feaa.psi.sgsm.ui;

import ro.uaic.feaa.psi.sgsm.model.entities.*;
import ro.uaic.feaa.psi.sgsm.model.repository.DocumentRepository;
import ro.uaic.feaa.psi.sgsm.model.repository.MasterRepository;
import java.util.List;

/**
 * ModelAdapter pentru formularul AVIZ DE ÎNSOȚIRE.
 *
 * Responsabilități:
 * 1. Stocarea stării curente a formularului (avizul în editare)
 * 2. Cache-uri pentru liste dropdown (clienți, produse, etc.)
 * 3. Referințe la Repository-urile necesare (accesul la BD)
 * 4. Metode ajutătoare pentru calcule (recalculare cantitate netă, totaluri)
 *
 * Design Pattern: Adapter
 * - Adaptează modelul de domeniu (entități JPA) la nevoile interfetei grafice
 * - Formularul nu vorbește direct cu BD, ci printr-un Model
 * - Separare clară între logica UI și logica de persistență
 *
 * Design Pattern: Singleton pe metodă (getInstance din Controller)
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public class AvizFormData {

    /**
     * ZONA 0 - COMUNĂ TUTUROR FORMULARELOR
     *
     * Obiectul-țintă al formularului - documentul curent în editare.
     * În cazul nostru: un Aviz de Însoțire.
     *
     * Inițial null, se inițializează în metoda avizNou() din Controller.
     */
    private Aviz avizCurent;

    /**
     * Lista de avize obținute prin búsari/filtrări.
     * Utilizată pentru navigarea între avize în modul vizualizare.
     */
    private List<Aviz> listaAvize;

    /**
     * Referință la DocumentRepository.
     * Utilizată pentru operații CRUD pe documente (saveDocument, findAvizeByComanda, etc.).
     */
    private DocumentRepository docRepo = new DocumentRepository();

    /**
     * Referință la MasterRepository.
     * Utilizată pentru operații pe nomenclatoare (findClientById, findProdusByCod, etc.).
     */
    private MasterRepository masterRepo = new MasterRepository();

    // ================= ZONA 1 - CLIENT =================

    /**
     * Lista clienților pentru combobox "Selectare Client".
     *
     * Cache-realizare:
     * - Prima apelare: se încarcă din BD prin masterRepo.findToatiClientii()
     * - Apelările următoare: se folosește lista din cache
     *
     * Optimizare: Evităm apeluri repetate la BD pentru aceeași listă.
     */
    private List<Client> listaClienti;

    /**
     * Getter pentru lista clienților cu cache-realizare.
     *
     * @return lista clienților (din cache sau din BD)
     */
    public List<Client> getListaClienti() {
        if (this.listaClienti == null) {
            // Prima apelare - încarc din BD
            this.listaClienti = this.masterRepo.findToatiClientii();
        }
        return listaClienti;
    }

    public void setListaClienti(List<Client> listaClienti) {
        this.listaClienti = listaClienti;
    }

    /**
     * Getter pentru clientul selectat în avizul curent.
     *
     * Returnează clientul asociat comenzii din avizul curent.
     *
     * @return clientul din comanda, sau null dacă nu e selectată comandă
     */
    public Client getClientSelectat() {
        if (this.avizCurent != null && this.avizCurent.getComanda() != null) {
            return this.avizCurent.getComanda().getClient();
        }
        return null;
    }

    /**
     * Setter pentru clientul selectat.
     *
     * ATENȚIE: Pe bază de client selectat, trebuie să se încarce comenzile disponibile
     * pentru acel client. Aceasta va fi gestionată de Controller.
     *
     * @param clientSelectat clientul selectat
     */
    public void setClientSelectat(Client clientSelectat) {
        if (this.avizCurent != null && this.avizCurent.getComanda() != null) {
            this.avizCurent.getComanda().setClient(clientSelectat);
        }
    }

    // ================= ZONA 2 - COMANDĂ =================

    /**
     * Lista comenzilor disponibile pentru clientul selectat.
     * Se populează la selectarea unui client din combobox.
     */
    private List<Comanda> listaComenzi;

    public List<Comanda> getListaComenzi() {
        return listaComenzi;
    }

    public void setListaComenzi(List<Comanda> listaComenzi) {
        this.listaComenzi = listaComenzi;
    }

    /**
     * Getter pentru comanda selectată în aviz.
     *
     * @return comanda asociată avizului, sau null
     */
    public Comanda getComenASelectedatå() {
        if (this.avizCurent != null) {
            return this.avizCurent.getComanda();
        }
        return null;
    }

    /**
     * Setter pentru comanda selectată.
     *
     * @param comandaSelectata comanda de setat
     */
    public void setComenASelectata(Comanda comandaSelectata) {
        if (this.avizCurent != null) {
            this.avizCurent.setComanda(comandaSelectata);
            // Preia și produsul din comandă
            if (comandaSelectata != null) {
                this.avizCurent.setProdus(comandaSelectata.getProdus());
            }
        }
    }

    // ================= ZONA 3 - PRODUS =================

    /**
     * Lista produselor din nomenclator.
     * Cache-realizare pentru performance.
     */
    private List<Produs> listaProduse;

    public List<Produs> getListaProduse() {
        if (this.listaProduse == null) {
            this.listaProduse = this.masterRepo.findToareProdusele();
        }
        return listaProduse;
    }

    public void setListaProduse(List<Produs> listaProduse) {
        this.listaProduse = listaProduse;
    }

    /**
     * Getter pentru produsul din avizul curent.
     *
     * @return produsul livrat (ciment)
     */
    public Produs getProdusSelectat() {
        if (this.avizCurent != null) {
            return this.avizCurent.getProdus();
        }
        return null;
    }

    public void setProdusSelectat(Produs produsSelectat) {
        if (this.avizCurent != null) {
            this.avizCurent.setProdus(produsSelectat);
        }
    }

    // ================= ZONA 4 - DATE CÂNTAR (Brut, Tara, Net) =================

    /**
     * Metoda ajutătoare: Recalculează cantitatea netă pe baza brut și tara.
     *
     * FORMUL: net = brut - tara
     *
     * Se apelează din Controller la modificarea câmpurilor Brut și Tara.
     *
     * Utilizare:
     * <pre>
     *   formData.recalculeazaCantitateNeta(40.0, 15.0);
     *   // avizCurent.net devine 25.0
     * </pre>
     *
     * @param brut greutatea brută (din cântar)
     * @param tara greutatea tara (din cântar)
     */
    public void recalculeazaCantitateNeta(Double brut, Double tara) {
        if (this.avizCurent != null) {
            this.avizCurent.setBrut(brut);
            this.avizCurent.setTara(tara);
            // Setter-ul din Aviz apelează recalculeazaCantitateNeta() automat
        }
    }

    /**
     * Metoda ajutătoare: Calculează valoarea totală a livrării.
     *
     * FORMULĂ: valoare = net * pret_unitar
     *
     * @return valoarea totală (în lei)
     */
    public Double calculeazaValoare() {
        if (this.avizCurent != null) {
            return this.avizCurent.calculeazaValoare();
        }
        return 0.0;
    }

    /**
     * Metoda ajutătoare: Calculează TVA.
     *
     * FORMULĂ: tva = valoare * 0.19
     *
     * @return valoarea TVA (în lei)
     */
    public Double calculeazaTVA() {
        Double valoare = this.calculeazaValoare();
        if (valoare != null) {
            return valoare * 0.19;
        }
        return 0.0;
    }

    /**
     * Metoda ajutătoare: Calculează total de plată (inclusiv TVA).
     *
     * FORMULĂ: total = valoare + tva
     *
     * @return total de plată (în lei)
     */
    public Double calculeazaTotal() {
        Double valoare = this.calculeazaValoare();
        Double tva = this.calculeazaTVA();
        if (valoare != null && tva != null) {
            return valoare + tva;
        }
        return 0.0;
    }

    // ================= GETTERS & SETTERS PRINCIPALE =================

    public Aviz getAvizCurent() {
        return avizCurent;
    }

    public void setAvizCurent(Aviz avizCurent) {
        this.avizCurent = avizCurent;
    }

    public List<Aviz> getListaAvize() {
        return listaAvize;
    }

    public void setListaAvize(List<Aviz> listaAvize) {
        this.listaAvize = listaAvize;
    }

    public DocumentRepository getDocRepo() {
        return docRepo;
    }

    public MasterRepository getMasterRepo() {
        return masterRepo;
    }
}