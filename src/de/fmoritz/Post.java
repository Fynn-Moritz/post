package de.fmoritz;

import java.util.List;

public class Post {

	private String aktenzeichen;
	private String mailText;
	private Sachbearbeiter sb;
	private Sachbearbeiter vertreter;
	private List<Seite> seiten;
	
	public List<Seite> getSeiten() {
		return seiten;
	}

	public void setSeiten(List<Seite> seiten) {
		this.seiten = seiten;
	}

	public Sachbearbeiter getVertreter() {
		return vertreter;
	}

	public void setVertreter(Sachbearbeiter vertreter) {
		this.vertreter = vertreter;
	}

	public Sachbearbeiter getSb() {
		return sb;
	}

	public void setSb(Sachbearbeiter sb) {
		this.sb = sb;
	}

	public String getAktenzeichen() {
		return aktenzeichen;
	}

	public void setAktenzeichen(String aktenzeichen) {
		this.aktenzeichen = aktenzeichen;
	}

	public String getMailText() {
		return mailText;
	}

	public void setMailText(String mailText) {
		this.mailText = mailText;
	}

	public Post(String mailtext) {
		this.mailText = mailtext;
		this.seiten = List.of(new Seite(), new Seite(), new Seite());
	}
	
	public class Seite{
	}
}
