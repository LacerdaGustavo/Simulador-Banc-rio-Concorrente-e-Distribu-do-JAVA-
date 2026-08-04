# InsideBank — Roadmap de Telas (varredura completa do Figma)

Varredura feita em `0:1` (pagina "Main", unica pagina do arquivo) via
`get_metadata`. Foram encontrados **44 frames**, organizados abaixo por fluxo.
O Figma tem visivelmente **mais telas do que comandos hoje suportados pelo
servidor** (que so entende LOGIN, SACAR, DEPOSITAR, TRANSF, SALDO, LOGOUT) —
ou seja, Investimentos, Emprestimo, Poupanca, Cartao, Estatisticas e as
sub-telas de Configuracoes vao precisar de novos comandos no protocolo/servidor
quando forem implementadas de verdade.

Status:
- ✅ Implementado nesta etapa (FXML + Controller + CSS)
- 🟡 Catalogado (design extraido/entendido), aguardando priorizacao
- ⬜ Apenas identificado na varredura (nome + node id), ainda nao inspecionado em detalhe

| # | Tela (nome no Figma)                        | Node id   | Status |
|---|-----------------------------------------------|-----------|--------|
| 1 | Landing Page                                  | 134:113   | 🟡 (design extraido) |
| 2 | Login \| 1                                     | 115:2     | ✅ |
| 3 | Login \| 2FA                                   | 131:55    | ✅ (layout aproximado - rate limit do Figma, ver nota abaixo) |
| 4 | Cadastrar \| 1                                 | 131:93    | ✅ (layout aproximado - rate limit do Figma) |
| 5 | Cadastrar \| 2                                 | 131:147   | ➖ (fundido em 1 unica tela) |
| 6 | Dashboard 1                                    | 131:40    | ✅ |
| 7 | Dashboard 2                                    | 190:163   | ⬜ (variante) |
| 8 | Dashboard \| Interacao Usuario                 | 147:844   | ⬜ (estado de UI) |
| 9 | Dashboard \| Interacao Notificacao             | 147:417   | ⬜ (estado de UI) |
| 10| Dashboard \| Interacao Pesquisa                | 148:1360  | ⬜ (estado de UI) |
| 11| Dashboard \| Interacao Cartao                  | 152:1891  | ⬜ (estado de UI) |
| 12| Dashboard \| Transferencia (3x)                | 190:714, 190:1150, 190:1342 | ✅ **extraido com fidelidade total** (3 modais: destino, confirmar, erro) |
| 13| Dashboard \| Deposito (3x)                     | 190:935, 190:1542, 190:1939 | ✅ (modal compartilhado com Saque - layout aproximado, rate limit) |
| 14| Dashboard \| Emprestimo 1 / 2 / (sem sufixo)    | 190:2324, 193:2760, 193:2997 | ⬜ fora do escopo combinado |
| 15| Dashboard \| Poupanca (3x)                     | 193:3387, 193:3580, 194:3784 | ⬜ fora do escopo combinado |
| 16| Dashboard \| Investimentos (3x)                | 195:3987, 195:4213, 195:4538 | ⬜ fora do escopo combinado |
| 17| Dashboard \| Extrato                           | 202:5883  | ✅ (layout aproximado - rate limit do Figma) |
| 18| Dashboard \| Cartao (5x)                       | 203:6192, 203:6416, 203:6645, 203:6859, 203:7060 | ⬜ fora do escopo combinado |
| 19| Estatisticas                                    | 88:2      | 🟡 (fora do escopo combinado nesta rodada) |
| 20| Estatisticas \| Interacao Duracao               | 190:341   | ⬜ (estado de UI) |
| 21| Estatisticas (variantes sem sufixo, 2x)         | 202:5263, 202:5699 | ⬜ |
| 22| Estatisticas \| Tema Claro                      | 202:5014  | ⬜ |
| 23| Configuracoes \| Geral                          | 88:260    | ✅ (toggle de tema funcional de verdade) |
| 24| Configuracoes \| Minha Conta 1                  | 101:8     | ✅ (layout aproximado - rate limit do Figma) |
| 25| Configuracoes \| Minha Conta 2                  | 136:323   | ➖ (fundido no painel Minha Conta) |
| 26| Configuracoes \| Privacidade e Seguranca        | 122:22    | ✅ (painel informativo - layout aproximado) |
| 27| Configuracoes \| Central de Ajuda               | 134:42    | ✅ (painel informativo - layout aproximado) |
| 28| Configuracoes \| Tema Claro (Dashboard/Estat./Config, 3x) | 202:4836, 202:5014, 202:5169 | ✅ Configuracoes extraida como referencia de tema claro |

## Sobre o rate limit do Figma nesta rodada

O plano Starter do MCP do Figma foi atingido no meio da extracao. As telas
marcadas acima como "layout aproximado" foram construidas seguindo
rigorosamente o design system ja extraido (`THEME_DESIGN_TOKENS.md`: cores,
gradientes, tipografia, cards, modais) mas **sem confirmar pixel-a-pixel**
contra o node exato do Figma. Assim que o limite resetar ou o plano for
atualizado, e possivel re-extrair esses nodes especificos e ajustar o layout
fino sem tocar na logica ja funcional.

## O que ficou 100% funcional nesta rodada (nao e so visual)

- **Transferencia** completa: escolher conta de destino (ou atalho para as 3
  contas de teste), digitar valor, confirmar com nome do destinatario
  (comando novo `NOME`), e tratamento de erro - tudo via `TRANSF` de verdade.
- **Deposito e Saque**: modal compartilhado, chama `DEPOSITAR`/`SACAR` de verdade.
- **Extrato**: comando novo `EXTRATO` + historico persistido em `Conta.java`
  (cada saque/deposito/transferencia agora fica registrado em memoria).
- **Cadastro**: comando novo `CADASTRAR` + `Banco.criarConta(...)` (ids gerados
  com `AtomicInteger`, mapa de contas agora e `ConcurrentHashMap`).
- **Login 2FA**: etapa extra apos o login (codigo mostrado em tela, modo
  demonstracao - nao ha SMS/e-mail real).
- **Configuracoes \| Geral**: alterna tema claro/escuro de verdade (troca a
  stylesheet da cena atual e persiste a escolha em `Sessao` para as proximas
  telas).

*(a contagem exata soma 44 frames; alguns agrupamentos acima juntam variantes
de um mesmo fluxo em uma linha para ficar legivel — a lista bruta com todos os
44 node ids individuais esta no historico da extracao e pode ser re-consultada
a qualquer momento).*

## O que já está pronto (este pacote)

- **Fundacao do projeto**: Maven + JavaFX + Ikonli, estrutura de pastas.
- **Design tokens completos**: cores (dois temas), gradientes, tipografia,
  raios, sombras — `THEME_DESIGN_TOKENS.md` + `theme-dark.css` / `theme-light.css`.
- **Tela de Login** (pixel-a-pixel, node 115:2) — conectada de verdade ao
  `Servidor.java` via `BankClient`.
- **Dashboard (Visao Geral)** (pixel-a-pixel, node 131:40) — saldo real via
  comando `SALDO`, navegacao para as demais telas (com aviso de "ainda nao
  implementada" onde aplicavel), logout real via `LOGOUT`.
- **Camada de rede reutilizavel** (`BankClient`, `Resultado`, `Sessao`) pronta
  para as proximas telas chamarem `SACAR`, `DEPOSITAR` e `TRANSF`.

## Sugestao de proxima etapa

Como o servidor atual so fala LOGIN/SACAR/DEPOSITAR/TRANSF/SALDO/LOGOUT, as
telas com maior retorno imediato (reaproveitam 100% do backend existente) sao,
em ordem sugerida:

1. **Transferencia** (fluxo de 3 telas: destinatario → valor → confirmacao) — usa `TRANSF`.
2. **Extrato** — hoje o servidor nao guarda historico de transacoes; precisa de
   um novo comando (ex: `EXTRATO`) e o `Banco`/`Conta` passariam a registrar um
   log de movimentacoes.
3. **Deposito e Saque** (o Figma so tem tela de Deposito; Saque pode reaproveitar
   o mesmo layout) — usam `DEPOSITAR` e `SACAR`.
4. **Configuracoes \| Geral** (toggle de tema claro/escuro e notificacoes) — 100%
   client-side, nao precisa de mudanca no servidor.
5. **Cadastro e Login 2FA** — precisam de um comando novo no protocolo (ex:
   `CADASTRAR;senha;...`) e de persistencia (hoje as contas sao fixas no
   construtor do `Banco.java`).
6. **Investimentos / Poupanca / Emprestimo / Cartao / Estatisticas** — sao
   features novas que ainda nao existem no backend; exigem modelagem de dados
   nova (fora do escopo de "so interface").

Me diga qual bloco quer que eu implemente a seguir e eu sigo pelo mesmo padrao
(FXML fiel ao Figma + Controller + integracao real com o socket).
