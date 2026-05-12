package test;

import java.util.List;
import org.junit.Assert;



import ro.uaic.feaa.psi.sgsm.model.entities.Localitate;

import ro.uaic.feaa.psi.sgsm.model.repository.MasterRepository;
/**
 * Test pentru Localitate. Teste efectuate:
 * # extragere lista de localitati
 * # adaugare localitate 
 */
public class TestLocalitati {

	static MasterRepository repo = new MasterRepository();

	public static void main(String[] args) {
		List<Localitate> localitati = repo.findLocalitatiAll();
		//daca nu sunt localitati introducem cateva date de test
		repo.beginTransaction();
		for (Localitate l:localitati)
			repo.deleteLocalitate(l);
		repo.commitTransaction();

		if (localitati.size() == 0) {
			addLocalitati();
			localitati = repo.findLocalitatiAll();
		}
		//verificam daca sunt date de test (ar trebui sa existe, la acest moment)
		Assert.assertTrue("nu aveti localitati in baza de date",localitati.size()>0);
		
		for(Localitate l:localitati)
			System.out.println(l.getDenumire());
	}


	private static void addLocalitati() {
		repo.beginTransaction();
		for (int i = 1; i < 10; i++) {
			Localitate loc = new Localitate();
			loc.setCod(100 + i);
			loc.setDenumire("Localitate " + (100 + i));

			repo.addLocalitate(loc);

		}
		repo.commitTransaction();
	}
}