import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContaPoupanca implements Serializable {
    private String numeroConta;
    private double saldo;
    private List<String> extrato;

    public ContaPoupanca(String numeroConta) {
        this.numeroConta = numeroConta;
        this.saldo = 0.0;
        this.extrato = new ArrayList<>();
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        saldo += valor;
        extrato.add(String.format("Depósito: +R$ %.2f", valor));
    }

    public boolean sacar(double valor) {
        if (valor > saldo) return false;
        saldo -= valor;
        extrato.add(String.format("Saque: -R$ %.2f", valor));
        return true;
    }

    public void mostrarExtrato() {
        if (extrato.isEmpty()) {
            System.out.println("Sem movimentações.");
            return;
        }
        for (String mov : extrato) {
            System.out.println(mov);
        }
    }
}
