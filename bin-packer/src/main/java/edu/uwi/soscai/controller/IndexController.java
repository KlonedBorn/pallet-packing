package edu.uwi.soscai.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uwi.soscai.algorithm.generator.BinarySpacePartitioner;
import edu.uwi.soscai.algorithm.packer.GuillotineAlgorithm;
import edu.uwi.soscai.algorithm.packer.FirstFitAlgorithm;
import edu.uwi.soscai.config.Components;
import edu.uwi.soscai.data.Algorithm;
import edu.uwi.soscai.data.PalletSize;
import edu.uwi.soscai.model.Rect;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.util.Map.Entry;

public class IndexController {

    protected static final String ALGORITHM_PROMPT_TEXT = "Select a search algorithm";

    @FXML
    private VBox customSizeBox;

    @FXML
    private Label genResults;

    @FXML
    private FlowPane boxesView;

    @FXML
    private Slider bspMaxDivisionSld;

    @FXML
    private TextField bspMaxDivisionsTf;

    @FXML
    private Slider bspMinLengthSld;

    @FXML
    private TextField bspMinLengthTf;

    @FXML
    private Slider bspMinWidthSld;

    @FXML
    private TextField bspMinWidthTf;

    @FXML
    private Canvas pallet;

    @FXML
    private Slider palletLengthSld;

    @FXML
    private TextField palletLengthTf;

    @FXML
    private ComboBox<PalletSize> palletSelector;

    @FXML
    private Slider palletWidthSld;

    @FXML
    private TextField palletWidthTf;

    @FXML
    private Label palletZoomLb;

    @FXML
    private Slider palletZoomSld;

    @FXML
    private ComboBox<Algorithm> searchAlgorithmSelector;

    @FXML
    private Group scaler;

    @FXML
    private Slider ranCountSld;

    @FXML
    private TextField ranCountTf;

    @FXML
    private Slider ranLengthSld;

    @FXML
    private TextField ranLengthTf;

    @FXML
    private Slider ranWidthSld;

    @FXML
    private TextField ranWidthTf;

    @FXML
    private Slider unitCountSld;

    @FXML
    private TextField unitCountTf;

    @FXML
    private Slider unitLengthSld;

    @FXML
    private TextField unitLengthTf;

    @FXML
    private Slider unitWidthSld;

    @FXML
    private TextField unitWidthTf;

    @FXML
    private TableColumn<Entry<String, Number>, String> statsColumn;

    @FXML
    private TableColumn<Entry<String, Number>, Number> valueColumn;

    @FXML
    private TableView<Entry<String, Number>> infoTable;

    private List<Rect> boxes = new ArrayList<>();

    @FXML
    void onActionGenerateRandomBoxes(ActionEvent event) {
        refresh();
        for (int i = 0; i < ranCountSld.valueProperty().intValue(); i++) {
            double width = Math.max(10.0, ranWidthSld.getValue() * Math.random());
            double length = Math.max(10.0, ranLengthSld.getValue() * Math.random());
            Rect box = new Rect(width, length, Color.BEIGE);
            boxes.add(box);
            boxesView.getChildren().add(box.getView());
            sortBoxesView();
        }
        genResults.setText("Total Boxes: " + boxes.size());
    }

    @FXML
    void onActionGenerateUnitBoxes(ActionEvent event) {
        refresh();
        for (int i = 0; i < unitCountSld.valueProperty().intValue(); i++) {
            Rect box = new Rect(unitWidthSld.getValue(), unitLengthSld.getValue(), Color.BEIGE);
            boxes.add(box);
            boxesView.getChildren().add(box.getView());
            sortBoxesView();
        }
        genResults.setText("Total Boxes: " + boxes.size());
    }

    @FXML
    void onActionClearPallet(ActionEvent event) {
        refresh();
    }

    @FXML
    void onActionGenerateBSPBoxes(ActionEvent event) {
        refresh();
        BinarySpacePartitioner bsp = new BinarySpacePartitioner(
                new BoundingBox(0, 0, palletWidthSld.getValue(), palletLengthSld.getValue()),
                new BoundingBox(0, 0, bspMinWidthSld.getValue(), bspMinLengthSld.getValue()),
                bspMaxDivisionSld.valueProperty().intValue());
        boxes = bsp.generate().stream()
                .map(box -> new Rect(box.getMinX(), box.getMinY(), box.getWidth(),
                        box.getHeight(), Color.BEIGE))
                .collect(Collectors.toList());
        genResults.setText("Total Boxes: " + boxes.size());
        Timeline boxDrawingAnimation = new Timeline();
        for (int i = 0; i < boxes.size(); i++) {
            Rect box = boxes.get(i);
            KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(i), e -> {
                box.draw(pallet.getGraphicsContext2D());
                boxesView.getChildren().add(box.getView());
                sortBoxesView();
            });
            boxDrawingAnimation.getKeyFrames().add(keyFrame);
        }
        boxDrawingAnimation.play();
    }

    private void sortBoxesView() {
        List<Rectangle> sortedRectangles = boxesView.getChildren().stream()
                .filter(node -> node instanceof Rectangle)
                .map(node -> (Rectangle) node)
                .collect(Collectors.toList());
        sortedRectangles.sort((a, b) -> {
            int diff = Double.compare(b.getHeight(), a.getHeight());
            if (diff == 0) {
                diff = Double.compare(a.getWidth(), b.getWidth());
            }
            return diff;
        });
        boxesView.getChildren().clear();
        boxesView.getChildren().addAll(sortedRectangles);
    }

    @FXML
    void onActionStartSearchAlgorithm(ActionEvent event) {
        redraw();
        infoTable.getItems().clear();
        boxes.forEach(b -> b.setSelected(false));
        switch (searchAlgorithmSelector.getSelectionModel().getSelectedItem()) {
            case FIRST_FIT:
                FirstFitAlgorithm algorithm = new FirstFitAlgorithm(pallet,
                        new BoundingBox(0, 0, palletWidthSld.getValue(), palletLengthSld.getValue()));
                Map<String, Number> metrics = algorithm.pack(boxes);
                for (Map.Entry<String, Number> entry : metrics.entrySet()) {
                    infoTable.getItems().add(entry);
                }
                break;
            case GREEDY: {
                BinarySpacePartitioner bsp = new BinarySpacePartitioner(
                        new BoundingBox(0, 0, palletWidthSld.getValue(), palletLengthSld.getValue()),
                        new BoundingBox(0, 0, 10, 10),
                        8);
                List<Rect> boxes = bsp.generate().stream()
                        .map(box -> new Rect(box.getMinX(), box.getMinY(), box.getWidth(),
                                box.getHeight(), Color.BEIGE))
                        .collect(Collectors.toList());
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), evt ->{}));
                showCorrect(timeline, boxes);
                timeline.play();
            }
                break;
            case NEXT_FIT:
                GuillotineAlgorithm algorithm3 = new GuillotineAlgorithm(pallet,
                        new BoundingBox(0, 0, palletWidthSld.getValue(), palletLengthSld.getValue()), true);
                Map<String, Number> metrics3 = algorithm3.pack(boxes);
                for (Map.Entry<String, Number> entry : metrics3.entrySet()) {
                    infoTable.getItems().add(entry);
                }
                break;
            default:
                break;
        }
    }

    private void showCorrect(Timeline timeline, List<Rect> boxes) {
        for (int i = 0; i < boxes.size(); i++) {
            Rect box = boxes.get(i);
            KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(i), e -> {
                box.draw(pallet.getGraphicsContext2D());
                boxesView.getChildren().add(box.getView());
                sortBoxesView();
            });
            timeline.getKeyFrames().add(keyFrame);
        }
    }

    @FXML
    public void initialize() {
        initPalletSelector();
        initAlgorithmSelector();
        bindPalletDimensions();
        bindSliderAndTextFields();
        bindPalletZoom();
        selectDefaultPallet();
        clearBoxesOnPalletSelection();
        statsColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey()));
        valueColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleObjectProperty<Number>(cellData.getValue().getValue()));
    }

    private void initAlgorithmSelector() {
        searchAlgorithmSelector.getItems().addAll(Algorithm.values());
        searchAlgorithmSelector.setConverter(new StringConverter<Algorithm>() {
            @Override
            public String toString(Algorithm algorithm) {
                return (algorithm != null) ? algorithm.getName() : ALGORITHM_PROMPT_TEXT;
            }

            @Override
            public Algorithm fromString(String text) {
                return Algorithm.valueOf(text);
            }
        });
        searchAlgorithmSelector.getSelectionModel().select(null);
    }

    private void initPalletSelector() {
        palletSelector.setConverter(new StringConverter<PalletSize>() {
            @Override
            public String toString(PalletSize pallet) {
                return (pallet != null) ? pallet.getName() : PALLET_SIZE_PROMPT_TEXT;
            }

            @Override
            public PalletSize fromString(String text) {
                return PalletSize.valueOf(text);
            }
        });
        palletSelector.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
            PalletSize selectedPallet = palletSelector.getSelectionModel().getSelectedItem();
            if (selectedPallet != PalletSize.CUSTOM) {
                palletWidthSld.setValue(selectedPallet.getWidth());
                palletLengthSld.setValue(selectedPallet.getLength());
            }
            customSizeBox.setDisable((selectedPallet != PalletSize.CUSTOM));
        });
        palletSelector.setPromptText(PALLET_SIZE_PROMPT_TEXT);
        palletSelector.getItems().addAll(PalletSize.values());
    }

    private void bindPalletDimensions() {
        pallet.widthProperty().bind(palletWidthSld.valueProperty().multiply(CM_TO_PX));
        pallet.heightProperty().bind(palletLengthSld.valueProperty().multiply(CM_TO_PX));
        pallet.widthProperty().addListener((obs, old, nvw) -> redraw());
        pallet.heightProperty().addListener((obs, old, nvw) -> redraw());
    }

    private void bindSliderAndTextFields() {
        Components.bindSliderToTextField(palletWidthSld, palletWidthTf);
        Components.bindSliderToTextField(palletLengthSld, palletLengthTf);
        Components.bindSliderToTextField(bspMinWidthSld, bspMinWidthTf);
        Components.bindSliderToTextField(bspMinLengthSld, bspMinLengthTf);
        Components.bindSliderToTextField(bspMaxDivisionSld, bspMaxDivisionsTf);
        Components.bindSliderToTextField(ranWidthSld, ranWidthTf);
        Components.bindSliderToTextField(ranLengthSld, ranLengthTf);
        Components.bindSliderToTextField(ranCountSld, ranCountTf);
        Components.bindSliderToTextField(unitWidthSld, unitWidthTf);
        Components.bindSliderToTextField(unitLengthSld, unitLengthTf);
        Components.bindSliderToTextField(unitCountSld, unitCountTf);
        Components.bindSliderToLabel(palletZoomSld, palletZoomLb, "%.1f%%");
    }

    private void bindPalletZoom() {
        scaler.scaleXProperty().bind(palletZoomSld.valueProperty());
        scaler.scaleYProperty().bind(palletZoomSld.valueProperty());
    }

    private void selectDefaultPallet() {
        palletSelector.getSelectionModel().select(PalletSize.AMERICAN_SQUARE_PALLET_48X48);
    }

    private void clearBoxesOnPalletSelection() {
        palletSelector.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> refresh());
    }

    private void refresh() {
        redraw();
        boxes.clear();
        genResults.setText("");
        infoTable.getItems().clear();
        boxesView.getChildren().clear();
    }

    private void redraw() {
        GraphicsContext gc = pallet.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.clearRect(0, 0, pallet.getWidth(), pallet.getHeight());
        gc.strokeRect(0, 0, pallet.getWidth(), pallet.getHeight());
    }

    public static final double CM_TO_PX = 4.0;
    private static final String PALLET_SIZE_PROMPT_TEXT = "Select a standard pallet size";
    private static final Duration DELAY_BETWEEN_BOXES = Duration.millis(200);
}
