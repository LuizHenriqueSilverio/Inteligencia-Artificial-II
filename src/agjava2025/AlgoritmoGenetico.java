package agjava2025;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AlgoritmoGenetico {
    private int tamanhoCromossomo;
    private int numeroGeracoes;
    private int tamanhoPopulacao;
    private int probabilidadeMutacao;
    private int qtdCruzamentos;
    private double capacidadeMochila;

    private ArrayList<Produto> produtos = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> populacao = new ArrayList<>();

    public AlgoritmoGenetico(int nGer, int tPop, int probMut, double cap) {
        this.numeroGeracoes = nGer;
        this.tamanhoPopulacao = tPop;
        this.probabilidadeMutacao = probMut;
        this.capacidadeMochila = cap;
    }

    public void executar() {
        criarPopulacao();
        for (int i = 0; i < this.numeroGeracoes; i++) {
            System.out.println("Geração: " + i);
            mostrarPopulacao();
            operadoresGeneticos();
            novaPopulacao();
        }
        ArrayList<Integer> melhor = obterMelhorSolucao();
        mostrarMochila(melhor);
    }

    private ArrayList<Integer> obterMelhorSolucao() {
        int indiceMelhor = obterMelhor();
        ArrayList<Integer> melhor = populacao.get(indiceMelhor);
        System.out.println("Melhor solucao: " + melhor);
        System.out.println("Avaliação do melhor: " + fitness(melhor));
        return melhor;
    }

    public void mostrarMochila(ArrayList<Integer> resultado) {
        System.out.println("Avaliacao do Melhor:" + this.fitness(resultado));
        // percorrer a solução e mostrar os produtos
        System.out.println("Produtos levados na mochilha:");
        for (int i = 0; i < resultado.size(); i++) {
            int leva = resultado.get(i);
            if (leva == 1) {
                Produto p;
                p = produtos.get(i);
                System.out.println(p.getDescricao()
                        + " Valor:" + p.getValor() + " Peso:" + p.getPeso());
            }// fim if
        }// fim for

    }

    public void carregaArquivo(String fileName) {
        String line = "";
        String[] produto = null;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                produto = line.split(",");
                Produto novoProduto = new Produto();
                novoProduto.setDescricao(produto[0]);
                novoProduto.setPeso(Double.parseDouble(produto[1]));
                novoProduto.setValor(Double.parseDouble(produto[2]));
                produtos.add(novoProduto);
            }
            this.tamanhoCromossomo = this.produtos.size();
            System.out.println("Total de produtos carregados: " + produtos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> criarCromossomo() {
        ArrayList<Integer> cromosssomo = new ArrayList<>();
        for (int i = 0; i < tamanhoCromossomo; i++) {
            if (Math.random() < 0.6) {
                cromosssomo.add(0);
            } else {
                cromosssomo.add(1);
            }
        }

        return cromosssomo;
    }

    private void criarPopulacao() {
        for (int i = 0; i < this.tamanhoPopulacao; i++) {
            this.populacao.add(criarCromossomo());
        }
    }

    private double fitness(ArrayList<Integer> cromossomo) {
        double peso=0, beneficio=0;
        for(int i=0; i< this.tamanhoCromossomo;i++){
            int leva = cromossomo.get(i);
            if(leva==1){
                Produto p = produtos.get(i);
                peso+= p.getPeso();
                beneficio+= p.getValor();
            }
        }
        if(peso<=this.capacidadeMochila) {
            return beneficio;
        }
        else {
            return 0;    
        }
    }

    private int roleta() {
        ArrayList roletaVirtual = new ArrayList();
        for (int i = 0; i < this.tamanhoPopulacao; i++) {
            double nota = fitness(populacao.get(i));
            for (int j = 0; j <= nota; j++) {
                roletaVirtual.add(i);
            }
        }// fim for
        Random r = new Random();
        int sorteado = r.nextInt(roletaVirtual.size());
        return (int) roletaVirtual.get(sorteado);
    }

    private ArrayList cruzamento() {
        ArrayList<Integer> filho1 = new ArrayList();
        ArrayList<Integer> filho2 = new ArrayList();
        ArrayList<ArrayList> filhos = new ArrayList();
        ArrayList<Integer> p1, p2;
        int ip1, ip2;
        ip1 = roleta();
        ip2 = roleta();
        p1 = populacao.get(ip1);
        p2 = populacao.get(ip2);
        Random r = new Random();
        int pos = r.nextInt(this.tamanhoCromossomo); // ponto de corte
        for (int i = 0; i <= pos; i++) {
            filho1.add(p1.get(i));
            filho2.add(p2.get(i));
        }
        for (int i = pos + 1; i < this.tamanhoCromossomo; i++) {
            filho1.add(p2.get(i));
            filho2.add(p1.get(i));
        }
        filhos.add(filho1);
        filhos.add(filho2);
        return filhos;
    }

    //----------------------------------------
    private void mutacao(ArrayList filho) {
        Random r = new Random();
        int v = r.nextInt(100);
        if (v < this.probabilidadeMutacao) {
            int ponto = r.nextInt(this.tamanhoCromossomo);
            if ((int) filho.get(ponto) == 1) {
                filho.set(ponto, 0);
            } else {
                filho.set(ponto, 1);
            }
            System.out.println("Ocorreu mutação!");
        }// fim if mutacao       

    }

    private int obterPior() {
        int indicePior = 0;
        double pior, nota = 0;
        pior = fitness((ArrayList) populacao.get(0));
        for (int i = 1; i < this.tamanhoPopulacao; i++) {
            nota = fitness((ArrayList) populacao.get(i));
            if (nota < pior) {
                pior = nota;
                indicePior = i;
            }// fim if
        }// fim for
        return indicePior;
    }// fim funcao
    //---------------------------------

    private int obterMelhor() {
        int indiceMelhor = 0;
        double melhor, nota = 0;
        melhor = fitness(populacao.get(0));
        for (int i = 1; i < this.tamanhoCromossomo; i++) {
            nota = fitness(populacao.get(i));
            if (nota > melhor) {
                melhor = nota;
                indiceMelhor = i;
            }// fim if
        }// fim for
        return indiceMelhor;
    }// fim funcao

    private void operadoresGeneticos() {
        ArrayList f1, f2;
        ArrayList<ArrayList> filhos;
        for (int i = 0; i < this.qtdCruzamentos; i++) {
            filhos = cruzamento();
            f1 = filhos.get(0);
            f2 = filhos.get(1);
            mutacao(f1);
            mutacao(f2);
            populacao.add(f1);
            populacao.add(f2);
        }
    }

    public void mostrarPopulacao() {
        for (int i = 0; i < this.tamanhoPopulacao; i++) {
            System.out.println("Cromossomo" + i + ": " + populacao.get(i));
            System.out.println("Avaliação: " + fitness(populacao.get(i)));
        }
    }

    private void novaPopulacao(){
        for(int i=0;i<this.qtdCruzamentos;i++){
            this.populacao.remove(obterPior());
            this.populacao.remove(obterPior());
        }
    }
}
