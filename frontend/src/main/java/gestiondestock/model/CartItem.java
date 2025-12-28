package gestiondestock.model;

public class CartItem {
    private final Produit produit;
    private int quantite;

    public CartItem(Produit produit) {
        this.produit = produit;
        this.quantite = 1;
    }

    public CartItem(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = Math.max(0, quantite);
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = Math.max(0, quantite);
    }

    public void increment() {
        quantite++;
    }

    public void decrement() {
        if (quantite > 1) {
            quantite--;
        }
    }

    public double getTotal() {
        Double prix = produit.getPrixUnitaire();
        return (prix != null ? prix : 0.0) * quantite;
    }
}
