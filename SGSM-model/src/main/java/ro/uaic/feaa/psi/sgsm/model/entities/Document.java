package ro.uaic.feaa.psi.sgsm.model.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitate de bază pentru toate documentele de tranzacție.
 *
 * Strategie de moștenire: JOINED TABLE - se creează o tabelă Document și câte o tabelă
 * pentru fiecare subclasă (Aviz, Comanda, FacturaDeAvans, etc.)
 *
 * Relație 1:1 între Document și subclasele sale.
 *
 * Atribute comune tuturor documentelor:
 * - tipDocument: identifică tipul documentului (AVIZ, COMANDA, etc)
 * - numarDocument: identificatorul unic al documentului
 * - dataDocument: data la care s-a creat documentul
 * - dataOperare: data la care s-a efectuat operația în sistem
 *
 * @author Echipa ROMOLDOVA SRL - Proiect PSI
 * @version 1.0
 */
@Entity
@Table(name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class Document extends AbstractEntity {

	/**
	 * Tipul documentului: AVIZ, COMANDA, FACTURA_AVANS, INCASARE, etc.
	 */
	@Column(name = "tip_document", length = 50)
	private String tipDocument;

	/**
	 * Numărul documentului (identificator semantic).
	 * Exemplu: AV-2025-0312 pentru aviz, CMD-0102 pentru comandă.
	 */
	@Column(name = "numar_document", length = 50, nullable = false)
	private String numarDocument;

	/**
	 * Data la care s-a creat documentul economic.
	 * Diferit de dataCreated din AbstractEntity care este data INSERT în BD.
	 */
	@Temporal(value = TemporalType.DATE)
	@Column(name = "data_document")
	private Date dataDocument;

	/**
	 * Data și ora la care s-a efectuat operația în sistem.
	 * Utilizată pentru audit și urmărirea workflow-ului.
	 */
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "data_operare")
	private Date dataOperare;

	/**
	 * Constructor implicit (OBLIGATORIU pentru JPA).
	 */
	public Document() {
		super();
	}

	/**
	 * Constructor cu ID.
	 *
	 * @param id identificatorul documentului
	 */
	public Document(Long id) {
		super(id);
	}

	// ================= GETTERS & SETTERS =================

	public String getTipDocument() {
		return tipDocument;
	}

	public void setTipDocument(String tipDocument) {
		this.tipDocument = tipDocument;
	}

	public String getNumarDocument() {
		return numarDocument;
	}

	public void setNumarDocument(String numarDocument) {
		this.numarDocument = numarDocument;
	}

	public Date getDataDocument() {
		return dataDocument;
	}

	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
	}

	public Date getDataOperare() {
		return dataOperare;
	}

	public void setDataOperare(Date dataOperare) {
		this.dataOperare = dataOperare;
	}
}