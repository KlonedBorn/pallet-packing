package edu.uwi.soscai.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.uwi.soscai.algorithm.generator.BinarySpacePartitioner;
import edu.uwi.soscai.algorithm.packing.GreedyAlgorithm;
import edu.uwi.soscai.config.Components;
import edu.uwi.soscai.data.PackingAlgorithm;
import edu.uwi.soscai.data.PalletSize;
import edu.uwi.soscai.model.Rect;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.StringConverter;

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
    private Slider bspMinHeightSld;

    @FXML
    private TextField bspMinHeightTf;

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
    private ComboBox<PackingAlgorithm> searchAlgorithmSelector;

    @FXML
    private Group scaler;

    private List<Rect> boxes = new ArrayList<>();

    @FXML
    void onActionClearPallet(ActionEvent event) {
        refresh();
    }

    @FXML
    void onActionGenerateBSPBoxes(ActionEvent event) {
        refresh();
        BinarySpacePartitioner bsp = new BinarySpacePartitioner(
                new BoundingBox(0, 0, palletWidthSld.getValue(), palletLengthSld.getValue()),
                new BoundingBox(0, 0, bspMinWidthSld.getValue(), bspMinHeightSld.getValue()),
                bspMaxDivisionSld.valueProperty().intValue());
        boxes = bsp.generate().stream().map(box -> new Rect(scaleToPixels(box))).collect(Collectors.toList());
        genResults.setText("Total Boxes: " + boxes.size());
        Timeline boxDrawingAnimation = new Timeline();
        for (int i = 0; i < boxes.size(); i++) {
            Rect box = boxes.get(i);
            KeyFrame keyFrame = new KeyFrame(DELAY_BETWEEN_BOXES.multiply(i), e -> {
                box.draw(pallet);
                boxesView.getChildren().add(box.getShape());
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

    private Bounds scaleToPixels(Bounds box) {
        return new BoundingBox(box.getMinX() * CM_TO_PX, box.getMinY() * CM_TO_PX,
                box.getWidth() * CM_TO_PX, box.getHeight() * CM_TO_PX);
    }

    @FXML
    void onActionStartSearchAlgorithm(ActionEvent event) {
        redraw();
        GreedyAlgorithm.BinPacker packer = new GreedyAlgorithm.BinPacker((int) (palletWidthSld.getValue() * CM_TO_PX),
                (int) (palletLengthSld.getValue() * CM_TO_PX));
        packer.packRectangles(boxes);
        for (Rect box : boxes) {
            box.draw(pallet);
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
    }

    private void initAlgorithmSelector() {
        searchAlgorithmSelector.getItems().addAll(PackingAlgorithm.values());
        searchAlgorithmSelector.setConverter(new StringConverter<PackingAlgorithm>() {
            @Override
            public String toString(PackingAlgorithm algorithm) {
                return (algorithm != null) ? algorithm.getName() : ALGORITHM_PROMPT_TEXT;
            }

            @Override
            public PackingAlgorithm fromString(String text) {
                return PackingAlgorithm.valueOf(text);
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
        Components.bindSliderToTextField(bspMinHeightSld, bspMinHeightTf);
        Components.bindSliderToTextField(bspMaxDivisionSld, bspMaxDivisionsTf);
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
        boxesView.getChildren().clear();
        boxes.clear();
        genResults.setText("");
        redraw();
    }

    private void redraw() {
        GraphicsContext gc = pallet.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.clearRect(0, 0, pallet.getWidth(), pallet.getHeight());
        gc.strokeRect(0, 0, pallet.getWidth(), pallet.getHeight());
    }

    private static final double CM_TO_PX = 4.0;
    private static final String PALLET_SIZE_PROMPT_TEXT = "Select a standard pallet size";
    private static final Duration DELAY_BETWEEN_BOXES = Duration.millis(200);
}
