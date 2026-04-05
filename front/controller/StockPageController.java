@FXML private TableView<JsonNode> stockTable;
@FXML private TableColumn<JsonNode, String> colName;
@FXML private TableColumn<JsonNode, String> colType;
@FXML private TableColumn<JsonNode, String> colUnit;
@FXML private TableColumn<JsonNode, String> colValue;
@FXML private TableColumn<JsonNode, String> colCurrent;
@FXML private TableColumn<JsonNode, String> colMin;
@FXML private TableColumn<JsonNode, String> colMax;
@FXML private Label statusLabel;

@FXML
public void initialize() {
    colName.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("productName").asText()));
    colType.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("productType").asText()));
    colUnit.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("measurementUnit").asText()));
    colValue.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("unitValue").asText()));
    colCurrent.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("currentStockQuantity").asText()));
    colMin.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("minStockQuantity").asText()));
    colMax.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().path("maxStockQuantity").asText()));

    stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);