package agjava2025;
public class AGMain {

    public static void main(String[] args) {
        int numeroGeracoes = 20;
        int tamanhoPopulacao = 100;
        int probabilidadeMutacao = 5;
        int qtdCruzamentos = 10;
        double capacidadeMochila = 5;

        AlgoritmoGenetico meuAg = new AlgoritmoGenetico(numeroGeracoes, tamanhoPopulacao, probabilidadeMutacao, capacidadeMochila);
        meuAg.carregaArquivo("dados.csv");
        meuAg.executar();
        
       /**  AGBruteForce meuBruteForce = new AGBruteForce(8);
        meuBruteForce.carregaArquivo("dados.csv");
        meuBruteForce.resolver();
        meuBruteForce.mostrarMelhorSolucao();
        */
    }
    
}
