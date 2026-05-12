package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.LinkedList;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@Entity
public class Receptie extends Document{
	@ManyToOne
	DocInsotitor docInsotitor;
	@ManyToOne
	Gestiune gestiune;
	
	Boolean facturaPrimita;
	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
	private List<LinieDocument> liniiDocument = new LinkedList<>();

	public List<LinieDocument> getLiniiDocument() {
		return liniiDocument;
	}

	public void setLiniiDocument(List<LinieDocument> liniiDocument) {
		this.liniiDocument = liniiDocument;
	}
	public void addLinieDocument(LinieDocument linie) {
		this.liniiDocument.add(linie);
		linie.setDocument(this);
	}
	public DocInsotitor getDocInsotitor() {
		return docInsotitor;
	}
	public void setDocInsotitor(DocInsotitor docInsotitor) {
		this.docInsotitor = docInsotitor;
	}
	public Gestiune getGestiune() {
		return gestiune;
	}
	public void setGestiune(Gestiune gestiune) {
		this.gestiune = gestiune;
	}
	public Boolean isFacturaPrimita() {
		return facturaPrimita;
	}
	public void setFacturaPrimita(Boolean facturaPrimita) {
		this.facturaPrimita = facturaPrimita;
	}
	
	
}
