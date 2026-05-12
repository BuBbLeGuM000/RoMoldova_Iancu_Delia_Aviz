package test.ro.uaic.feaa.psi.sgsm.test;

import org.junit.Assert;
import ro.uaic.feaa.psi.sgsm.model.entities.*;
import ro.uaic.feaa.psi.sgsm.model.repository.DocumentRepository;
import ro.uaic.feaa.psi.sgsm.model.repository.MasterRepository;
import ro.uaic.feaa.psi.sgsm.ui.AvizFormCtrl;
import java.util.Date;

/**
 * Test JUnit pentru simularea fluxului COMPLET de vânzare și livrare în ROMOLDOVA SRL.
 *
 * Scenariu:
 * 1. Administrator creează nomenclator: Produs (Ciment cu stoc 100 tone)
 * 2. Administrator creează nomenclator: Client (SC Construct SRL)
 * 3. Utilizator plasează o comandă de 25 tone
 * 4. Utilizator introducedat date cântar: Brut 40 tone, Tara 15 tone (net = 25 tone)
 * 5. Utilizator salvează avizul
 * 6. VALIDĂRI:
 *    - Avizul primește un ID în BD (INSERT reușit)
 *    - Stocul scade de la 100 la 75 tone
 *
 * Utilizare din IntelliJ:
 * - Click dreapta pe fișier
 * - "Run 'TestFluxVanzareAviz.main()'"
 * - Rezultat: "BUILD SUCCESS" sau "BUILD FAILURE"
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
public class TestFluxVanzareAviz {

    /**
     * Main method - permite rulare ca aplicație Java (nu doar JUnit).
     *
     * Fluxul testului:
     * 1. Setup: creare entități de test (Produs, Client, Comandă)
     * 2. Inițializare Controller
     * 3. Simulare utilizator (click adăugare, selectare client, selectare comandă)
     * 4. Simulare date cântar (brut, tara)
     * 5. Simulare salvare
     * 6. Validări (assertions)
     *
     * @param args argumente linie de comandă (nu se folosesc)
     */
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("TEST: Flux Vânzare și Livrare - AVIZ");
            System.out.println("========================================\n");

            // ===== SETUP: CREARE ENTITĂȚI DE TEST =====

            System.out.println("[1/6] SETUP: Creare Produs (Ciment)...");
            MasterRepository masterRepo = new MasterRepository();

            // Creare și salvare Produs
            Produs ciment = new Produs(
                    "CIM001",                                    // Cod
                    "Ciment Portland CEM I 42.5R",              // Denumire
                    "t",                                         // UM (tone)
                    100.0,                                       // Stoc inițial: 100 tone
                    420.0                                        // Preț: 420 lei/tonă
            );

            masterRepo.beginTransaction();
            ciment = masterRepo.addProdus(ciment);
            masterRepo.commitTransaction();

            System.out.println("  ✓ Produs creat cu ID: " + ciment.getId());
            System.out.println("    - Stoc inițial: " + ciment.getStocDisponibil() + " tone\n");

            // ===== SETUP: CREARE CLIENT =====

            System.out.println("[2/6] SETUP: Creare Client (SC Construct SRL)...");

            Client client = new Client(
                    "SC Construct SRL",                         // Nume
                    "RO 23145678",                              // CIF
                    "Str. Industriei, nr. 5, Iași",            // Adresă
                    50000.0                                     // Limita credit: 50.000 lei
            );
            client.setSoldCurent(0.0);

            masterRepo.beginTransaction();
            client = masterRepo.addClient(client);
            masterRepo.commitTransaction();

            System.out.println("  ✓ Client creat cu ID: " + client.getId());
            System.out.println("    - CIF: " + client.getCuiCif() + "\n");

            // ===== SETUP: CREARE COMANDĂ =====

            System.out.println("[3/6] SETUP: Creare Comandă (25 tone)...");

            Comanda comanda = new Comanda(
                    "CMD-0102",                                 // Număr comandă
                    25.0,                                       // Cantitate: 25 tone
                    client,                                     // Client
                    ciment                                      // Produs
            );
            comanda.setStatus("Confirmat");
            comanda.setTipDocument("COMANDA");
            comanda.setDataDocument(new Date());
            comanda.setDataOperare(new Date());

            DocumentRepository docRepo = new DocumentRepository();
            docRepo.beginTransaction();
            comanda = (Comanda) docRepo.saveDocument(comanda);
            docRepo.commitTransaction();

            System.out.println("  ✓ Comandă creată cu ID: " + comanda.getId());
            System.out.println("    - Cantitate: " + comanda.getCantitate() + " tone\n");

            // ===== UTILIZARE CONTROLLER =====

            System.out.println("[4/6] CONTROLLER: Inițializare și creare Aviz nou...");

            AvizFormCtrl controller = new AvizFormCtrl();

            // Metoda 1: Click "Adăugare"
            controller.avizNou();
            System.out.println("  ✓ Aviz nou inițializat\n");

            // Metoda 2: Selectare Client
            System.out.println("[5/6] CONTROLLER: Selectare Client și Comandă...");
            controller.selectieClient(client.getId());
            controller.selectieComanda(comanda.getId());
            System.out.println("  ✓ Client și comandă selectate\n");

            // Metoda 3: Date cântar
            System.out.println("[6/6] CONTROLLER: Introducere date cântar...");

            Double brut = 40.0;  // Greutate brută: 40 tone
            Double tara = 15.0;  // Greutate tara: 15 tone
            // Net = 40 - 15 = 25 tone

            controller.recalculeazaDateCântar(brut, tara);
            System.out.println("  - Brut: " + brut + " tone");
            System.out.println("  - Tara: " + tara + " tone");
            System.out.println("  - Net (calculat): " +
                    controller.getFormData().getAvizCurent().getNet() + " tone\n");

            // ===== SIMULARE SALVARE =====

            System.out.println("========================================");
            System.out.println("SALVARE AVIZ (TRANSACTIONAL)...");
            System.out.println("========================================\n");

            try {
                controller.salveazaModificariDocument();
                System.out.println("\n✓ SALVARE REUȘITĂ!\n");
            } catch (Exception e) {
                System.err.println("\n✗ SALVARE EȘUATĂ: " + e.getMessage() + "\n");
                throw e;
            }

            // ===== VALIDĂRI (ASSERTIONS) =====

            System.out.println("========================================");
            System.out.println("VALIDĂRI (ASSERTIONS)...");
            System.out.println("========================================\n");

            Aviz avizSalvat = controller.getFormData().getAvizCurent();

            // Assertion 1: Avizul trebuie să aibă ID
            System.out.println("[ASSERT 1] Avizul trebuie să aibă ID (INSERT reușit)...");
            Assert.assertNotNull("Avizul ar trebui să aibă un ID după INSERT!", avizSalvat.getId());
            System.out.println("  ✓ PASS - Aviz ID: " + avizSalvat.getId() + "\n");

            // Assertion 2: Stocul trebuie să scadă corect
            System.out.println("[ASSERT 2] Stocul trebuie să scadă cu cantitatea livrată...");

            // Reîncarcă produsul din BD pentru a vedea stocul actualizat
            Produs cimentActualizat = masterRepo.findProdusById(ciment.getId());
            Double stocAsteptat = 100.0 - 25.0; // 100 - 25 = 75
            Double stocReal = cimentActualizat.getStocDisponibil();

            Assert.assertEquals(
                    "Stocul ar trebui să fie 75 tone (100 - 25 livrare)",
                    stocAsteptat,
                    stocReal,
                    0.01  // Delta: 0.01 tone (precizie 2 zecimale)
            );
            System.out.println("  ✓ PASS - Stoc inițial: 100.00, Stoc livrat: 25.00, Stoc rămas: " + stocReal + "\n");

            // Assertion 3: Cantitatea netă trebuie calculată corect
            System.out.println("[ASSERT 3] Cantitatea netă trebuie calculată: Brut - Tara...");
            Double netAsteptat = 25.0;  // 40 - 15
            Double netReal = avizSalvat.getNet();

            Assert.assertEquals(
                    "Net ar trebui să fie 25 tone (40 - 15)",
                    netAsteptat,
                    netReal,
                    0.01
            );
            System.out.println("  ✓ PASS - Net calculat corect: " + netReal + " tone\n");

            // ===== REZUMAT TEST =====

            System.out.println("========================================");
            System.out.println("✓✓✓ TOȚI TESTELE AU TRECUT! ✓✓✓");
            System.out.println("========================================\n");

            System.out.println("REZUMAT:");
            System.out.println("  1. Produs creat: " + ciment.getDenumire());
            System.out.println("  2. Client creat: " + client.getNumeClient());
            System.out.println("  3. Comandă creată: " + comanda.getNumarDocument() + " (" + comanda.getCantitate() + " tone)");
            System.out.println("  4. Aviz salvat: " + avizSalvat.getNumarDocument());
            System.out.println("  5. Stoc actualizat: " + cimentActualizat.getStocDisponibil() + " tone");
            System.out.println("\n");

        } catch (Exception e) {
            System.err.println("\n✗✗✗ TEST EȘUAT! ✗✗✗");
            System.err.println("Eroare: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}