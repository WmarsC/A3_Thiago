import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class SistemaBanco {
    private static final String ARQUIVO_USUARIOS = "usuarios.ser";
    private static Map<String, Usuario> usuarios = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        carregarUsuarios();
        menuPrincipal();
        salvarUsuarios();
    }

    private static void menuPrincipal() {
        int opcao;
        do {
            desenharCaixa(" \uD83C\uDFE6 Bem-vindo ao Banco Digital ✨");
            System.out.println("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557");
            System.out.println("\u2551 1\uFE0F\u20E3 Criar conta             \u2551");
            System.out.println("\u2551 2\uFE0F\u20E3 Fazer login             \u2551");
            System.out.println("\u2551 0\uFE0F\u20E3 Sair                    \u2551");
            System.out.println("\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D");
            System.out.print("👉 Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> criarConta();
                case 2 -> fazerLogin();
                case 0 -> System.out.println("👋 Até logo e volte sempre!");
                default -> System.out.println("\u001B[31m❌ Opção inválida!\u001B[0m");
            }

            esperar(1000);
        } while (opcao != 0);
    }

    private static void desenharCaixa(String titulo) {
        String linha = "═".repeat(titulo.length());
        System.out.println("╔" + linha + "╗");
        System.out.println("║" + titulo + "║");
        System.out.println("╚" + linha + "╝");
    }

    private static void esperar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void criarConta() {
        System.out.println("\n✨ Vamos criar sua conta!");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        String senhaHash = hashSenha(senha);
        Usuario novoUsuario = new Usuario(nome, senhaHash, cpf, telefone, email);

        System.out.print("Deseja criar conta corrente? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            novoUsuario.criarContaCorrente(UUID.randomUUID().toString());
        }

        System.out.print("Deseja criar conta poupança? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            novoUsuario.criarContaPoupanca(UUID.randomUUID().toString());
        }

        usuarios.put(email, novoUsuario);
        salvarUsuarios();
        System.out.println("✅ Conta criada com sucesso! Seja bem-vindo! 🥳\n");
        esperar(1000);
    }

    private static void fazerLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Usuario usuario = usuarios.get(email);
        if (usuario == null) {
            System.out.println("\u001B[31m❌ Usuário não encontrado.\u001B[0m");
            return;
        }

        int tentativas = 0;
        while (tentativas < 3) {
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            if (usuario.getSenhaHash().equals(hashSenha(senha))) {
                menuUsuario(usuario);
                return;
            } else {
                System.out.println("\u001B[31m❌ Senha incorreta.\u001B[0m");
                tentativas++;
            }
        }

        System.out.println("\u001B[31m🚫 Número de tentativas excedido.\u001B[0m");
    }

    private static String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar senha.");
        }
    }

    private static void salvarUsuarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_USUARIOS))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários.");
        }
    }

    private static void carregarUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_USUARIOS))) {
            usuarios = (Map<String, Usuario>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Erro ao carregar usuários.");
        }
    }

    private static void menuUsuario(Usuario usuario) {
        int opcao;
        do {
            desenharCaixa("\uD83D\uDC64 Olá, " + usuario.getNome() + " | Saldo Total: R$" + usuario.getSaldoTotal());
            System.out.println("╔══════════════════════════════════╗");
            System.out.println("║ 1️⃣ Ver dados do usuário          ║");
            System.out.println("║ 2️⃣ Ver saldo                     ║");
            System.out.println("║ 3️⃣ Depositar                     ║");
            System.out.println("║ 4️⃣ Sacar                         ║");
            System.out.println("║ 5️⃣ Transferência PIX             ║");
            System.out.println("║ 6️⃣ Ver extrato                   ║");
            System.out.println("║ 0️⃣ Sair                          ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("👉 Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> usuario.mostrarDados();
                case 2 -> usuario.mostrarSaldos();
                case 3 -> usuario.depositar(scanner);
                case 4 -> usuario.sacar(scanner);
                case 5 -> usuario.transferirPIX(scanner, usuarios);
                case 6 -> usuario.verExtrato();
                case 0 -> System.out.println("👋 Saindo da conta...");
                default -> System.out.println("❌ Opção inválida!");
            }
            esperar(1200);
        } while (opcao != 0);
    }
}
