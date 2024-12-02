# Aplicativo de Controle de Plantas

## Descrição do Aplicativo

Este aplicativo tem como objetivo o controle e gerenciamento de plantas e de regas das mesmas em um ambiente doméstico. Ele permite que o usuário registre, visualize, edite e exclua informações sobre suas plantas, como apelido, espécie, local, notas adicionais e status de irrigação.

## Funcionalidades

- **Cadastro e edição de plantas**: O usuário pode adicionar e editar novas plantas.
- **Cadastro e edição de espécies**: O usuário pode adicionar e editar novas espécies.
- **Cadastro e edição de ambientes**: O usuário pode adicionar e editar novos ambientes.
- **Exclusão**: O usuário pode remover plantas, ambientes e espécies do sistema quando necessário.
- **Visualização detalhada**: Exibição de detalhes da planta, incluindo a última rega e a previsão de próxima rega.
- **Gestão de regas**: O usuário pode registrar ao regar uma planta e o aplicativo calculará o intervalo para a próxima rega.
- **Notificações**: O sistema alertará o usuário quando uma planta precisar ser regada, ao concluir o intervalo desde a última rega cadastrada. 

## Entidades

### 1. **Plant**
- **Descrição**: Representa uma planta registrada no sistema.
- **Atributos**:
  - **id**: Identificador único da planta.
  - **nickname**: Nome atribuído pelo usuário para identificar a planta.
  - **specieId**: Tipo ou espécie da planta.
  - **placeId**: Local onde a planta está situada (ex: "Sala de Estar", "Varanda").
  - **notes**: Notas adicionais fornecidas pelo usuário.
  - **lastWatering**: Data e hora da última rega realizada.
  
### 2. **Specie**
- **Descrição**: Representa as espécies de plantas disponíveis para seleção ao cadastrar uma nova planta.
- **Atributos**:
  - **id**: Identificador único da espécie.
  - **name**: Nome científico ou popular da espécie.
  - **description**: Descrição da espécie (opcional).
  - **waterFrequency**: Quantidade de dias entre as regas indica para a espécie.
  - **careInstructions**: Recomendações especiais de cuidados com a espécie (opcional).

 ### 3. **Place**
 - **Descrição**: Representa os ambientes ou locais nos quais uma planta pode estar.
 - **Atributos**:
   - **id**: Identificador único do ambiente ou local.
   - **name**: Nome do local.
   - **description**: Descrição de atributos do ambiente ou local (opcional).

## Relacionamentos

- **Planta -> Espécie**: Cada planta registrada possui uma espécie associada. O relacionamento é de **muitos para um**, ou seja, muitas plantas podem pertencer à mesma espécie.
- **Planta -> Local**: A planta está associada a um local (dentro da casa, por exemplo). Esse relacionamento é **muitos para um**, onde várias plantas podem estar no mesmo local.


