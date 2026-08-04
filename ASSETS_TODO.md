# Assets pendentes de substituição

## Por que os assets não vieram prontos no projeto

O ambiente onde o Claude processou esta conversa não tem acesso de rede para
baixar arquivos binários (imagens/SVGs) do Figma diretamente — só consegue ler
código/texto das respostas do MCP do Figma. As URLs das imagens foram capturadas
com sucesso (uma por asset), mas o download em si precisa ser feito **pelo seu
computador**, que tem acesso à internet normalmente.

Isso está resolvido em `download-assets.sh` (na raiz do projeto): rode-o e todas
as imagens/ícones já catalogados caem em `src/main/resources/images/`.

## O que está usando placeholder no código agora

| Elemento                                   | Onde                        | Placeholder atual                          | Asset real |
|---------------------------------------------|------------------------------|-----------------------------------------------|------------|
| Ilustração lateral da tela de Login          | `login.fxml`                 | Retângulo com gradiente + ícone genérico       | `login-foto-lateral.jpg` |
| Ilustração de fundo da Landing Page          | (tela ainda não construída)  | —                                              | `landing-foto-fundo.jpg` |
| Ícones da sidebar e da grade "Acesso Rápido" | `dashboard.fxml`             | Ícones Ikonli (Feather) equivalentes           | `icone-pix.svg`, `icone-investir.svg`, `icone-boleto.svg`, `icone-poupanca.svg`, `icone-deposito.svg`, `icone-emprestimo.svg` |
| Sidebar completa (grafismo de fundo)         | `dashboard.fxml`             | `VBox` estilizado via CSS                      | `sidebar-dark.svg` |
| Avatar do usuário                            | `dashboard.fxml`              | Círculo com gradiente + ícone                  | `avatar-usuario-dark.png` |
| Bandeira do cartão (Mastercard)               | `dashboard.fxml`             | Omitido                                        | `dashboard-bandeira-cartao.png` |
| Fotos dos 3 cards promocionais                | `dashboard.fxml`              | Só o gradiente de cor, sem foto                | `dashboard-promo-bg-1/2/3.jpg` |

## Como trocar um placeholder por um asset real

Depois de rodar `download-assets.sh`, troque, por exemplo, no `login.fxml`:

```xml
<!-- antes -->
<StackPane layoutX="720" layoutY="46" prefWidth="678" prefHeight="932" style="...">
    <FontIcon iconLiteral="fth-image" iconSize="64" .../>
</StackPane>

<!-- depois -->
<ImageView layoutX="720" layoutY="46" fitWidth="678" fitHeight="932" preserveRatio="false">
    <image><Image url="@../images/login-foto-lateral.jpg"/></image>
</ImageView>
```

(lembre de importar `javafx.scene.image.Image` e `javafx.scene.image.ImageView`
no topo do FXML, e aplicar um `Rectangle`/`clip` se quiser preservar as bordas
arredondadas do design original).

## Ícones sem asset baixado (apenas identificados, não extraídos em detalhe)

Os frames que ainda não passaram por `get_design_context` (ver lista 🟡/⬜ em
`ROADMAP_TELAS.md`) certamente têm outros ícones/imagens próprios que ainda não
foram catalogados. Cada novo `get_design_context` que rodarmos vai gerar uma
nova leva de URLs — é só repetir o mesmo processo (adicionar ao
`download-assets.sh`, ou eu já entrego o script atualizado quando avançarmos
para essas telas).
