# InsideBank — Design Tokens (extraidos do Figma)

Fonte: `https://www.figma.com/design/BSXK8Q5Hi0gEjfcnpckVWZ/InsideBank--Community-`
Extraido via MCP `get_design_context` / `get_metadata` em 4 telas representativas
(Landing Page, Login, Dashboard 1, Configuracoes | Tema Claro), cobrindo os dois
temas (escuro = padrao do app, claro = usado em Configuracoes/Estatisticas).

Os arquivos `src/main/resources/css/theme-dark.css` e `theme-light.css` sao a
implementacao pratica destes tokens. Este documento explica a origem de cada um.

## 1. Cores — Tema Escuro (padrao)

| Token           | Valor                        | Uso                                   |
|-----------------|-------------------------------|----------------------------------------|
| `-bg-base`      | `#000000`                    | Fundo de todas as telas                |
| `-surface`      | `rgba(255,255,255,0.10)`      | Cards, inputs, botoes "glass"          |
| `-surface-strong`| `rgba(255,255,255,0.40)`     | Borda de cards em destaque (promo)     |
| `-border-subtle`| `rgba(255,255,255,0.10)`      | Borda padrao de cards/inputs           |
| `-border-strong`| `rgba(255,255,255,0.40)`      | Borda de destaque / checkbox           |
| `-text-primary` | `#FFFFFF`                    | Texto principal                        |
| `-text-secondary`| `rgba(255,255,255,0.40)`     | Placeholders, legendas, texto mudo     |

## 2. Cores — Tema Claro (Configuracoes / Estatisticas)

| Token            | Valor                     |
|-------------------|---------------------------|
| `-bg-base`        | `#F1F1F1`                 |
| `-surface`        | `#FFFFFF`                 |
| `-text-primary`   | `#000000`                 |
| `-text-secondary` | `rgba(0,0,0,0.40)`         |
| nav pill ativo    | `rgba(0,0,0,0.10)`         |

## 3. Gradiente principal (dourado / CTA)

Aparece em todos os botoes primarios, no card de credito em destaque e no
checkbox marcado. O angulo varia ligeiramente (100.4deg a 154.9deg) dependendo
do elemento no Figma, mas as cores-base sao sempre as mesmas:

```
linear-gradient(~100-155deg, rgb(255,248,83) 0%, rgb(153,149,50 | 187,180,16) 100%)
```

Padronizado no CSS como:
- `-gold-start: #FFF853`
- `-gold-end: #999532`
- `-gold-end-alt: #BBB410` (variante usada no card de credito e em alguns botoes)

## 4. Cores do grafico "Limite da Conta" (Dashboard)

| Categoria | Cor       |
|-----------|-----------|
| Servicos  | `#526EFF` |
| Contas    | `#8E67FF` |
| Produtos  | `#C261FE` |
| Outros    | `rgba(255,255,255,0.10)` (trilho, sem cor propria) |

## 5. Gradientes dos cards promocionais (Dashboard)

| Card                  | Cor de destaque |
|-----------------------|-----------------|
| "Torne-se um Insider" | `#FCF61B`       |
| "Cashback Exclusivo" (1) | `#4FC6D5`    |
| "Cashback Exclusivo" (2) | `#962326`    |

## 6. Tipografia

Duas familias:
- **Inter** (Regular) — logo "InsideBank" e titulos de tela grandes (ex: "Entre com sua Conta").
- **Montserrat** (Regular / Medium / SemiBold / Italic) — todo o resto (labels, botoes, valores, corpo de texto).

| Estilo                        | Fonte               | Tamanho |
|--------------------------------|---------------------|---------|
| Saldo / valores grandes        | Montserrat Regular  | 40px    |
| Titulo de tela (Login)         | Inter Regular       | 36px    |
| Hero da Landing Page            | Montserrat Regular  | 28px    |
| Titulo de secao (Visao Geral)  | Montserrat Regular  | 22px    |
| Logo "InsideBank"               | Inter Regular       | 22px    |
| Titulo de card (promo, cartao) | Montserrat SemiBold | 20px    |
| Labels / botoes / inputs       | Montserrat Regular/Medium | 16px |
| Texto secundario                | Montserrat Regular  | 14px    |
| Texto fino (badges, legendas)  | Montserrat Regular/Medium | 12px |
| Badge "Pub"                     | Montserrat Medium   | 8px     |

Baixe as fontes em https://fonts.google.com/specimen/Montserrat e
https://fonts.google.com/specimen/Inter, e coloque os `.ttf` em
`src/main/resources/fonts/` com os nomes ja referenciados no CSS:
`Montserrat-Regular.ttf`, `Montserrat-Medium.ttf`, `Montserrat-SemiBold.ttf`,
`Inter-Regular.ttf`.

## 7. Raios de borda

| Token         | Valor | Uso                                  |
|----------------|-------|----------------------------------------|
| `-radius-lg`  | 20px  | Cards, botoes grandes, quick actions   |
| `-radius-md`  | 12px  | Pills pequenas ("Transferir", "Esse mes")|
| `-radius-pill`| 24px  | Campo de busca (totalmente arredondado)|
| `-radius-sm`  | 4px   | Checkbox, badge "Pub"                  |

## 8. Sombras

| Sombra                                             | Uso                        |
|------------------------------------------------------|-----------------------------|
| `0px 10px 20px -10px rgba(0,0,0,0.30)`               | Sombra padrao de cards/inputs/botoes |
| `0px 0px 40px 0px rgba(0,0,0,0.50)`                  | Card de credito em destaque |
| `0px 0px 20px rgba(0,0,0,0.25)` (text-shadow)         | Texto sobre foto (Landing Page) |

## 9. Efeitos nao replicaveis 1:1 em JavaFX

O Figma usa `backdrop-blur` (10px / 25px / 50px) em varios elementos "glass"
(botao secundario da Landing Page, quick actions, card de credito claro). O
JavaFX **nao tem um equivalente direto de backdrop-filter/blur do que esta
atras do elemento** (BoxBlur do JavaFX borra o proprio no e seus filhos, nao
o conteudo atras dele). A aproximacao usada foi manter a transparencia
(`rgba(255,255,255,0.10)`) sem o blur real — visualmente muito proximo em um
fundo solido, mas diferente sobre uma foto/gradiente atras.

## 10. Icones

A maioria dos icones do Figma sao SVGs customizados (Frame3, Frame6-8, Vector,
Vector1, Vector2, Vector6, Vector9, Vector12, imgGroup9/10, etc.) que nao
puderam ser baixados automaticamente nesta sessao (ver `ASSETS_TODO.md`). Como
substituto imediato, o projeto usa a biblioteca **Ikonli** (pack Feather —
`org.kordamp.ikonli:ikonli-feather-pack`) com icones de significado equivalente
(ex: `fe-send` para "Transferir via Pix", `fe-download` para "Deposito"). Troque
por `ImageView` com os SVGs reais assim que forem baixados.
