import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AGtsp {

    private ArrayList<Cidade> cidades = new ArrayList<>();
    private int tamPopulacao;
    private int tamCromossomo = 0;
    private int probMutacao;
    private int qtdeCruzamentos;
    private int numeroGeracoes;
    private ArrayList<ArrayList<Cidade>> populacao = new ArrayList<>();
    private ArrayList<Integer> roletaVirtual = new ArrayList<>();

    public AGtsp(int tamPopulacao, int probMutacao, int qtdeCruzamentos, int numeroGeracoes) {
        this.tamPopulacao = tamPopulacao;
        this.probMutacao = probMutacao;
        this.qtdeCruzamentos = qtdeCruzamentos;
        this.numeroGeracoes = numeroGeracoes;
    }

    public void executar() {
        criarPopulacao();
        for (int i = 0; i < this.numeroGeracoes; i++) {
            System.out.println("Geração: " + i);
            mostraPopulacao();
            gerarRoleta();
            operadoresGeneticos();
            novaPopulacao();
        }

        int melhor = obterMelhor();
        System.out.println("\nMelhor solução encontrada:");
        mostrarRota(populacao.get(melhor));
    }

    public void carregarCidades(String arquivo) {
        String linha;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"))) {
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                String nome = dados[0].trim();
                double x = Double.parseDouble(dados[1]);
                double y = Double.parseDouble(dados[2]);
                Cidade cidade = new Cidade(nome, x, y);
                cidades.add(cidade);
            }
            this.tamCromossomo = this.cidades.size();
            System.out.println("Total de cidades carregadas: " + this.tamCromossomo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Cidade> criarCromossomo() {
        ArrayList<Cidade> cromossomo = new ArrayList<>(this.cidades);
        Collections.shuffle(cromossomo);
        return cromossomo;
    }

    private void criarPopulacao() {
        for (int i = 0; i < this.tamPopulacao; i++) {
            this.populacao.add(criarCromossomo());
        }
    }

    private void mostraPopulacao() {
        for (int i = 0; i < this.populacao.size(); i++) {
            System.out.println("Cromossomo " + i + ": " + populacao.get(i));
            System.out.println("Fitness: " + fitness(populacao.get(i)));
            System.out.println("Distância Total: " + (1 / fitness(populacao.get(i))));
        }
        System.out.println("-------------------------------");
    }

    private double calcularDistancia(Cidade a, Cidade b) {
        return a.distanciaPara(b);
    }

    private double fitness(ArrayList<Cidade> cromossomo) {
        double distanciaTotal = 0;
        for (int i = 0; i < cromossomo.size() - 1; i++) {
            distanciaTotal += calcularDistancia(cromossomo.get(i), cromossomo.get(i + 1));
        }
        distanciaTotal += calcularDistancia(cromossomo.get(cromossomo.size() - 1), cromossomo.get(0));

        return 1.0 / distanciaTotal;
    }

    private void gerarRoleta() {
        this.roletaVirtual.clear();
        double fitnessTotal = 0;
        for (ArrayList<Cidade> cromossomo : populacao) {
            fitnessTotal += fitness(cromossomo);
        }

        for (int i = 0; i < populacao.size(); i++) {
            double fitnessRelativo = fitness(populacao.get(i)) / fitnessTotal;
            int numSlots = (int) (fitnessRelativo * 1000);
            for (int j = 0; j < numSlots; j++) {
                roletaVirtual.add(i);
            }
        }
    }

    private int roleta() {
        Random r = new Random();
        if (roletaVirtual.isEmpty()) {
            return r.nextInt(tamPopulacao);
        }
        int sorteado = r.nextInt(roletaVirtual.size());
        return roletaVirtual.get(sorteado);
    }

    public ArrayList<ArrayList<Cidade>> cruzamentoPMX(ArrayList<Cidade> pai1, ArrayList<Cidade> pai2) {
        int tamanho = pai1.size();
        Random rand = new Random();

        int corte1 = rand.nextInt(tamanho);
        int corte2 = rand.nextInt(tamanho);
        if (corte1 > corte2) {
            int temp = corte1;
            corte1 = corte2;
            corte2 = temp;
        }

        ArrayList<Cidade> filho1 = new ArrayList<>(Collections.nCopies(tamanho, null));
        ArrayList<Cidade> filho2 = new ArrayList<>(Collections.nCopies(tamanho, null));

        for (int i = corte1; i <= corte2; i++) {
            filho1.set(i, pai2.get(i));
            filho2.set(i, pai1.get(i));
        }

        preencherPMX(filho1, pai1, pai2, corte1, corte2);
        preencherPMX(filho2, pai2, pai1, corte1, corte2);

        ArrayList<ArrayList<Cidade>> filhos = new ArrayList<>();
        filhos.add(filho1);
        filhos.add(filho2);
        return filhos;
    }

    private void preencherPMX(ArrayList<Cidade> filho, ArrayList<Cidade> paiOrigem, ArrayList<Cidade> paiSegmento, int corte1, int corte2) {
        for (int i = 0; i < paiOrigem.size(); i++) {
            if (i >= corte1 && i <= corte2) {
                continue;
            }
            Cidade gene = paiOrigem.get(i);
            while (filho.contains(gene)) {
                int indexNoFilho = -1;
                for(int j=corte1; j<=corte2; j++){
                    if(filho.get(j).equals(gene)){
                        indexNoFilho = j;
                        break;
                    }
                }
                
                if(indexNoFilho != -1) {
                     gene = paiOrigem.get(indexNoFilho);
                } else {
                    int idx = paiSegmento.indexOf(gene);
                    gene = paiOrigem.get(idx);
                }
            }
            filho.set(i, gene);
        }
    }


    private void mutacao(ArrayList<Cidade> cromossomo) {
        Random r = new Random();
        if (r.nextInt(100) < this.probMutacao) {
            int ponto1 = r.nextInt(this.tamCromossomo);
            int ponto2 = r.nextInt(this.tamCromossomo);
            Collections.swap(cromossomo, ponto1, ponto2);
            System.out.println("Ocorreu mutação!");
        }
    }

    private int obterMelhor() {
        int indiceMelhor = 0;
        double melhorFitness = 0;
        if (!populacao.isEmpty()) {
            melhorFitness = fitness(populacao.get(0));
        }

        for (int i = 1; i < this.populacao.size(); i++) {
            double nota = fitness(populacao.get(i));
            if (nota > melhorFitness) {
                melhorFitness = nota;
                indiceMelhor = i;
            }
        }
        return indiceMelhor;
    }

    private int obterPior() {
        int indicePior = 0;
        double piorFitness = Double.MAX_VALUE;
        if (!populacao.isEmpty()) {
            piorFitness = fitness(populacao.get(0));
        }

        for (int i = 1; i < this.populacao.size(); i++) {
            double nota = fitness(populacao.get(i));
            if (nota < piorFitness) {
                piorFitness = nota;
                indicePior = i;
            }
        }
        return indicePior;
    }

    private void novaPopulacao() {
        for (int i = 0; i < this.qtdeCruzamentos; i++) {
            if(!populacao.isEmpty()){
                this.populacao.remove(obterPior());
                this.populacao.remove(obterPior());
            }
        }
    }

    private void operadoresGeneticos() {
        for (int i = 0; i < this.qtdeCruzamentos; i++) {
            int p1_idx = roleta();
            int p2_idx = roleta();
            ArrayList<Cidade> pai1 = populacao.get(p1_idx);
            ArrayList<Cidade> pai2 = populacao.get(p2_idx);

            ArrayList<ArrayList<Cidade>> filhos = cruzamentoPMX(pai1, pai2);
            mutacao(filhos.get(0));
            mutacao(filhos.get(1));

            populacao.add(filhos.get(0));
            populacao.add(filhos.get(1));
        }
    }

    private void mostrarRota(ArrayList<Cidade> rota) {
        for (Cidade c : rota) {
            System.out.print(c.getNome() + " -> ");
        }
        System.out.println(rota.get(0).getNome());
        System.out.println("Distância Total: " + (1 / fitness(rota)));
    }
}