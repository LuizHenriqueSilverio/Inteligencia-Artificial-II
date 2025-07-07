public class AGMain {

    public static void main(String[] args) {
        // Parâmetros do Algoritmo Genético
        int numeroGeracoes = 1000;
        int tamanhoPopulacao = 100;
        int probabilidadeMutacao = 10; // Em porcentagem
        int qtdCruzamentos = 20;

        AGtsp agTSP = new AGtsp(tamanhoPopulacao, probabilidadeMutacao, qtdCruzamentos, numeroGeracoes);
        agTSP.carregarCidades("cidades.csv");
        agTSP.executar();
    }
} 