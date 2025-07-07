public class Cidade {
    private String nome;
    private double x;
    private double y;

    public Cidade(String nome, double x, double y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public String getNome() {
        return nome;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

        @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cidade cidade = (Cidade) obj;
        return nome.equals(cidade.nome); // ou comparar por ID, se houver
    }

    @Override
    public int hashCode() {
        return nome.hashCode(); // ou hash do ID
    }


    @Override
    public String toString() {
        return nome + " (" + x + ", " + y + ")";
    }

    public double distanciaPara(Cidade outra) {
        double dx = this.x - outra.x;
        double dy = this.y - outra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
