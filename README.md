# InsideBank — Interface JavaFX

Interface grafica gerada a partir do Figma
(`InsideBank - Community`, https://www.figma.com/design/BSXK8Q5Hi0gEjfcnpckVWZ)
e integrada ao servidor bancario concorrente/distribuido ja existente
(`Servidor.java`, `Banco.java`, `Conta.java`, `AtendimentoCliente.java`).

## Estrutura

```
insidebank-javafx/
└── src/main/
    ├── java/
    │   ├── Servidor.java, Banco.java, Conta.java,          <- backend original,
    │   │   AtendimentoCliente.java, Cliente.java,             sem alteracoes
    │   │   TesteBanco.java
    │   └── com/insidebank/
    │       ├── MainApp.java                 <- ponto de entrada JavaFX
    │       ├── controller/
    │       │   ├── LoginController.java
    │       │   └── DashboardController.java
    │       ├── net/
    │       │   ├── BankClient.java          <- fala o protocolo do AtendimentoCliente
    │       │   └── Resultado.java
    │       └── model/
    │           └── Sessao.java              <- estado da sessao logada
    └── resources/
        ├── fxml/login.fxml, dashboard.fxml
        ├── css/theme-dark.css, theme-light.css
        ├── fonts/   
        └── images/ 
```

Em um terminal:
```bash
cd src/main/java
javac Banco.java Conta.java AtendimentoCliente.java Servidor.java Cliente.java TesteBanco.java
java Servidor
```
Isso sobe o servidor em `127.0.0.1:4444` com as 3 contas de teste
(`1/senha123`, `2/senha456`, `3/senha789`), exatamente como no projeto original.

### 4. Rode a interface JavaFX

Em outro terminal, na raiz do projeto:
```bash
mvn clean javafx:run
```

Faça login com a conta `1` e senha `senha123` (ou `2`/`senha456`, `3`/`senha789`).

## Funcionalidades

- **Login** → envia `LOGIN;conta;senha` para o servidor, depois passa por uma
  etapa de **verificacao em 2 fatores** (codigo mostrado em tela - modo
  demonstracao, sem SMS/e-mail real) antes de entrar no Dashboard.
- **Dashboard** → busca o saldo real via `SALDO` assim que a tela abre.
- **Transferir** (botao no card de saldo, quick action ou icone da sidebar) →
  fluxo completo em 3 modais (destino → confirmar → erro), com consulta do
  nome do destinatario (comando novo `NOME`) e chamada real de `TRANSF`.
- **Depositar / Sacar** (quick actions do Dashboard) → modal compartilhado que
  chama `DEPOSITAR`/`SACAR` de verdade.
- **Extrato** (sidebar ou botao "Ver Extrato") → lista o historico real de
  movimentacoes via comando novo `EXTRATO` (cada saque/deposito/transferencia
  agora fica registrado em `Conta.java`).
- **Cadastrar-se** (tela de Login) → cria uma conta nova de verdade via
  comando novo `CADASTRAR` (`Banco.criarConta`, com geracao de ID thread-safe).
- **Configuracoes \| Geral** → alterna entre tema claro e escuro de verdade
  (troca a stylesheet da janela e mostra o numero/nome da propria conta em
  "Minha Conta", via o comando novo `NOME`).
- **Sair da Conta** (icone na sidebar) → envia `LOGOUT` e volta para o Login.

Novos comandos de protocolo adicionados ao `AtendimentoCliente.java` para
viabilizar o que estava acima (documentados com comentarios no proprio
codigo): `EXTRATO`, `CADASTRAR;NOME;SENHA`, `NOME;CONTA`.

As telas de Investimentos, Poupanca, Emprestimo, Cartao e Estatisticas
continuam mostrando o aviso "tela ainda nao implementada" — ver
`ROADMAP_TELAS.md`.


## Proximas etapas (fora do escopo desta entrega)

- Hash de senha (o `pom.xml` ja inclui `jbcrypt` como dependencia, pronto para uso).
- Persistencia em banco de dados (hoje as contas sao fixas no construtor de `Banco.java`).
- Suporte a multiplos bancos.
