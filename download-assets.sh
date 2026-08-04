#!/bin/bash
# ==============================================================================
# InsideBank - Download dos assets extraidos do Figma
#
# POR QUE ESTE SCRIPT EXISTE:
# O ambiente onde esta conversa com o Claude roda NAO TEM acesso a rede para
# baixar arquivos binarios (bloqueio de saida/egress). Por isso, as imagens e
# icones do Figma nao puderam ser salvos automaticamente no projeto.
#
# As URLs abaixo foram geradas pelo Figma no momento da extracao (01/08/2026)
# e SAO VALIDAS POR APENAS 7 DIAS. Rode este script o quanto antes.
#
# USO:
#   chmod +x download-assets.sh
#   ./download-assets.sh
# ==============================================================================

set -e
DEST="src/main/resources/images"
mkdir -p "$DEST"

baixar() {
  local nome="$1"
  local url="$2"
  echo "Baixando $nome..."
  curl -sL -o "$DEST/$nome" "$url" || echo "  -> FALHOU (URL pode ter expirado): $nome"
}

# ---------- Landing Page (node 134:113) ----------
baixar "landing-foto-fundo.jpg"      "https://www.figma.com/api/mcp/asset/696b306d-fb94-4e67-aa6a-9c8066f75378"
baixar "logo-icone.svg"              "https://www.figma.com/api/mcp/asset/7011db8d-c1dc-465d-8276-5e668201e8c3"

# ---------- Login (node 115:2) ----------
baixar "login-foto-lateral.jpg"      "https://www.figma.com/api/mcp/asset/a8e1c5cd-a4fd-4b08-b8ec-5518b7ad5eb2"
baixar "login-logo-icone.svg"        "https://www.figma.com/api/mcp/asset/50090baf-4976-4361-a1dd-e4f721a28235"
baixar "login-checkbox-check.svg"    "https://www.figma.com/api/mcp/asset/87a5b1db-eb08-43a5-a582-1b20c4d5e726"
baixar "login-divisor.svg"           "https://www.figma.com/api/mcp/asset/e55a6e83-cd46-4b46-8e75-359f61f8babe"

# ---------- Dashboard 1 (node 131:40) ----------
baixar "dashboard-bandeira-cartao.png"   "https://www.figma.com/api/mcp/asset/2d50bd6e-ea24-4efa-bf3a-06c875b1bb40"
baixar "dashboard-promo-bg-1.jpg"        "https://www.figma.com/api/mcp/asset/405420a1-81c2-47ab-b430-ad7ec3c7424d"
baixar "dashboard-promo-bg-2.jpg"        "https://www.figma.com/api/mcp/asset/9737890d-f494-476c-bc3c-882de27ac2ca"
baixar "dashboard-promo-bg-3.jpg"        "https://www.figma.com/api/mcp/asset/748da7cb-fb02-4b60-8e32-733e6f0480ac"
baixar "dashboard-cartao-textura.svg"    "https://www.figma.com/api/mcp/asset/6454737e-210a-4668-b006-daa149e8aef5"
baixar "dashboard-cartao-contactless.svg" "https://www.figma.com/api/mcp/asset/a4464c17-184c-4ef1-a228-6f0f59ee9c29"
baixar "icone-pix.svg"                   "https://www.figma.com/api/mcp/asset/ce7786f6-d476-4511-a6c0-450a8336539c"
baixar "icone-investir.svg"              "https://www.figma.com/api/mcp/asset/246f98da-ec04-480c-9726-1d2abc128f11"
baixar "icone-boleto.svg"                "https://www.figma.com/api/mcp/asset/37d10c5f-e8de-4e4b-9179-c3e3c081fb11"
baixar "icone-poupanca.svg"              "https://www.figma.com/api/mcp/asset/8f9c91bb-1f5a-4ed4-a982-d75386f4507d"
baixar "icone-deposito.svg"              "https://www.figma.com/api/mcp/asset/06797748-4d97-4311-abfe-b9ce428fba1a"
baixar "icone-emprestimo.svg"            "https://www.figma.com/api/mcp/asset/3f1a61af-dbef-4d67-8f4d-ffac4f337aaf"
baixar "quick-action-9.svg"              "https://www.figma.com/api/mcp/asset/cdc6badc-d6b0-4c99-9cf8-024d85e0e247"
baixar "icone-receitas.svg"              "https://www.figma.com/api/mcp/asset/37596a57-b876-41a5-8549-b5dec20e9a12"
baixar "icone-despesas.svg"              "https://www.figma.com/api/mcp/asset/cdbe3750-7afa-4569-a5a2-6771f8461cb0"
baixar "seta-dropdown.svg"               "https://www.figma.com/api/mcp/asset/176dcdbd-f397-49aa-af51-da48836b8cc0"
baixar "legenda-dot-servicos.svg"        "https://www.figma.com/api/mcp/asset/6fd23c10-80f0-485e-891a-259bba2d42de"
baixar "legenda-dot-contas.svg"          "https://www.figma.com/api/mcp/asset/f5b9b3f7-a75f-44e1-acfd-6eba1bd9c5c5"
baixar "legenda-dot-produtos.svg"        "https://www.figma.com/api/mcp/asset/81f46610-2546-4dc0-be73-3eeffc479e0b"
baixar "legenda-dot-outros.svg"          "https://www.figma.com/api/mcp/asset/87470fa5-9c51-4e17-9ba7-80e20b9e8d9f"
baixar "sidebar-dark.svg"                "https://www.figma.com/api/mcp/asset/179d6991-b89e-42f8-9bef-4acfcae472a9"
baixar "avatar-usuario-dark.png"         "https://www.figma.com/api/mcp/asset/d2986996-a9b5-4100-b39a-fb590ae59e2e"
baixar "icone-busca-dark.svg"            "https://www.figma.com/api/mcp/asset/cb81a8c7-6d30-450a-a71a-0bd5786cf1c3"
baixar "icone-notificacao-dark.svg"      "https://www.figma.com/api/mcp/asset/2427ae84-da8b-41ce-a925-87ea1ea7dfc5"
baixar "seta-saiba-mais.svg"             "https://www.figma.com/api/mcp/asset/1a3d92b7-972c-41a1-8bf2-bbe8038168f0"

# ---------- Configuracoes | Tema Claro (node 202:5169) ----------
baixar "config-avatar-1.png"          "https://www.figma.com/api/mcp/asset/8cbc018e-dd9e-4ab6-a7a2-f58b97ae43c6"
baixar "config-avatar-2.png"          "https://www.figma.com/api/mcp/asset/b998b4a5-ab59-4176-b64a-a902b7eb9976"
baixar "config-icone-tema.svg"        "https://www.figma.com/api/mcp/asset/3c6dd665-6a20-4e99-b947-5307f5698c9d"
baixar "config-seta-1.svg"            "https://www.figma.com/api/mcp/asset/ec86addf-1872-4b03-95d6-146de5a9b563"
baixar "config-divisor.svg"           "https://www.figma.com/api/mcp/asset/c0118ee4-40dc-47cd-af05-5a92c023822e"
baixar "sidebar-light.svg"            "https://www.figma.com/api/mcp/asset/de6e0618-fa41-4383-95aa-c0280be40e14"
baixar "avatar-usuario-light.png"     "https://www.figma.com/api/mcp/asset/91a08fe0-ebbb-4b29-b24d-6561dc2ee4c8"
baixar "icone-busca-light.svg"        "https://www.figma.com/api/mcp/asset/bd0f3380-785f-4b2c-ba7a-69d560cbc939"
baixar "icone-notificacao-light.svg"  "https://www.figma.com/api/mcp/asset/a8a7f96f-fa5b-461a-b07b-87510006a77b"
baixar "config-divisor-vertical.svg"  "https://www.figma.com/api/mcp/asset/8c08c86a-fde5-4546-b239-beb027a44afb"

echo ""
echo "Concluido. Verifique $DEST - qualquer arquivo com 0 bytes indica URL expirada;"
echo "nesse caso, peça ao Claude para rodar get_design_context novamente no node"
echo "correspondente (ver numeros de node no THEME_DESIGN_TOKENS.md / ROADMAP_TELAS.md)."
