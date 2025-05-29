import java.io.Serializable;
import java.util.*;

public class Usuario implements Serializable {
    private String nome;
    private String senhaHash;
    private String cpf;
    private String telefone;
    private String email;

    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    public Usuario(String nome, String senhaHash, String cpf, String telefone, String email) {
        this.nome = nome;
        this.senhaHash = senhaHash;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public double getSaldoTotal() {
        double saldo = 0;
        if (contaCorrente != null) saldo += contaCorrente.getSaldo();
        if (contaPoupanca != null) saldo += contaPoupanca.getSaldo();
        return saldo;
    }

    public void criarContaCorrente(String numeroConta) {
        this.contaCorrente = new ContaCorrente(numeroConta);
    }

    public void criarContaPoupanca(String numeroConta) {
        this.contaPoupanca = new ContaPoupanca(numeroConta);
    }

    public void mostrarDados() {
        System.out.println("\n📋 Dados do Usuário:");
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + cpf);
        System.out.println("Telefone: " + telefone);
        System.out.println("Email: " + email);
        if (contaCorrente != null) System.out.println("Conta Corrente: " + contaCorrente.getNumeroConta());
        if (contaPoupanca != null) System.out.println("Conta Poupança: " + contaPoupanca.getNumeroConta());
    }

    public void mostrarSaldos() {
        System.out.println("\n💰 Saldos:");
        if (contaCorrente != null)
            System.out.printf("Conta Corrente (%s): R$ %.2f\n", contaCorrente.getNumeroConta(), contaCorrente.getSaldo());
        if (contaPoupanca != null)
            System.out.printf("Conta Poupança (%s): R$ %.2f\n", contaPoupanca.getNumeroConta(), contaPoupanca.getSaldo());
        System.out.printf("Saldo Total: R$ %.2f\n", getSaldoTotal());
    }

    public void depositar(Scanner scanner) {
        System.out.println("\n💸 Depositar em qual conta?");
        int opc = escolherConta(scanner);
        if (opc == 0) return;

        System.out.print("Valor a depositar: R$ ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        if (valor <= 0) {
            System.out.println("❌ Valor inválido.");
            return;
        }

        if (opc == 1 && contaCorrente != null) {
            contaCorrente.depositar(valor);
            System.out.println("✅ Depósito realizado na Conta Corrente!");
        } else if (opc == 2 && contaPoupanca != null) {
            contaPoupanca.depositar(valor);
            System.out.println("✅ Depósito realizado na Conta Poupança!");
        } else {
            System.out.println("❌ Conta inválida.");
        }
    }

    public void sacar(Scanner scanner) {
        System.out.println("\n🏧 Sacar de qual conta?");
        int opc = escolherConta(scanner);
        if (opc == 0) return;

        System.out.print("Valor a sacar: R$ ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        if (valor <= 0) {
            System.out.println("❌ Valor inválido.");
            return;
        }

        boolean sucesso = false;
        if (opc == 1 && contaCorrente != null) {
            sucesso = contaCorrente.sacar(valor);
            if (sucesso) System.out.println("✅ Saque realizado da Conta Corrente!");
        } else if (opc == 2 && contaPoupanca != null) {
            sucesso = contaPoupanca.sacar(valor);
            if (sucesso) System.out.println("✅ Saque realizado da Conta Poupança!");
        }

        if (!sucesso) System.out.println("❌ Saldo insuficiente ou conta inválida.");
    }

    public void transferirPIX(Scanner scanner, Map<String, Usuario> usuarios) {
        System.out.println("\n🔄 Transferência PIX");
        int opc = escolherConta(scanner);
        if (opc == 0) return;

        System.out.print("Informe o email do destinatário: ");
        String emailDest = scanner.nextLine();
        Usuario destinatario = usuarios.get(emailDest);

        if (destinatario == null) {
            System.out.println("❌ Usuário destinatário não encontrado.");
            return;
        }

        System.out.print("Valor a transferir: R$ ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        if (valor <= 0) {
            System.out.println("❌ Valor inválido.");
            return;
        }

        System.out.print("Confirma a transferência de R$ " + valor + " para " + destinatario.getNome() + "? (s/n): ");
        String confirma = scanner.nextLine();
        if (!confirma.equalsIgnoreCase("s")) {
            System.out.println("❌ Transferência cancelada.");
            return;
        }

        boolean sucesso = false;
        if (opc == 1 && contaCorrente != null) {
            sucesso = contaCorrente.sacar(valor);
        } else if (opc == 2 && contaPoupanca != null) {
            sucesso = contaPoupanca.sacar(valor);
        }

        if (!sucesso) {
            System.out.println("❌ Saldo insuficiente para transferência.");
            return;
        }

        // Deposita na conta corrente do destinatário se existir, senão poupança
        if (destinatario.contaCorrente != null) {
            destinatario.contaCorrente.depositar(valor);
        } else if (destinatario.contaPoupanca != null) {
            destinatario.contaPoupanca.depositar(valor);
        } else {
            System.out.println("❌ Destinatário não tem conta válida para receber.");
            // Devolver dinheiro ao remetente
            if (opc == 1) contaCorrente.depositar(valor);
            else if (opc == 2) contaPoupanca.depositar(valor);
            return;
        }

        System.out.println("✅ Transferência realizada com sucesso!");
    }

    public void verExtrato() {
        System.out.println("\n📜 Extrato da Conta Corrente:");
        if (contaCorrente != null) contaCorrente.mostrarExtrato();
        else System.out.println("Conta Corrente não cadastrada.");

        System.out.println("\n📜 Extrato da Conta Poupança:");
        if (contaPoupanca != null) contaPoupanca.mostrarExtrato();
        else System.out.println("Conta Poupança não cadastrada.");
    }

    private int escolherConta(Scanner scanner) {
        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupança");
        System.out.println("0 - Cancelar");
        System.out.print("Escolha: ");
        int opc = scanner.nextInt();
        scanner.nextLine();
        return opc;
    }
}
