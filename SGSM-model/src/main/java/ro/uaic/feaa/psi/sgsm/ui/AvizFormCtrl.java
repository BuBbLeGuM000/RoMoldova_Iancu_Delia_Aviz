package ro.uaic.feaa.psi.sgsm.ui;
import ro.uaic.feaa.psi.sgsm.model.entities.*;
import java.util.Date;
import java.util.List;

/**
 * Controller MVC pentru formularul Aviz de Însotire.
 * Gestioneaza fluxul UI, validarea stocurilor si tranzactiile (conform diagramei de secventa).
 */
public class AvizFormCtrl {
    private AvizFormData formData = new AvizFormData();

    public AvizFormCtrl() {}

    /** Initializeaza un document nou si pregateste modelul pentru introducerea datelor. */
    public void avizNou() {
        Aviz aviz = new Aviz();
        aviz.setTipDocument("AVIZ");
        aviz.setDataDocument(new Date());
        aviz.setDataOperare(new Date());
        aviz.setComanda(new Comanda());
        formData.setAvizCurent(aviz);
    }

    /**
     * Preia datele clientului selectat si populeaza lista de comenzi aferente din BD.
     * @param idClient ID-ul clientului
     */
    public void selectieClient(Long idClient) {
        Client client = formData.getMasterRepo().findClientById(idClient);
        if (client != null && formData.getAvizCurent() != null) {
            if (formData.getAvizCurent().getComanda() == null) formData.getAvizCurent().setComanda(new Comanda());
            formData.getAvizCurent().getComanda().setClient(client);
            formData.setListaComenzi(formData.getDocRepo().findComenziByClient(idClient));
        }
    }

    /**
     * Asociaza comanda si produsul aferent avizului curent.
     * @param idComanda ID-ul comenzii
     */
    public void selectieComanda(Long idComanda) {
        Comanda comanda = formData.getDocRepo().findComandaById(idComanda);
        if (comanda != null && formData.getAvizCurent() != null) {
            formData.getAvizCurent().setComanda(comanda);
            formData.getAvizCurent().setProdus(comanda.getProdus());
        }
    }

    /** Recalculeaza cantitatea neta la modificarea datelor de cantar (Brut/Tara). */
    public void recalculeazaDateCantar(Double brut, Double tara) {
        if (formData.getAvizCurent() != null) formData.recalculeazaCantitateNeta(brut, tara);
    }

    /**
     * Salveaza atomic avizul si actualizeaza stocul produsului (All-or-Nothing).
     * @throws Exception in caz de validare esuata sau eroare pe stratul de persistenta
     */
    public void salveazaModificariDocument() throws Exception {
        if (formData.getAvizCurent() == null) throw new Exception("Nu exista aviz de salvat!");
        formData.getDocRepo().beginTransaction();
        try {
            Aviz aviz = formData.getAvizCurent();
            Produs produs = aviz.getProdus();
            if (aviz.getComanda() == null || aviz.getComanda().getId() == null) throw new Exception("Selectati o comanda!");
            if (produs == null || produs.getId() == null) throw new Exception("Produs neselectat!");
            if (aviz.getNet() == null || aviz.getNet() <= 0) throw new Exception("Cantitate neta invalida!");
            if (produs.getStocDisponibil() == null || produs.getStocDisponibil() < aviz.getNet())
                throw new Exception(String.format("STOC INSUFICIENT! Disponibil: %.2f, Cerut: %.2f", produs.getStocDisponibil(), aviz.getNet()));
            if (aviz.getNumarDocument() == null || aviz.getNumarDocument().isEmpty()) aviz.setNumarDocument("AV-" + System.currentTimeMillis());
            if (aviz.getDataExpedierii() == null) aviz.setDataExpedierii(new Date());

            aviz = (Aviz) formData.getDocRepo().saveDocument(aviz);
            produs.setStocDisponibil(produs.getStocDisponibil() - aviz.getNet());
            formData.getMasterRepo().updateProdus(produs);

            formData.getDocRepo().commitTransaction();
            formData.setAvizCurent(aviz);
        } catch (Exception e) {
            formData.getDocRepo().rollbackTransaction();
            throw e;
        }
    }

    public AvizFormData getFormData() { return formData; }
}