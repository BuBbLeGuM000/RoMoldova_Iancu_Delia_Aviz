package ro.uaic.feaa.psi.sgsm.ui;

import ro.uaic.feaa.psi.sgsm.model.entities.Aviz;
import ro.uaic.feaa.psi.sgsm.model.entities.Client;
import ro.uaic.feaa.psi.sgsm.model.entities.Comanda;
import ro.uaic.feaa.psi.sgsm.model.entities.Produs;
import ro.uaic.feaa.psi.sgsm.model.repository.DocumentRepository;
import ro.uaic.feaa.psi.sgsm.model.repository.MasterRepository;
import java.util.List;

/**
 * Model Adapter pentru formularul Aviz de Insoțire.
 * Stochează documentul curent și listele pentru combobox-uri.
 */
public class AvizFormData {

    private Aviz avizCurent;
    private List<Client> listaClienti;
    private List<Produs> listaProduse;
    private List<Comanda> listaComenzi;

    private DocumentRepository docRepo = new DocumentRepository();
    private MasterRepository masterRepo = new MasterRepository();

    public AvizFormData() {
        incarcaNomenclatoare();
    }

    /** Populează listele dropdown din BD la deschiderea formularului. */
    public void incarcaNomenclatoare() {
        listaClienti = masterRepo.findToatiClientii();
        listaProduse = masterRepo.findToateProdusele();
    }

    /** Calculează automat greutatea netă din cântar. */
    public void recalculeazaCantitateNeta(Double brut, Double tara) {
        if (avizCurent != null) {
            avizCurent.setBrut(brut);
            avizCurent.setTara(tara);
            avizCurent.setNet(brut != null && tara != null ? brut - tara : 0.0);
        }
    }

    // ================= GETTERS & SETTERS =================

    public Aviz getAvizCurent() { return avizCurent; }
    public void setAvizCurent(Aviz avizCurent) { this.avizCurent = avizCurent; }

    public List<Client> getListaClienti() { return listaClienti; }
    public void setListaClienti(List<Client> listaClienti) { this.listaClienti = listaClienti; }

    public List<Produs> getListaProduse() { return listaProduse; }
    public void setListaProduse(List<Produs> listaProduse) { this.listaProduse = listaProduse; }

    public List<Comanda> getListaComenzi() { return listaComenzi; }
    public void setListaComenzi(List<Comanda> listaComenzi) { this.listaComenzi = listaComenzi; }

    public DocumentRepository getDocRepo() { return docRepo; }
    public MasterRepository getMasterRepo() { return masterRepo; }
}