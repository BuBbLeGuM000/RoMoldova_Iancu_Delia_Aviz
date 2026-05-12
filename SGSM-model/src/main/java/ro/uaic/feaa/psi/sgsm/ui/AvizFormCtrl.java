package ro.uaic.feaa.psi.sgsm.ui;

import ro.uaic.feaa.psi.sgsm.model.entities.*;
import java.util.Date;
import java.util.List;

/**
 * Controller pentru formularul AVIZ DE ÎNSOȚIRE.
 *
 * Responsabilități:
 * 1. Gestionarea evenimentelor formularului (click pe butoane, selecții din combo)
 * 2. Aplicarea logicii de business (validări, calcule)
 * 3. Coordonarea între View (formular) și Model (AvizFormData)
 * 4. Gestionarea tranzacțiilor la salvare
 *
 * Design Pattern: MVC (Model-View-Controller)
 * - View: formularul AVIZ DE ÎNSOȚIRE (UI în Swing/JavaFX)
 * - Model: AvizFormData + entități JPA
 * - Controller: această clasă (AvizFormCtrl)
 *
 * Fluxul tipic al unui scenariu:
 * 1. Utilizator: Click "Adăugare" (buton) → avizNou()
 * 2. Utilizator: Selectare Client din dropdown → selectieClient(idClient)
 * 3. Utilizator: Selectare Comandă din dropdown → selectieComanda(idComanda)
 * 4. Utilizator: Introducere date cântar (Brut, Tara) → recalculeazaCantitateNeta()
 * 5. Utilizator: Click "Salvare" → salveazaModificariDocument()
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public class AvizFormCtrl {

    /**
     * Model-Adapter al formularului.
     * Conține:
     * - Avizul curent în editare
     * - Lista clienți, comenzi, produse (cache)
     * - Referințe la Repository-uri pentru acces la BD
     * - Metode helper (calcule)
     */
    private AvizFormData formData = new AvizFormData();

    /**
     * Constructor implicit.
     * Inițializează Model-Adapter.
     */
    public AvizFormCtrl() {
    }

    // ================= EVENIMENT: ADĂUGARE (Creare Aviz Nou) =================

    /**
     * Eveniment: CLICK pe butonul "ADĂUGARE".
     *
     * Inițializează un aviz nou, gol, pentru editare.
     * Formularul intră în mod "Adăugare" → câmpurile devin editable.
     *
     * Fluxul:
     * 1. Se creează un Aviz nou (ID = null)
     * 2. Se inițializează o Comandă nouă asociată
     * 3. Se setează data curentă a documentului
     * 4. Avizul este gata pentru a fi populat cu date
     *
     * După apelul acestei metode, utilizatorul:
     * - Selectează un client
     * - Selectează o comandă de la acel client
     * - Introduce date de cântar
     * - Apasă "Salvare"
     */
    public void avizNou() {
        Aviz aviz = new Aviz();
        aviz.setTipDocument("AVIZ");
        aviz.setDataDocument(new Date());
        aviz.setDataOperare(new Date());

        // Inițializez o comandă nouă asociată (o va selecta utilizatorul)
        aviz.setComanda(new Comanda());

        // Setez avizul ca document curent în Model
        formData.setAvizCurent(aviz);
    }

    // ================= EVENIMENT: SELECTARE CLIENT =================

    /**
     * Eveniment: SELECTARE CLIENT din combobox.
     *
     * Fluxul:
     * 1. Preia datele complete ale clientului din BD (pe baza ID-ului)
     * 2. Setează clientul în comanda asociată avizului
     * 3. Preia toate comenzile acestui client (pentru a le afișa în combobox următor)
     * 4. Afișează CUI și adresa clientului (completare automată)
     *
     * @param idClient ID-ul clientului selectat
     */
    public void selectieClient(Long idClient) {
        // Preia client complet din BD
        Client client = formData.getMasterRepo().findClientById(idClient);

        if (client != null && formData.getAvizCurent() != null) {
            // Setează clientul în aviz (via comanda)
            if (formData.getAvizCurent().getComanda() == null) {
                formData.getAvizCurent().setComanda(new Comanda());
            }
            formData.getAvizCurent().getComanda().setClient(client);

            // Preia toate comenzile acestui client
            List<Comanda> comenzi = formData.getDocRepo().findComenziByClient(idClient);
            formData.setListaComenzi(comenzi);

            // (View-ul va afișa CUI și adresa automat din client)
        }
    }

    // ================= EVENIMENT: SELECTARE COMANDĂ =================

    /**
     * Eveniment: SELECTARE COMANDĂ din combobox.
     *
     * Fluxul:
     * 1. Preia comanda completa din BD
     * 2. Setează comanda în avizul curent
     * 3. Preia și produsul din comandă
     * 4. Afișează cantitatea comandată pentru referință
     *
     * @param idComanda ID-ul comenzii selectate
     */
    public void selectieComanda(Long idComanda) {
        Comanda comanda = formData.getDocRepo().findComandaById(idComanda);

        if (comanda != null && formData.getAvizCurent() != null) {
            formData.getAvizCurent().setComanda(comanda);
            formData.getAvizCurent().setProdus(comanda.getProdus());
        }
    }

    // ================= EVENIMENT: MODIFICARE DATE CÂNTAR =================

    /**
     * Eveniment: MODIFICARE în câmpurile BRUT și TARA.
     *
     * Se apelează de View la orice schimbare în câmpurile Brut sau Tara.
     * Recalculează automat Net și Valoare.
     *
     * Utilizare din View:
     * <pre>
     *   // La focusLost din txtBrut
     *   controller.recalculeazaDateCântar(40.0, txtTara.getValue());
     *
     *   // La focusLost din txtTara
     *   controller.recalculeazaDateCântar(txtBrut.getValue(), 15.0);
     * </pre>
     *
     * @param brut greutatea brută de pe cântar
     * @param tara greutatea tara de pe cântar
     */
    public void recalculeazaDateCântar(Double brut, Double tara) {
        if (formData.getAvizCurent() != null) {
            // Recalculează net = brut - tara
            formData.recalculeazaCantitateNeta(brut, tara);

            // (View-ul va apela metode din formData pentru a afișa valori calculate)
        }
    }

    // ================= EVENIMENT: SALVARE (CRUD + Tranzacție) =================

    /**
     * Eveniment: CLICK pe butonul "SALVARE".
     *
     * Aceasta este metoda CEA MAI IMPORTANTĂ a controller-ului.
     *
     * Fluxul TRANSACTIONAL (Atomic - All or Nothing):
     * 1. BEGIN TRANSACTION
     * 2. Validări: stoc suficient?
     * 3. SALVARE avizul (INSERT sau UPDATE)
     * 4. SCĂDERE stoc din Produs
     * 5. COMMIT TRANSACTION (dacă totul a reușit)
     * 6. ROLLBACK TRANSACTION (dacă ceva a eșuat)
     *
     * INVARIANTE GARANTATE:
     * - Avizul și scăderea stocului se salvează ATOMIC (împreună)
     * - Dacă salvare eșuează, stocul NU scade
     * - Dacă scădere stoc eșuează, avizul NU se salvează
     *
     * Utilizare din View:
     * <pre>
     *   btnSalvare.addActionListener(e -> {
     *       try {
     *           controller.salveazaModificariDocument();
     *           JOptionPane.showMessageDialog(null, "Aviz salvat cu succes!");
     *       } catch (Exception ex) {
     *           JOptionPane.showMessageDialog(null, "Eroare: " + ex.getMessage());
     *       }
     *   });
     * </pre>
     *
     * @throws Exception Dacă stoc insuficient, eroare BD, etc.
     */
    public void salveazaModificariDocument() throws Exception {
        if (formData.getAvizCurent() == null) {
            throw new Exception("Nu există aviz de salvat!");
        }

        // ===== PASO 1: BEGIN TRANSACTION =====
        formData.getDocRepo().beginTransaction();

        try {
            Aviz aviz = formData.getAvizCurent();
            Produs produs = aviz.getProdus();

            // ===== PASO 2: VALIDĂRI =====

            // Validare 1: Avizul trebuie să aibă o comandă
            if (aviz.getComanda() == null || aviz.getComanda().getId() == null) {
                throw new Exception("Trebuie să selectați o comandă!");
            }

            // Validare 2: Avizul trebuie să aibă un produs
            if (produs == null || produs.getId() == null) {
                throw new Exception("Produsul nu este selectat!");
            }

            // Validare 3: Cantitate netă calculată corect
            if (aviz.getNet() == null || aviz.getNet() <= 0) {
                throw new Exception("Cantitate netă invalida! (Brut - Tara trebuie > 0)");
            }

            // Validare 4: Stoc disponibil suficient
            if (produs.getStocDisponibil() == null || produs.getStocDisponibil() < aviz.getNet()) {
                throw new Exception(
                        String.format(
                                "STOC INSUFICIENT! Disponibil: %.2f %s, Cerut: %.2f %s",
                                produs.getStocDisponibil(),
                                produs.getUm(),
                                aviz.getNet(),
                                produs.getUm()
                        )
                );
            }

            // Setare număr aviz (dacă nu e deja setat)
            if (aviz.getNumarDocument() == null || aviz.getNumarDocument().isEmpty()) {
                aviz.setNumarDocument("AV-" + System.currentTimeMillis());
            }

            // Setare dată expediere (dacă nu e deja setată)
            if (aviz.getDataExpedierii() == null) {
                aviz.setDataExpedierii(new Date());
            }

            // ===== PASO 3: SALVARE AVIZ (INSERT sau UPDATE) =====
            aviz = (Aviz) formData.getDocRepo().saveDocument(aviz);

            // ===== PASO 4: SCĂDERE STOC DIN PRODUS =====
            Double stocNou = produs.getStocDisponibil() - aviz.getNet();
            produs.setStocDisponibil(stocNou);
            formData.getMasterRepo().updateProdus(produs);

            // ===== PASO 5: COMMIT TRANSACTION =====
            formData.getDocRepo().commitTransaction();

            // Update in memoria cache
            formData.setAvizCurent(aviz);

            System.out.println("✓ Aviz salvat cu succes!");
            System.out.println("  - Număr: " + aviz.getNumarDocument());
            System.out.println("  - Cantitate: " + aviz.getNet() + " " + produs.getUm());
            System.out.println("  - Stoc rămas: " + stocNou + " " + produs.getUm());

        } catch (Exception e) {
            // ===== PASO 6: ROLLBACK (Eroare) =====
            formData.getDocRepo().rollbackTransaction();
            System.err.println("✗ Eroare la salvare: " + e.getMessage());
            throw e;
        }
    }

    // ================= GETTERS =================

    public AvizFormData getFormData() {
        return formData;
    }
}